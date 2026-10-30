package edu.btree.fs;

import edu.btree.core.BTree;
import edu.btree.core.Entry;
import edu.btree.core.TreeSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 파일 시스템 경로 인덱스 (교육용 모델, 메모리 내 시뮬레이션 — 실제 파일은 건드리지 않음).
 *
 *   경로 B-Tree:  "/home/leem/a.txt" → fileId
 *   inode 테이블: fileId → FileMetadata
 *
 * 경로를 사전순으로 정렬하므로 한 디렉터리의 하위 항목은 "dir/" 접두사 구간에 모인다.
 * → 디렉터리 목록 = 접두사 범위 검색. 중복 경로는 허용하지 않는다.
 */
public final class FileSystemIndex {
    public static final long ROOT_ID = 1;

    private final BTree<String, Long> pathIndex;
    private final Map<Long, FileMetadata> inodeTable = new HashMap<>();
    private long nextId = ROOT_ID + 1;
    private long clock;

    public FileSystemIndex(int minDegree) {
        this.pathIndex = new BTree<>(minDegree);
        inodeTable.put(ROOT_ID, new FileMetadata(ROOT_ID, "/", FileType.DIRECTORY, 0, "root", clock++));
        pathIndex.insert("/", ROOT_ID);
    }

    /** mkdir */
    public FsStatus mkdir(String path, String owner) {
        return create(path, FileType.DIRECTORY, 0, owner);
    }

    /** 파일/디렉터리 등록: 상위 디렉터리가 있어야 하고 같은 경로가 없어야 한다. */
    public FsStatus create(String path, FileType type, long size, String owner) {
        if (!PathRules.isValid(path) || path.equals("/")) return FsStatus.INVALID_PATH;
        if (type == null || size < 0 || owner == null || owner.trim().isEmpty()
                || (type == FileType.DIRECTORY && size != 0)) {
            return FsStatus.INVALID_ARGUMENT;
        }
        if (pathIndex.contains(path)) return FsStatus.ALREADY_EXISTS;
        FileMetadata parent = lookup(PathRules.parent(path));
        if (parent == null) return FsStatus.PARENT_NOT_FOUND;
        if (parent.type() != FileType.DIRECTORY) return FsStatus.NOT_A_DIRECTORY;

        long id = nextId++;
        inodeTable.put(id, new FileMetadata(id, path, type, size, owner, clock++));
        pathIndex.insert(path, id);
        return FsStatus.OK;
    }

    /** stat: 경로로 메타데이터 검색 (없거나 잘못된 경로면 null) */
    public FileMetadata lookup(String path) {
        if (!PathRules.isValid(path)) return null;
        Long id = pathIndex.search(path);
        return id == null ? null : inodeTable.get(id);
    }

    /** 파일 ID 로 검색 (inode 테이블 해시 조회) */
    public FileMetadata lookupById(long fileId) {
        return inodeTable.get(fileId);
    }

    /** unlink / rmdir: 디렉터리는 비어 있을 때만 */
    public FsStatus delete(String path) {
        if (!PathRules.isValid(path)) return FsStatus.INVALID_PATH;
        if (path.equals("/")) return FsStatus.ROOT_PROTECTED;
        FileMetadata meta = lookup(path);
        if (meta == null) return FsStatus.NOT_FOUND;
        if (meta.type() == FileType.DIRECTORY && hasDescendant(path)) return FsStatus.DIRECTORY_NOT_EMPTY;
        Long id = pathIndex.delete(path);
        inodeTable.remove(id);
        return FsStatus.OK;
    }

    /** 접두사 구간에서 1개만 찾아 하위 항목 존재 여부 확인: O(t·log n) */
    private boolean hasDescendant(String dir) {
        String prefix = PathRules.childPrefix(dir);
        return !pathIndex.rangeSearch(prefix, false, PathRules.prefixUpperBound(prefix), false, 1).isEmpty();
    }

    /**
     * ls: 직속 항목만, 이름 오름차순.
     * 접두사 구간을 한 항목씩 전진하다가 더 깊은 경로("dir/sub/x")를 만나면
     * 그 하위 디렉터리 구간 전체를 건너뛴다 → 비용이 직속 항목 수에 비례.
     */
    public List<FileMetadata> listDirectory(String dir) {
        requireDirectory(dir);
        String prefix = PathRules.childPrefix(dir);
        String upper = PathRules.prefixUpperBound(prefix);
        List<FileMetadata> out = new ArrayList<>();
        String cursor = prefix;
        boolean cursorInclusive = false; // prefix 자체(루트의 경우 "/")는 제외
        while (true) {
            List<Entry<String, Long>> next = pathIndex.rangeSearch(cursor, cursorInclusive, upper, false, 1);
            if (next.isEmpty()) break;
            String path = next.get(0).key();
            int slash = path.indexOf('/', prefix.length());
            if (slash < 0) {
                out.add(inodeTable.get(next.get(0).value()));
                cursor = path;
                cursorInclusive = false;
            } else {
                cursor = PathRules.prefixUpperBound(path.substring(0, slash + 1));
                cursorInclusive = true;
                if (cursor == null) break;
            }
        }
        return out;
    }

    /** find: 모든 하위 항목 (자신 제외), 경로 오름차순 */
    public List<FileMetadata> listRecursive(String dir) {
        requireDirectory(dir);
        String prefix = PathRules.childPrefix(dir);
        List<FileMetadata> out = new ArrayList<>();
        for (Entry<String, Long> e : pathIndex.rangeSearch(prefix, false, PathRules.prefixUpperBound(prefix), false,
                Integer.MAX_VALUE)) {
            out.add(inodeTable.get(e.value()));
        }
        return out;
    }

    /** 경로 사전순 범위 [from, to) */
    public List<FileMetadata> rangeByPath(String from, String toExclusive) {
        if (from == null || toExclusive == null) throw new IllegalArgumentException("범위 경계가 null 입니다.");
        List<FileMetadata> out = new ArrayList<>();
        for (Entry<String, Long> e : pathIndex.rangeSearch(from, true, toExclusive, false, Integer.MAX_VALUE)) {
            out.add(inodeTable.get(e.value()));
        }
        return out;
    }

    private void requireDirectory(String dir) {
        String err = PathRules.check(dir);
        if (err != null) throw new IllegalArgumentException("잘못된 경로: " + err);
        FileMetadata meta = lookup(dir);
        if (meta == null) throw new IllegalArgumentException("디렉터리가 없습니다: " + dir);
        if (meta.type() != FileType.DIRECTORY) throw new IllegalArgumentException("디렉터리가 아닙니다: " + dir);
    }

    public int entryCount() { return pathIndex.size(); }
    public int inodeCount() { return inodeTable.size(); }
    public TreeSnapshot<String, Long> snapshot() { return pathIndex.snapshot(); }
    public BTree<String, Long> tree() { return pathIndex; }
}
