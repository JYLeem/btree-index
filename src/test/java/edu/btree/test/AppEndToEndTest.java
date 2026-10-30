package edu.btree.test;

import edu.btree.db.DbIndexApp;
import edu.btree.fs.FsIndexApp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * 10. 애플리케이션 E2E 테스트: 샘플 명령 파일로 두 프로그램의 main 을 실행하고,
 * 손으로 작성한 기대 출력 파일(samples/*-expected.txt)과 줄 단위로 비교한다.
 * 작업 디렉터리는 프로젝트 루트여야 한다.
 */
final class AppEndToEndTest {
    private AppEndToEndTest() {
    }

    interface App {
        void main(String[] args);
    }

    static void run(TestRecorder r, long seed) {
        r.beginSuite("10. 애플리케이션 E2E 테스트");
        Path outDir = Paths.get("build", "e2e");
        try {
            Files.createDirectories(outDir);
        } catch (IOException e) {
            r.record("E2E-SETUP", "출력 폴더 생성", outDir.toString(), "성공", e.toString());
            return;
        }
        compare(r, "E2E-DB", DbIndexApp::main, Paths.get("samples", "db-commands.txt"),
                Paths.get("samples", "db-expected.txt"), outDir.resolve("db-output.txt"));
        compare(r, "E2E-FS", FsIndexApp::main, Paths.get("samples", "fs-commands.txt"),
                Paths.get("samples", "fs-expected.txt"), outDir.resolve("fs-output.txt"));

        Path missingOut = outDir.resolve("missing-output.txt");
        try {
            Files.deleteIfExists(missingOut);
        } catch (IOException ignored) {
            // 아래 검사에서 드러난다
        }
        String err = captureErr(() -> DbIndexApp.main(new String[]{"samples/does-not-exist.txt", missingOut.toString()}));
        r.record("E2E-ERR1", "입력 파일이 없으면 오류 메시지 출력, 출력 파일 생성 안 함", "DbIndexApp samples/does-not-exist.txt",
                "오류 메시지 출력 / 출력 파일 없음",
                (err.startsWith("[오류] 파일을 찾을 수 없습니다") ? "오류 메시지 출력" : "메시지: " + err.trim())
                        + " / " + (Files.exists(missingOut) ? "출력 파일 있음" : "출력 파일 없음"));
        String err2 = captureErr(() -> FsIndexApp.main(new String[]{"samples/fs-commands.txt", missingOut.toString(), "1"}));
        r.record("E2E-ERR2", "잘못된 최소 차수 인자 처리", "FsIndexApp ... 1",
                "[오류] 최소 차수는 2 이상 65536 이하의 정수여야 합니다.", err2.trim());
        String err3;
        try {
            err3 = captureErr(() -> DbIndexApp.main(new String[]{"samples/db-commands.txt", missingOut.toString(), "2000000000"}));
        } catch (RuntimeException e) {
            err3 = "프로그램 비정상 종료: " + e.getClass().getSimpleName();
        }
        r.record("E2E-ERR3", "범위를 넘는 최소 차수 인자도 비정상 종료 없이 처리 (R-002)", "DbIndexApp ... 2000000000",
                "[오류] 최소 차수는 2 이상 65536 이하의 정수여야 합니다.", err3.trim());
    }

    private static void compare(TestRecorder r, String id, App app, Path in, Path expectedFile, Path out) {
        List<String> expected;
        List<String> actual;
        try {
            Files.deleteIfExists(out);
            app.main(new String[]{in.toString(), out.toString(), "3"});
            expected = Files.readAllLines(expectedFile, StandardCharsets.UTF_8);
            actual = Files.exists(out) ? Files.readAllLines(out, StandardCharsets.UTF_8) : java.util.Collections.<String>emptyList();
        } catch (IOException e) {
            r.record(id + "-IO", "샘플 실행", in.toString(), "입출력 성공", e.toString());
            return;
        }
        r.record(id + "-N", "출력 줄 수", in + " → " + out, expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            r.record(String.format("%s-L%02d", id, i + 1), "출력 줄 일치", in + " 출력 " + (i + 1) + "번째 줄",
                    expected.get(i), i < actual.size() ? actual.get(i) : "(없음)");
        }
    }

    private static String captureErr(Runnable action) {
        PrintStream original = System.err;
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        try (PrintStream ps = new PrintStream(buf, true, "UTF-8")) {
            System.setErr(ps);
            action.run();
        } catch (IOException e) {
            return e.toString();
        } finally {
            System.setErr(original);
        }
        return new String(buf.toByteArray(), StandardCharsets.UTF_8);
    }
}
