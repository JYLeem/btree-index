package edu.btree.test;

import edu.btree.core.BTree;
import edu.btree.core.StructureEvent;

/**
 * 9. 회귀 테스트 — 발견된 결함마다 최소 재현 테스트를 하나씩 둔다.
 * 결함 기록은 docs/LOOP_LOG.md 참조.
 */
final class RegressionTest {
    private RegressionTest() {
    }

    static void run(TestRecorder r, long seed) {
        r.beginSuite("9. 회귀 테스트");

        // R-001 (이전 세션에서 발견): 하향식 삭제가 없는 키에도 경로상의 병합·재분배를 수행해 구조를 바꿈
        BTree<Integer, Integer> tree = new BTree<>(2);
        for (int k : new int[]{0, 3, 2, 1}) tree.insert(k, ModelHarness.valueOf(k));
        String before = tree.snapshot().structure();
        long events = structuralEvents(tree);
        Integer result = tree.delete(4);
        r.record("R-001", "없는 키 delete 가 구조를 바꾸지 않음 (최소 반례)", "t=2, insert 0,3,2,1 → delete 4",
                "null " + before + " 구조 이벤트 0회",
                result + " " + tree.snapshot().structure() + " 구조 이벤트 " + (structuralEvents(tree) - events) + "회");

        // R-002 (이번 루프 반복 3에서 발견): 최소 차수 상한 검사 없음 → 2t-1 정수 오버플로
        r.record("R-002a", "매우 큰 최소 차수는 IllegalArgumentException", "new BTree(Integer.MAX_VALUE), new BTree(65537)",
                "IllegalArgumentException IllegalArgumentException",
                thrown(() -> new BTree<Integer, Integer>(Integer.MAX_VALUE)) + " " + thrown(() -> new BTree<Integer, Integer>(65_537)));
        r.record("R-002b", "허용 상한(65536)의 최소 차수는 생성 가능", "new BTree(65536)",
                "생성됨", thrown(() -> new BTree<Integer, Integer>(65_536)).equals("예외 없음") ? "생성됨" : "생성 실패");
    }

    private static String thrown(Runnable r) {
        try {
            r.run();
            return "예외 없음";
        } catch (RuntimeException e) {
            return e.getClass().getSimpleName();
        }
    }

    /** 무효 연산 집계용 이벤트를 제외한 모든 구조 변경 이벤트 수 */
    private static long structuralEvents(BTree<Integer, Integer> tree) {
        long sum = 0;
        for (StructureEvent e : StructureEvent.values()) {
            if (e != StructureEvent.DUPLICATE_INSERT_REJECTED && e != StructureEvent.DELETE_MISSING_KEY) {
                sum += tree.eventCount(e);
            }
        }
        return sum;
    }
}
