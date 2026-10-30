package edu.btree.test;

import edu.btree.fs.FileMetadata;
import edu.btree.fs.FileSystemIndex;
import edu.btree.fs.FileType;
import edu.btree.fs.FsCommandProcessor;
import edu.btree.fs.FsStatus;
import edu.btree.fs.PathRules;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

/** 사례 B(파일 시스템 인덱스) 기능 테스트 — 기대 출력은 손으로 정한 값 또는 브루트포스 */
final class FsModuleTest {
    private FsModuleTest() {
    }

    static void run(TestRecorder r, long seed) {
        r.beginSuite("8. FS 인덱스 모듈 기능 테스트");

        FsCommandProcessor p = new FsCommandProcessor(new FileSystemIndex(2));
        String[][] script = {
                {"MKDIR /home leem", "OK /home"},
                {"MKDIR /home/leem leem", "OK /home/leem"},
                {"CREATE /home/leem/b.txt 10 leem", "OK /home/leem/b.txt"},
                {"CREATE /home/leem/b-x 5 leem", "OK /home/leem/b-x"},
                {"MKDIR /home/leem/b leem", "OK /home/leem/b"},
                {"CREATE /home/leem/b/c 1 leem", "OK /home/leem/b/c"},
                {"CREATE /home/leem/b0 1 leem", "OK /home/leem/b0"},
                {"LS /home/leem", "OK 4 [/home/leem/b, /home/leem/b-x, /home/leem/b.txt, /home/leem/b0]"},
                {"FIND /home", "OK 6 [/home/leem, /home/leem/b, /home/leem/b-x, /home/leem/b.txt, /home/leem/b/c, /home/leem/b0]"},
                {"LS /", "OK 1 [/home]"},
                {"STAT /home/leem/b.txt", "OK /home/leem/b.txt FILE id=4 size=10 owner=leem"},
                {"STAT /home/leem/zzz", "FAIL NOT_FOUND /home/leem/zzz"},
                {"CREATE /home/leem/b.txt 1 leem", "FAIL ALREADY_EXISTS /home/leem/b.txt"},
                {"CREATE /tmp/x 1 leem", "FAIL PARENT_NOT_FOUND /tmp/x"},
                {"CREATE /home/leem/b.txt/y 1 leem", "FAIL NOT_A_DIRECTORY /home/leem/b.txt/y"},
                {"CREATE home/x 1 leem", "FAIL INVALID_PATH home/x"},
                {"CREATE /home//x 1 leem", "FAIL INVALID_PATH /home//x"},
                {"CREATE /home/../x 1 leem", "FAIL INVALID_PATH /home/../x"},
                {"CREATE /home/x -3 leem", "FAIL INVALID_ARGUMENT /home/x"},
                {"DELETE /home/leem/b", "FAIL DIRECTORY_NOT_EMPTY /home/leem/b"},
                {"DELETE /home/leem/b/c", "OK /home/leem/b/c"},
                {"DELETE /home/leem/b", "OK /home/leem/b"},
                {"DELETE /home/leem/b", "FAIL NOT_FOUND /home/leem/b"},
                {"DELETE /", "FAIL ROOT_PROTECTED /"},
                {"RANGE /home/leem/b /home/leem/b0", "OK 2 [/home/leem/b-x, /home/leem/b.txt]"},
                {"LS /home/leem/b.txt", "ERROR 디렉터리가 아닙니다: /home/leem/b.txt"},
                {"LS /nope", "ERROR 디렉터리가 없습니다: /nope"},
                {"CREATE /a 1", "ERROR 인자 개수 오류, 사용법: CREATE <path> <size> <owner>"},
                {"CREATE /a x leem", "ERROR size 는 정수여야 합니다: x"},
                {"MOVE /a /b", "ERROR 알 수 없는 명령: MOVE"},
        };
        for (int i = 0; i < script.length; i++) {
            r.record(String.format("FS-C%02d", i + 1), "명령 처리기 출력", script[i][0], script[i][1], p.process(script[i][0]));
        }

        r.record("FS-P01", "경로 규칙 유틸리티",
                "prefixUpperBound(\"/a/\"), prefixUpperBound(\"\\uffff\"), parent(\"/a\"), parent(\"/a/b\"), 이름 255자/256자",
                "/a0 null / /a true false",
                PathRules.prefixUpperBound("/a/") + " " + PathRules.prefixUpperBound("\uffff") + " "
                        + PathRules.parent("/a") + " " + PathRules.parent("/a/b") + " "
                        + PathRules.isValid("/" + repeat('n', 255)) + " " + PathRules.isValid("/" + repeat('n', 256)));

        // 무작위 디렉터리 트리를 브루트포스와 비교
        Random rnd = new Random(seed + 99);
        FileSystemIndex fs = new FileSystemIndex(2);
        TreeMap<String, FileType> all = new TreeMap<>();
        all.put("/", FileType.DIRECTORY);
        List<String> dirs = new ArrayList<>();
        dirs.add("/");
        String statusMismatch = null;
        for (int i = 0; i < 3_000; i++) {
            String parent = dirs.get(rnd.nextInt(dirs.size()));
            String path = PathRules.childPrefix(parent) + randomName(rnd);
            FileType type = rnd.nextInt(3) == 0 ? FileType.DIRECTORY : FileType.FILE;
            FsStatus expected = all.containsKey(path) ? FsStatus.ALREADY_EXISTS : FsStatus.OK;
            FsStatus actual = fs.create(path, type, type == FileType.FILE ? 10 : 0, "u");
            if (actual != expected && statusMismatch == null) statusMismatch = "create " + path + " " + actual + "≠" + expected;
            if (actual == FsStatus.OK) {
                all.put(path, type);
                if (type == FileType.DIRECTORY) dirs.add(path);
            }
        }
        List<String> keys = new ArrayList<>(all.keySet());
        for (int i = 0; i < 1_200; i++) {
            String path = keys.get(rnd.nextInt(keys.size()));
            FsStatus expected;
            if (path.equals("/")) expected = FsStatus.ROOT_PROTECTED;
            else if (!all.containsKey(path)) expected = FsStatus.NOT_FOUND;
            else if (all.get(path) == FileType.DIRECTORY && !bruteDescendants(all, path).isEmpty()) {
                expected = FsStatus.DIRECTORY_NOT_EMPTY;
            } else expected = FsStatus.OK;
            FsStatus actual = fs.delete(path);
            if (actual != expected && statusMismatch == null) statusMismatch = "delete " + path + " " + actual + "≠" + expected;
            if (expected == FsStatus.OK) {
                all.remove(path);
                dirs.remove(path);
            }
        }
        r.record("FS-M01", "무작위 create 3,000 / delete 1,200 의 결과 코드", "이름 알파벳 'ab-.0z'(‘/’ 인접 문자 포함), t=2, seed=" + (seed + 99),
                "모두 기대값과 일치", statusMismatch == null ? "모두 기대값과 일치" : "불일치: " + statusMismatch);
        r.record("FS-M02", "경로 항목 수 = inode 수 = 기대 항목 수", "위와 동일",
                all.size() + "/" + all.size(), fs.entryCount() + "/" + fs.inodeCount());

        String lsMismatch = null;
        String findMismatch = null;
        for (String d : dirs) {
            if (lsMismatch == null && !paths(fs.listDirectory(d)).equals(bruteChildren(all, d))) lsMismatch = d;
            if (findMismatch == null && !paths(fs.listRecursive(d)).equals(bruteDescendants(all, d))) findMismatch = d;
        }
        r.record("FS-M03", "모든 디렉터리의 LS 결과 = 브루트포스", "디렉터리 " + dirs.size() + "개",
                "전부 일치", lsMismatch == null ? "전부 일치" : "불일치: " + lsMismatch);
        r.record("FS-M04", "모든 디렉터리의 FIND 결과 = 브루트포스", "디렉터리 " + dirs.size() + "개",
                "전부 일치", findMismatch == null ? "전부 일치" : "불일치: " + findMismatch);
        String idMismatch = null;
        for (String path : all.keySet()) {
            FileMetadata m = fs.lookup(path);
            if (m == null || fs.lookupById(m.fileId()) != m) {
                idMismatch = path;
                break;
            }
        }
        r.record("FS-M05", "경로 검색과 파일 ID 검색이 같은 메타데이터를 가리킴", "모든 경로",
                "일치", idMismatch == null ? "일치" : "불일치: " + idMismatch);
        r.record("FS-M06", "작업 후 경로 인덱스 B-Tree 불변식", "위와 동일", "위반 0건",
                InvariantChecker.summary(InvariantChecker.check(fs.snapshot(), (k, v) -> v != null)));
    }

    private static String repeat(char c, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append(c);
        return sb.toString();
    }

    private static String randomName(Random rnd) {
        String alphabet = "ab-.0z";
        int len = 1 + rnd.nextInt(3);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) sb.append(alphabet.charAt(rnd.nextInt(alphabet.length())));
        String s = sb.toString();
        return (s.equals(".") || s.equals("..")) ? s + "x" : s;
    }

    private static List<String> paths(List<FileMetadata> list) {
        List<String> out = new ArrayList<>();
        for (FileMetadata m : list) out.add(m.path());
        return out;
    }

    private static List<String> bruteChildren(TreeMap<String, FileType> all, String dir) {
        List<String> out = new ArrayList<>();
        for (String p : all.keySet()) if (!p.equals("/") && PathRules.parent(p).equals(dir)) out.add(p);
        return out;
    }

    private static List<String> bruteDescendants(TreeMap<String, FileType> all, String dir) {
        String prefix = PathRules.childPrefix(dir);
        List<String> out = new ArrayList<>();
        for (String p : all.keySet()) if (!p.equals("/") && p.startsWith(prefix)) out.add(p);
        return out;
    }
}
