package edu.btree.test;

import edu.btree.core.BTree;
import edu.btree.core.Entry;
import edu.btree.core.StructureEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * 4. Edge case test — 예상 결과는 모두 손으로 계산한 값이다(구조 문자열 포함).
 * 구조 문자열 형식: 리프 "(1 2)", 내부 노드 "((1 2) 3 (4 5))"
 */
final class EdgeCaseTest {
    private EdgeCaseTest() {
    }

    private static BTree<Integer, Integer> build(int t, int... keys) {
        BTree<Integer, Integer> tree = new BTree<>(t);
        for (int k : keys) tree.insert(k, ModelHarness.valueOf(k));
        return tree;
    }

    private static String inv(BTree<Integer, Integer> tree) {
        return InvariantChecker.summary(InvariantChecker.check(tree.snapshot(), ModelHarness.VALUE_RULE));
    }

    private static String keys(List<Entry<Integer, Integer>> entries) {
        List<Integer> out = new ArrayList<>();
        for (Entry<Integer, Integer> e : entries) out.add(e.key());
        return out.toString();
    }

    private static String thrown(Runnable r) {
        try {
            r.run();
            return "예외 없음";
        } catch (RuntimeException e) {
            return e.getClass().getSimpleName();
        }
    }

    static void run(TestRecorder r, long seed) {
        r.beginSuite("4. Edge case test");

        // --- 빈 트리
        BTree<Integer, Integer> empty = new BTree<>(2);
        r.record("EDGE-01", "빈 트리 검색/삭제/범위/순회", "t=2, 키 없음; search(1), delete(1), rangeSearch(null,null), inOrder",
                "null null [] [] 높이1 () 위반 0건",
                empty.search(1) + " " + empty.delete(1) + " " + keys(empty.rangeSearch(null, null)) + " "
                        + keys(empty.inOrder()) + " 높이" + empty.height() + " " + empty.snapshot().structure() + " " + inv(empty));

        // --- 단일 키
        BTree<Integer, Integer> single = build(2, 42);
        String s1 = single.snapshot().structure() + " " + single.search(42);
        Integer removed = single.delete(42);
        r.record("EDGE-02", "단일 키 삽입·검색·삭제", "t=2, insert 42 → search 42 → delete 42",
                "(42) 1309 → 1309 () size0 위반 0건",
                s1 + " → " + removed + " " + single.snapshot().structure() + " size" + single.size() + " " + inv(single));

        // --- 존재하지 않는 키
        BTree<Integer, Integer> t20 = new BTree<>(2);
        for (int k = 1; k <= 20; k++) t20.insert(k, ModelHarness.valueOf(k));
        String before = t20.snapshot().structure();
        Integer sr = t20.search(99);
        Integer dr = t20.delete(99);
        Integer dr0 = t20.delete(0);
        r.record("EDGE-03", "존재하지 않는 키의 검색·삭제는 null, 구조 불변", "t=2, 키 1~20; search(99), delete(99), delete(0)",
                "null null null 구조불변 size20",
                sr + " " + dr + " " + dr0 + " " + (before.equals(t20.snapshot().structure()) ? "구조불변" : "구조변경")
                        + " size" + t20.size());

        // --- 루트 분할
        BTree<Integer, Integer> rs = build(3, 1, 2, 3, 4, 5);
        String full = rs.snapshot().structure() + " 높이" + rs.height();
        rs.insert(6, ModelHarness.valueOf(6));
        r.record("EDGE-04", "루트 분할: 가득 찬 루트(2t-1)에 삽입", "t=3, insert 1..5 → insert 6",
                "(1 2 3 4 5) 높이1 → ((1 2) 3 (4 5 6)) 높이2 ROOT_SPLIT=1",
                full + " → " + rs.snapshot().structure() + " 높이" + rs.height()
                        + " ROOT_SPLIT=" + rs.eventCount(StructureEvent.ROOT_SPLIT));

        // --- 루트 축소
        BTree<Integer, Integer> shrink = build(3, 1, 2, 3, 4, 5, 6);
        shrink.delete(6);
        String mid = shrink.snapshot().structure();
        shrink.delete(1);
        r.record("EDGE-05", "루트 축소: 병합으로 루트가 비면 높이 감소", "t=3, insert 1..6, delete 6 → delete 1",
                "((1 2) 3 (4 5)) → (2 3 4 5) 높이1 ROOT_SHRINK=1 위반 0건",
                mid + " → " + shrink.snapshot().structure() + " 높이" + shrink.height()
                        + " ROOT_SHRINK=" + shrink.eventCount(StructureEvent.ROOT_SHRINK) + " " + inv(shrink));

        // --- 최소 키 노드(t-1)에서 삭제: 오른쪽 형제에서 재분배
        BTree<Integer, Integer> br = build(3, 1, 2, 3, 4, 5, 6);
        br.delete(1);
        r.record("EDGE-06", "최소 키 노드 삭제 → 오른쪽 형제 재분배", "t=3, ((1 2) 3 (4 5 6)) 에서 delete 1",
                "((2 3) 4 (5 6)) BORROW_RIGHT_LEAF=1",
                br.snapshot().structure() + " BORROW_RIGHT_LEAF=" + br.eventCount(StructureEvent.BORROW_RIGHT_LEAF));

        // --- 최소 키 노드에서 삭제: 왼쪽 형제에서 재분배
        BTree<Integer, Integer> bl = build(3, 1, 2, 3, 4, 5, 6);
        bl.delete(6);
        bl.insert(0, ModelHarness.valueOf(0));
        String blBefore = bl.snapshot().structure();
        bl.delete(4);
        r.record("EDGE-07", "최소 키 노드 삭제 → 왼쪽 형제 재분배", "t=3, insert 1..6, delete 6, insert 0 → delete 4",
                "((0 1 2) 3 (4 5)) → ((0 1) 2 (3 5)) BORROW_LEFT_LEAF=1",
                blBefore + " → " + bl.snapshot().structure() + " BORROW_LEFT_LEAF="
                        + bl.eventCount(StructureEvent.BORROW_LEFT_LEAF));

        // --- 최소 키 노드에서 삭제: 형제도 최소 → 병합
        BTree<Integer, Integer> mg = build(3, 1, 2, 3, 4, 5, 6);
        mg.delete(6);
        mg.delete(4);
        r.record("EDGE-08", "최소 키 노드 삭제 → 형제와 병합(+루트 축소)", "t=3, ((1 2) 3 (4 5)) 에서 delete 4",
                "(1 2 3 5) LEAF_MERGE=1", mg.snapshot().structure() + " LEAF_MERGE=" + mg.eventCount(StructureEvent.LEAF_MERGE));

        // --- 내부 노드 키 삭제 2a / 2b / 2c
        BTree<Integer, Integer> c2a = build(3, 1, 2, 3, 4, 5, 6);
        c2a.delete(6);
        c2a.insert(0, ModelHarness.valueOf(0));
        c2a.delete(3);
        r.record("EDGE-09", "내부 키 삭제 2a: 왼쪽 자식 ≥ t → 선행자로 대체", "t=3, ((0 1 2) 3 (4 5)) 에서 delete 3",
                "((0 1) 2 (4 5)) 2a=1", c2a.snapshot().structure() + " 2a="
                        + c2a.eventCount(StructureEvent.DELETE_INTERNAL_BY_PREDECESSOR));

        BTree<Integer, Integer> c2b = build(3, 1, 2, 3, 4, 5, 6);
        c2b.delete(3);
        r.record("EDGE-10", "내부 키 삭제 2b: 오른쪽 자식 ≥ t → 후속자로 대체", "t=3, ((1 2) 3 (4 5 6)) 에서 delete 3",
                "((1 2) 4 (5 6)) 2b=1", c2b.snapshot().structure() + " 2b="
                        + c2b.eventCount(StructureEvent.DELETE_INTERNAL_BY_SUCCESSOR));

        BTree<Integer, Integer> c2c = build(3, 1, 2, 3, 4, 5, 6);
        c2c.delete(6);
        c2c.delete(3);
        r.record("EDGE-11", "내부 키 삭제 2c: 양쪽 자식 t-1 → 병합 후 삭제", "t=3, ((1 2) 3 (4 5)) 에서 delete 3",
                "(1 2 4 5) 2c=1 ROOT_SHRINK=1", c2c.snapshot().structure() + " 2c="
                        + c2c.eventCount(StructureEvent.DELETE_INTERNAL_BY_MERGE)
                        + " ROOT_SHRINK=" + c2c.eventCount(StructureEvent.ROOT_SHRINK));

        // --- 중복 키
        BTree<Integer, Integer> dup = build(2, 1, 2, 3, 4, 5);
        String dupBefore = dup.snapshot().structure();
        boolean first = dup.insert(3, -1);
        r.record("EDGE-12", "중복 키 삽입 거부: 값·구조 유지", "t=2, 키 1..5 에 insert(3, -1)",
                "false 100 구조불변", first + " " + dup.search(3) + " "
                        + (dupBefore.equals(dup.snapshot().structure()) ? "구조불변" : "구조변경"));

        // --- 잘못된 입력
        BTree<Integer, Integer> nul = build(2, 1);
        r.record("EDGE-13", "null 키/값과 잘못된 차수", "insert(null,1), insert(1,null), search(null), delete(null), new BTree(1), new BTree(0)",
                "NullPointerException NullPointerException NullPointerException NullPointerException IllegalArgumentException IllegalArgumentException size1",
                thrown(() -> nul.insert(null, 1)) + " " + thrown(() -> nul.insert(2, null)) + " "
                        + thrown(() -> nul.search(null)) + " " + thrown(() -> nul.delete(null)) + " "
                        + thrown(() -> new BTree<Integer, Integer>(1)) + " " + thrown(() -> new BTree<Integer, Integer>(0))
                        + " size" + nul.size());

        // --- 정수 경계값
        BTree<Integer, Integer> ext = build(2, Integer.MAX_VALUE, 0, Integer.MIN_VALUE, -1);
        r.record("EDGE-14", "정수 최솟값·최댓값 키", "t=2, insert MAX, 0, MIN, -1 → inOrder, rangeSearch(MIN, MIN)",
                "[-2147483648, -1, 0, 2147483647] [-2147483648]",
                keys(ext.inOrder()) + " " + keys(ext.rangeSearch(Integer.MIN_VALUE, Integer.MIN_VALUE)));

        // --- 범위 경계
        BTree<Integer, Integer> rg = build(2, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100);
        r.record("EDGE-15", "범위 검색 경계 조건", "t=2, 키 10..100(10 간격): [50,50] (50,50] [60,40] [35,∞)limit1 limit0 [15,45] (10,30]",
                "[50] [] [] [40] [] [20, 30, 40] [20, 30]",
                keys(rg.rangeSearch(50, 50)) + " " + keys(rg.rangeSearch(50, false, 50, true, 10)) + " "
                        + keys(rg.rangeSearch(60, 40)) + " " + keys(rg.rangeSearch(35, true, null, true, 1)) + " "
                        + keys(rg.rangeSearch(null, true, null, true, 0)) + " " + keys(rg.rangeSearch(15, 45)) + " "
                        + keys(rg.rangeSearch(10, false, 30, true, 10)));

        // --- 큰 차수
        BTree<Integer, Integer> wide = new BTree<>(64);
        for (int k = 0; k < 127; k++) wide.insert(k, ModelHarness.valueOf(k));
        int h127 = wide.height();
        wide.insert(127, ModelHarness.valueOf(127));
        r.record("EDGE-16", "큰 차수 t=64: 2t-1=127개까지 루트 하나", "t=64, insert 0..126 → insert 127",
                "높이1 → 높이2 위반 0건", "높이" + h127 + " → 높이" + wide.height() + " " + inv(wide));

        // --- 같은 키 반복 삽입/삭제
        BTree<Integer, Integer> flip = new BTree<>(2);
        boolean ok = true;
        for (int i = 0; i < 1_000; i++) ok &= flip.insert(7, ModelHarness.valueOf(7)) && flip.delete(7) != null;
        r.record("EDGE-17", "같은 키 삽입/삭제 1,000회 반복", "t=2, (insert 7, delete 7) × 1000",
                "true () size0", ok + " " + flip.snapshot().structure() + " size" + flip.size());
    }
}
