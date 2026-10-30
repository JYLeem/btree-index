package edu.btree.test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 테스트 결과 기록기. 모든 테스트는 (입력, 예상 결과, 실제 결과) 를 남기고,
 * 예상과 실제의 문자열이 정확히 같을 때만 PASS 다. 기준을 느슨하게 비교하는 경로는 없다.
 */
public final class TestRecorder {

    public static final class Result {
        public final String suite;
        public final String id;
        public final String name;
        public final String input;
        public final String expected;
        public final String actual;
        public final String status; // PASS / FAIL / NOT RUN

        Result(String suite, String id, String name, String input, String expected, String actual, String status) {
            this.suite = suite;
            this.id = id;
            this.name = name;
            this.input = input;
            this.expected = expected;
            this.actual = actual;
            this.status = status;
        }
    }

    private final List<Result> results = new ArrayList<>();
    private String suite = "";

    public void beginSuite(String name) {
        suite = name;
        System.out.println();
        System.out.println("=== " + name + " ===");
    }

    /** @return 통과 여부 */
    public boolean record(String id, String name, String input, Object expected, Object actual) {
        String e = String.valueOf(expected);
        String a = String.valueOf(actual);
        boolean pass = e.equals(a);
        results.add(new Result(suite, id, name, input, e, a, pass ? "PASS" : "FAIL"));
        System.out.printf("  [%s] %-12s %s%n", pass ? "PASS" : "FAIL", id, name);
        if (!pass) {
            System.out.println("         입력: " + clip(input, 300));
            System.out.println("         예상: " + clip(e, 300));
            System.out.println("         실제: " + clip(a, 300));
        }
        return pass;
    }

    /** 실행하지 못한 스위트를 NOT RUN 으로 남긴다 (통과로 세지 않음). */
    public void notRun(String suiteName, String reason) {
        results.add(new Result(suiteName, "-", "스위트 미실행", "-", "-", reason, "NOT RUN"));
        System.out.println();
        System.out.println("=== " + suiteName + " === NOT RUN: " + reason);
    }

    public List<Result> results() { return Collections.unmodifiableList(results); }

    public long count(String status) {
        long c = 0;
        for (Result r : results) if (r.status.equals(status)) c++;
        return c;
    }

    public List<Result> failures() {
        List<Result> out = new ArrayList<>();
        for (Result r : results) if (!r.status.equals("PASS")) out.add(r);
        return out;
    }

    /** 결과 전체를 마크다운 표로 저장한다. */
    public void writeMarkdown(Path file, String title, List<String> headerLines) throws IOException {
        if (file.getParent() != null) Files.createDirectories(file.getParent());
        try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            w.write("# " + title + "\n\n");
            for (String line : headerLines) w.write(line + "\n");
            w.write("\n| 스위트 | ID | 테스트 | 입력 | 예상 결과 | 실제 결과 | 결과 |\n");
            w.write("|---|---|---|---|---|---|---|\n");
            for (Result r : results) {
                w.write("| " + cell(r.suite) + " | " + cell(r.id) + " | " + cell(r.name) + " | " + cell(r.input)
                        + " | " + cell(r.expected) + " | " + cell(r.actual) + " | **" + r.status + "** |\n");
            }
        }
    }

    private static String cell(String s) {
        return clip(s, 400).replace("|", "\\|").replace("\n", " ");
    }

    static String clip(String s, int max) {
        if (s == null) return "null";
        return s.length() <= max ? s : s.substring(0, max) + "…(" + s.length() + "자)";
    }
}
