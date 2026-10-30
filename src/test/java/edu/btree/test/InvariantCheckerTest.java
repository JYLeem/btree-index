package edu.btree.test;

import edu.btree.core.BTree;
import edu.btree.core.NodeSnapshot;
import edu.btree.core.TreeSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 1. Invariant checker test
 *  (a) 실제 B-Tree 를 무작위로 변경하면서 매 연산 후 불변식을 검사 → 위반 0건이어야 한다.
 *  (b) 검사기 자체 검증: 일부러 손상시킨 스냅샷을 만들어 각 불변식 위반을 정확히 검출하는지 확인한다.
 */
final class InvariantCheckerTest {
    private InvariantCheckerTest() {
    }

    static void run(TestRecorder r, long seed) {
        r.beginSuite("1. Invariant checker test");

        for (int t = 2; t <= 6; t++) {
            Random rnd = new Random(seed + t);
            BTree<Integer, Integer> tree = new BTree<>(t);
            List<String> firstViolation = Collections.emptyList();
            int ops = 6_000;
            for (int i = 0; i < ops && firstViolation.isEmpty(); i++) {
                int k = rnd.nextInt(800);
                if (rnd.nextInt(10) < 6) tree.insert(k, ModelHarness.valueOf(k)); else tree.delete(k);
                firstViolation = InvariantChecker.check(tree.snapshot(), ModelHarness.VALUE_RULE);
            }
            r.record("INV-0" + (t - 1), "t=" + t + " 무작위 삽입/삭제 후 매 연산마다 불변식 검사",
                    "t=" + t + ", 키 0~799, 연산 " + ops + "회(삽입 60%/삭제 40%), seed=" + (seed + t),
                    "위반 0건", InvariantChecker.summary(firstViolation));
        }

        // (b) 손상 스냅샷 검출 — t=2 (비루트 키 1~3개) 기준, KEY_COUNT 부족만 t=3 사용
        TreeSnapshot<Integer, Integer> base = tree(2, 3, node(0, list(10), leaf(1, 5), leaf(2, 15)));
        r.record("INV-M00", "정상 기준 스냅샷은 위반 없음", base.structure(), "위반 0건",
                InvariantChecker.summary(InvariantChecker.check(base, ModelHarness.VALUE_RULE)));

        detect(r, "INV-M01", "노드 내 키 순서 뒤바뀜", "KEY_ORDER",
                tree(2, 4, node(0, list(10), leaf(1, 6, 5), leaf(2, 15))));
        detect(r, "INV-M02", "비루트 노드 키 부족 (t=3, 키 1개)", "KEY_COUNT",
                tree(3, 4, node3(0, list(10), leaf3(1, 5), leaf3(2, 15, 16))));
        detect(r, "INV-M03", "노드 키 초과 (t=2, 키 4개)", "KEY_COUNT",
                tree(2, 6, node(0, list(10), leaf(1, 1, 2, 3, 4), leaf(2, 15))));
        detect(r, "INV-M04", "자식 키가 구분자 구간 밖", "CHILD_RANGE",
                tree(2, 3, node(0, list(10), leaf(1, 5), leaf(2, 7))));
        detect(r, "INV-M05", "내부 노드 자식 수 ≠ 키 수+1", "CHILD_COUNT",
                tree(2, 4, node(0, list(10, 20), leaf(1, 5), leaf(2, 15))));
        detect(r, "INV-M06", "리프 깊이 불일치", "LEAF_DEPTH",
                tree(2, 5, node(0, list(10), leaf(1, 5), node(2, list(20), leaf(3, 15), leaf(4, 25)))));
        detect(r, "INV-M07", "키 없는 내부 루트", "ROOT",
                tree(2, 1, node(0, Collections.<Integer>emptyList(), leaf(1, 5))));
        detect(r, "INV-M08", "같은 노드 id 가 두 번 참조됨", "ALIAS",
                tree(2, 3, node(0, list(10), leaf(1, 5), leaf(1, 15))));
        detect(r, "INV-M09", "미사용 슬롯에 참조 잔존", "STALE_SLOT",
                tree(2, 3, node(0, list(10), new NodeSnapshot<>(1, true, list(5), list(ModelHarness.valueOf(5)),
                        Collections.<NodeSnapshot<Integer, Integer>>emptyList(), 3, 1), leaf(2, 15))));
        detect(r, "INV-M10", "기록된 size 와 실제 키 수 불일치", "SIZE",
                tree(2, 4, node(0, list(10), leaf(1, 5), leaf(2, 15))));
        detect(r, "INV-M11", "키-값 대응 손상", "VALUE",
                tree(2, 3, node(0, list(10), new NodeSnapshot<>(1, true, list(5), list(999),
                        Collections.<NodeSnapshot<Integer, Integer>>emptyList(), 3, 0), leaf(2, 15))));
    }

    private static void detect(TestRecorder r, String id, String name, String code, TreeSnapshot<Integer, Integer> s) {
        List<String> v = InvariantChecker.check(s, ModelHarness.VALUE_RULE);
        String actual = InvariantChecker.codes(v).contains(code)
                ? "검출: " + code : "미검출 (보고된 코드 " + InvariantChecker.codes(v) + ")";
        r.record(id, "검사기가 손상을 검출: " + name, "t=" + s.minDegree() + " " + s.structure() + " size=" + s.size(),
                "검출: " + code, actual);
    }

    // ---- 스냅샷 생성 도우미 ----

    static List<Integer> list(Integer... xs) {
        return new ArrayList<>(Arrays.asList(xs));
    }

    private static List<Integer> values(List<Integer> keys) {
        List<Integer> out = new ArrayList<>();
        for (Integer k : keys) out.add(ModelHarness.valueOf(k));
        return out;
    }

    static NodeSnapshot<Integer, Integer> leaf(int id, Integer... keys) {
        List<Integer> k = list(keys);
        return new NodeSnapshot<>(id, true, k, values(k), Collections.<NodeSnapshot<Integer, Integer>>emptyList(), 3, 0);
    }

    static NodeSnapshot<Integer, Integer> leaf3(int id, Integer... keys) {
        List<Integer> k = list(keys);
        return new NodeSnapshot<>(id, true, k, values(k), Collections.<NodeSnapshot<Integer, Integer>>emptyList(), 5, 0);
    }

    @SafeVarargs
    @SuppressWarnings("varargs") // 배열을 읽기만 하므로 안전
    static NodeSnapshot<Integer, Integer> node(int id, List<Integer> keys, NodeSnapshot<Integer, Integer>... children) {
        return new NodeSnapshot<>(id, false, keys, values(keys), childList(children), 3, 0);
    }

    @SafeVarargs
    @SuppressWarnings("varargs") // 배열을 읽기만 하므로 안전
    static NodeSnapshot<Integer, Integer> node3(int id, List<Integer> keys, NodeSnapshot<Integer, Integer>... children) {
        return new NodeSnapshot<>(id, false, keys, values(keys), childList(children), 5, 0);
    }

    private static List<NodeSnapshot<Integer, Integer>> childList(NodeSnapshot<Integer, Integer>[] children) {
        List<NodeSnapshot<Integer, Integer>> out = new ArrayList<>();
        for (NodeSnapshot<Integer, Integer> c : children) out.add(c);
        return out;
    }

    static TreeSnapshot<Integer, Integer> tree(int t, int size, NodeSnapshot<Integer, Integer> root) {
        return new TreeSnapshot<>(t, size, root);
    }
}
