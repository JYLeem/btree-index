package edu.btree.test;

import java.util.Random;

/**
 * 2. Differential test — 기준 모델: java.util.TreeMap(값) + java.util.TreeSet(키 집합)
 * 매 연산의 반환값을 비교하고, 64연산마다 불변식과 중위 순회 키 = TreeSet 을 확인한다.
 */
final class DifferentialTest {
    private DifferentialTest() {
    }

    static void run(TestRecorder r, long seed) {
        r.beginSuite("2. Differential test");
        int[] degrees = {2, 3, 4, 7, 16};
        int[] domains = {64, 5_000, Integer.MAX_VALUE};
        int opsPerRun = 20_000;
        int seedsPerCase = 2;
        int caseNo = 0;
        for (int t : degrees) {
            for (int domain : domains) {
                caseNo++;
                String divergence = null;
                long ops = 0;
                int finalSize = 0;
                for (int s = 0; s < seedsPerCase && divergence == null; s++) {
                    Random rnd = new Random(seed * 31 + t * 1_000L + domain + s);
                    ModelHarness h = new ModelHarness(t, 64);
                    for (int i = 0; i < opsPerRun && h.divergence == null; i++) {
                        int k = key(rnd, domain);
                        int p = rnd.nextInt(100);
                        if (p < 40) h.insert(k);
                        else if (p < 75) h.delete(k);
                        else if (p < 90) h.search(k);
                        else randomRange(h, rnd, domain);
                    }
                    h.ok();
                    divergence = h.divergence;
                    ops += h.ops();
                    finalSize = h.tree.size();
                }
                String dom = domain == Integer.MAX_VALUE ? "전체 int" : "0~" + (domain - 1);
                r.record(String.format("DIFF-%02d", caseNo), "t=" + t + ", 키 " + dom + " 무작위 연산이 TreeMap/TreeSet 과 일치",
                        "삽입40/삭제35/검색15/범위10%, 시드 " + seedsPerCase + "개 × " + opsPerRun + "회, seed=" + seed,
                        "모든 반환값·키 집합 일치",
                        divergence == null ? "모든 반환값·키 집합 일치" : "불일치 " + divergence
                                + " (실행 " + ops + "회, 마지막 크기 " + finalSize + ")");
            }
        }
    }

    static int key(Random rnd, int domain) {
        return domain == Integer.MAX_VALUE ? rnd.nextInt() : rnd.nextInt(domain);
    }

    static void randomRange(ModelHarness h, Random rnd, int domain) {
        Integer lo = rnd.nextInt(8) == 0 ? null : key(rnd, domain);
        Integer hi;
        if (rnd.nextInt(8) == 0) {
            hi = null;
        } else if (lo != null && rnd.nextBoolean()) {
            hi = (int) Math.min(Integer.MAX_VALUE, (long) lo + rnd.nextInt(Math.max(1, Math.min(domain, 500))));
        } else {
            hi = key(rnd, domain);
        }
        int limit = rnd.nextInt(6) == 0 ? rnd.nextInt(5) - 1 : Integer.MAX_VALUE;
        h.range(lo, rnd.nextBoolean(), hi, rnd.nextBoolean(), limit);
    }
}
