package edu.btree.test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 전체 테스트 실행기.
 * 사용법: java edu.btree.test.TestMain [라벨=run] [seed=20261024] [결과폴더=test-results]
 *
 * 무한 실행 방지: 전체 시간 상한 300초. 상한을 넘으면 남은 스위트는 NOT RUN 으로 기록한다.
 * 실패 또는 NOT RUN 이 하나라도 있으면 종료 코드 1.
 */
public final class TestMain {
    private static final long TIME_LIMIT_SECONDS = 300;

    interface Suite {
        void run(TestRecorder r, long seed);
    }

    private TestMain() {
    }

    public static void main(String[] args) throws IOException {
        String label = args.length > 0 ? args[0] : "run";
        long seed = args.length > 1 ? Long.parseLong(args[1]) : 20261024L;
        Path outDir = Paths.get(args.length > 2 ? args[2] : "test-results");

        Map<String, Suite> suites = new LinkedHashMap<>();
        suites.put("1. Invariant checker test", InvariantCheckerTest::run);
        suites.put("2. Differential test", DifferentialTest::run);
        suites.put("3. Stateful property-based test", StatefulPropertyTest::run);
        suites.put("4. Edge case test", EdgeCaseTest::run);
        suites.put("5. Structural coverage test", StructuralCoverageTest::run);
        suites.put("6. Adversarial sequence test", AdversarialSequenceTest::run);
        suites.put("7. DB 인덱스 모듈 기능 테스트", DbModuleTest::run);
        suites.put("8. FS 인덱스 모듈 기능 테스트", FsModuleTest::run);
        suites.put("9. 회귀 테스트", RegressionTest::run);
        suites.put("10. 애플리케이션 E2E 테스트", AppEndToEndTest::run);

        TestRecorder recorder = new TestRecorder();
        long start = System.nanoTime();
        long deadline = start + TIME_LIMIT_SECONDS * 1_000_000_000L;
        List<String> timing = new ArrayList<>();
        System.out.println("B-Tree 테스트 실행: label=" + label + ", seed=" + seed + ", 시간 상한 " + TIME_LIMIT_SECONDS + "초");

        for (Map.Entry<String, Suite> s : suites.entrySet()) {
            if (System.nanoTime() > deadline) {
                recorder.notRun(s.getKey(), "전체 시간 상한(" + TIME_LIMIT_SECONDS + "초) 초과");
                timing.add("- " + s.getKey() + ": NOT RUN");
                continue;
            }
            long t0 = System.nanoTime();
            try {
                s.getValue().run(recorder, seed);
            } catch (Throwable e) {
                recorder.beginSuite(s.getKey());
                recorder.record("CRASH", "스위트 실행 중 예외 없이 완료", "-", "예외 없음", e.toString());
                e.printStackTrace(System.out);
            }
            timing.add(String.format("- %s: %.2f초", s.getKey(), (System.nanoTime() - t0) / 1e9));
        }

        double total = (System.nanoTime() - start) / 1e9;
        long pass = recorder.count("PASS");
        long fail = recorder.count("FAIL");
        long notRun = recorder.count("NOT RUN");
        List<String> header = new ArrayList<>();
        header.add(String.format("- 실행 라벨: `%s`, seed: `%d`, 총 소요: %.2f초", label, seed, total));
        header.add(String.format("- 결과: **PASS %d / FAIL %d / NOT RUN %d** (총 %d건)", pass, fail, notRun, pass + fail + notRun));
        header.add("- Java: " + System.getProperty("java.version"));
        header.add("");
        header.add("## 스위트별 소요 시간");
        header.addAll(timing);
        if (fail + notRun > 0) {
            header.add("");
            header.add("## 실패 / 미실행 목록");
            for (TestRecorder.Result r : recorder.failures()) {
                header.add("- [" + r.status + "] " + r.suite + " / " + r.id + " " + r.name + " — 실제: "
                        + TestRecorder.clip(r.actual, 300));
            }
        }
        Path file = outDir.resolve(label + ".md");
        recorder.writeMarkdown(file, "테스트 결과: " + label, header);

        System.out.println();
        System.out.printf("총 PASS %d / FAIL %d / NOT RUN %d (%.2f초) → %s%n", pass, fail, notRun, total, file.toAbsolutePath());
        for (TestRecorder.Result r : recorder.failures()) {
            System.out.println("  [" + r.status + "] " + r.id + " " + r.name);
        }
        System.exit(fail + notRun > 0 ? 1 : 0);
    }
}
