package edu.btree.fs;

/** 경로 문법 검사와 사전순 범위 계산 */
public final class PathRules {
    public static final int MAX_NAME_LENGTH = 255;
    public static final int MAX_PATH_LENGTH = 4096;

    private PathRules() {
    }

    /** 올바른 절대 경로면 null, 아니면 오류 이유 */
    public static String check(String path) {
        if (path == null || path.isEmpty()) return "경로가 비어 있습니다";
        if (path.charAt(0) != '/') return "절대 경로('/'로 시작)여야 합니다";
        if (path.equals("/")) return null;
        if (path.length() > MAX_PATH_LENGTH) return "경로가 너무 깁니다";
        if (path.endsWith("/")) return "경로 끝에 '/'를 붙일 수 없습니다";
        for (String part : path.substring(1).split("/", -1)) {
            if (part.isEmpty()) return "빈 구성요소('//')가 있습니다";
            if (part.equals(".") || part.equals("..")) return "'.', '..' 는 지원하지 않습니다";
            if (part.length() > MAX_NAME_LENGTH) return "이름이 " + MAX_NAME_LENGTH + "자를 넘습니다";
            for (int i = 0; i < part.length(); i++) {
                char c = part.charAt(i);
                if (c == '\0' || Character.isWhitespace(c)) return "NUL·공백 문자는 쓸 수 없습니다";
            }
        }
        return null;
    }

    public static boolean isValid(String path) {
        return check(path) == null;
    }

    /** 상위 경로 ("/a" → "/", "/a/b" → "/a") */
    public static String parent(String path) {
        int idx = path.lastIndexOf('/');
        return idx == 0 ? "/" : path.substring(0, idx);
    }

    /** 디렉터리의 하위 항목이 공통으로 갖는 접두사 ("/" → "/", "/a" → "/a/") */
    public static String childPrefix(String dir) {
        return dir.equals("/") ? "/" : dir + "/";
    }

    /**
     * prefix 로 시작하는 모든 문자열 s 에 대해 prefix ≤ s < 반환값 인 배타적 상한.
     * 마지막 문자를 1 증가시킨다. 마지막 문자가 '￿' 면 떼고 반복, 모두 떼면 null(상한 없음).
     */
    public static String prefixUpperBound(String prefix) {
        int end = prefix.length();
        while (end > 0 && prefix.charAt(end - 1) == Character.MAX_VALUE) end--;
        if (end == 0) return null;
        return prefix.substring(0, end - 1) + (char) (prefix.charAt(end - 1) + 1);
    }
}
