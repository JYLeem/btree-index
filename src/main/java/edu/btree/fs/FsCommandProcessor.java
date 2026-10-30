package edu.btree.fs;

import java.util.ArrayList;
import java.util.List;

/**
 * FS 명령 한 줄을 실행하고 결과 문자열을 돌려준다.
 * <pre>
 * MKDIR path owner | CREATE path size owner | STAT path | DELETE path | LS dir | FIND dir | RANGE from to
 * </pre>
 */
public final class FsCommandProcessor {
    private final FileSystemIndex fs;
    private int ok;
    private int fail;
    private int error;

    public FsCommandProcessor(FileSystemIndex fs) {
        this.fs = fs;
    }

    public String process(String line) {
        if (line == null) return null;
        String trimmed = line.trim();
        if (trimmed.isEmpty() || trimmed.startsWith("#")) return null;
        String[] p = trimmed.split("\\s+");
        String cmd = p[0].toUpperCase();
        try {
            String result;
            switch (cmd) {
                case "MKDIR":
                    expectArgs(p, 3, "MKDIR <path> <owner>");
                    result = status(fs.mkdir(p[1], p[2]), p[1]);
                    break;
                case "CREATE":
                    expectArgs(p, 4, "CREATE <path> <size> <owner>");
                    result = status(fs.create(p[1], FileType.FILE, parseLong(p[2], "size"), p[3]), p[1]);
                    break;
                case "STAT": {
                    expectArgs(p, 2, "STAT <path>");
                    FileMetadata m = fs.lookup(p[1]);
                    result = m == null ? "FAIL NOT_FOUND " + p[1] : "OK " + m;
                    break;
                }
                case "DELETE":
                    expectArgs(p, 2, "DELETE <path>");
                    result = status(fs.delete(p[1]), p[1]);
                    break;
                case "LS":
                    expectArgs(p, 2, "LS <dir>");
                    result = listing(fs.listDirectory(p[1]));
                    break;
                case "FIND":
                    expectArgs(p, 2, "FIND <dir>");
                    result = listing(fs.listRecursive(p[1]));
                    break;
                case "RANGE":
                    expectArgs(p, 3, "RANGE <from> <to>");
                    result = listing(fs.rangeByPath(p[1], p[2]));
                    break;
                default:
                    throw new IllegalArgumentException("알 수 없는 명령: " + p[0]);
            }
            if (result.startsWith("OK")) ok++; else fail++;
            return result;
        } catch (IllegalArgumentException e) {
            error++;
            return "ERROR " + e.getMessage();
        }
    }

    private static String status(FsStatus s, String path) {
        return s == FsStatus.OK ? "OK " + path : "FAIL " + s + " " + path;
    }

    private static String listing(List<FileMetadata> list) {
        List<String> paths = new ArrayList<>();
        for (FileMetadata m : list) paths.add(m.path());
        return "OK " + paths.size() + " " + paths;
    }

    private static void expectArgs(String[] p, int n, String usage) {
        if (p.length != n) throw new IllegalArgumentException("인자 개수 오류, 사용법: " + usage);
    }

    private static long parseLong(String s, String name) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(name + " 는 정수여야 합니다: " + s);
        }
    }

    public String summary() {
        return "# summary ok=" + ok + " fail=" + fail + " error=" + error + " entries=" + fs.entryCount()
                + " indexHeight=" + fs.tree().height();
    }
}
