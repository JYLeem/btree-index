package edu.btree.fs;

/** inode 테이블에 저장되는 파일 메타데이터 (불변 객체) */
public final class FileMetadata {
    private final long fileId;
    private final String path;
    private final FileType type;
    private final long size;
    private final String owner;
    private final long createdSeq; // 논리 시계 (결정적 테스트를 위해 실제 시간 대신 사용)

    public FileMetadata(long fileId, String path, FileType type, long size, String owner, long createdSeq) {
        this.fileId = fileId;
        this.path = path;
        this.type = type;
        this.size = size;
        this.owner = owner;
        this.createdSeq = createdSeq;
    }

    public long fileId() { return fileId; }
    public String path() { return path; }
    public FileType type() { return type; }
    public long size() { return size; }
    public String owner() { return owner; }
    public long createdSeq() { return createdSeq; }

    public String name() {
        return path.equals("/") ? "/" : path.substring(path.lastIndexOf('/') + 1);
    }

    @Override
    public String toString() {
        return path + " " + type + " id=" + fileId + " size=" + size + " owner=" + owner;
    }
}
