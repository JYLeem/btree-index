package edu.btree.test;

import edu.btree.core.BTree;
import edu.btree.core.NodeSnapshot;
import edu.btree.core.StructureEvent;
import edu.btree.core.TreeSnapshot;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

/**
 * 5. Structural coverage test
 * 트리가 세는 StructureEvent 횟수로 각 구조 변경 경로의 실행 여부를 기록하고,
 * 노드 채움 수준·높이 형태 커버리지도 확인한다.
 */
final class StructuralCoverageTest {
    private StructuralCoverageTest() {
    }

    static void run(TestRecorder r, long seed) {
        r.beginSuite("5. Structural coverage test");
        Map<StructureEvent, Long> totals = new EnumMap<>(StructureEvent.class);
        for (StructureEvent e : StructureEvent.values()) totals.put(e, 0L);

        Set<Integer> leafFills = new TreeSet<>();
        Set<Integer> internalFills = new TreeSet<>();
        Set<Integer> rootFills = new TreeSet<>();
        Set<Integer> heights = new TreeSet<>();
        int fillT = 3;

        Random rnd = new Random(seed + 5);
        String workload = "t=2·3, 키 도메인 60/600/6000, 단계마다 삽입 70%↔30% 교대, 각 30,000회, seed=" + (seed + 5);
        for (int t : new int[]{2, 3}) {
            for (int domain : new int[]{60, 600, 6_000}) {
                BTree<Integer, Integer> tree = new BTree<>(t);
                for (int i = 0; i < 30_000; i++) {
                    if (t == fillT && i % 25 == 0) collectShape(tree.snapshot(), leafFills, internalFills, rootFills, heights);
                    int k = rnd.nextInt(domain);
                    int insertShare = (i / 5_000) % 2 == 0 ? 70 : 30;
                    if (rnd.nextInt(100) < insertShare) tree.insert(k, ModelHarness.valueOf(k));
                    else tree.delete(k);
                }
                for (Map.Entry<StructureEvent, Long> e : tree.eventCounts().entrySet()) {
                    totals.merge(e.getKey(), e.getValue(), Long::sum);
                }
            }
        }

        int no = 0;
        for (StructureEvent e : StructureEvent.values()) {
            long c = totals.get(e);
            no++;
            r.record(String.format("COV-%02d", no), "경로 실행 여부: " + e + " (" + describe(e) + ")", workload,
                    "실행됨", c > 0 ? "실행됨" : "미실행");
            System.out.printf("         %-32s %,10d회%n", e, c);
        }

        Set<Integer> expectedFills = new TreeSet<>();
        for (int f = fillT - 1; f <= 2 * fillT - 1; f++) expectedFills.add(f);
        Set<Integer> expectedRoot = new TreeSet<>();
        for (int f = 0; f <= 2 * fillT - 1; f++) expectedRoot.add(f);
        r.record("COV-F1", "비루트 리프의 모든 채움 수준 관측", "t=3 워크로드, 25연산마다 표본",
                "포함 " + expectedFills, leafFills.containsAll(expectedFills) ? "포함 " + expectedFills : "관측 " + leafFills);
        r.record("COV-F2", "비루트 내부 노드의 모든 채움 수준 관측", "t=3 워크로드, 25연산마다 표본",
                "포함 " + expectedFills, internalFills.containsAll(expectedFills) ? "포함 " + expectedFills : "관측 " + internalFills);
        r.record("COV-F3", "루트의 모든 채움 수준 관측", "t=3 워크로드",
                "포함 " + expectedRoot, rootFills.containsAll(expectedRoot) ? "포함 " + expectedRoot : "관측 " + rootFills);
        Set<Integer> expectedHeights = new TreeSet<>(Arrays.asList(1, 2, 3, 4, 5));
        r.record("COV-F4", "높이 1~5 관측 (루트 분할·축소가 여러 층에서 발생)", "t=3 워크로드",
                "포함 " + expectedHeights, heights.containsAll(expectedHeights) ? "포함 " + expectedHeights : "관측 " + heights);
    }

    private static String describe(StructureEvent e) {
        switch (e) {
            case ROOT_SPLIT: return "루트 변경-분할";
            case ROOT_SHRINK: return "루트 변경-축소";
            case LEAF_SPLIT: case INTERNAL_SPLIT: return "노드 분할";
            case LEAF_MERGE: case INTERNAL_MERGE: return "병합";
            case BORROW_LEFT_LEAF: case BORROW_LEFT_INTERNAL: case BORROW_RIGHT_LEAF: case BORROW_RIGHT_INTERNAL:
                return "키 재분배";
            case DELETE_INTERNAL_BY_PREDECESSOR: case DELETE_INTERNAL_BY_SUCCESSOR: case DELETE_INTERNAL_BY_MERGE:
                return "내부 노드 키 삭제";
            case DELETE_FROM_LEAF: return "리프 키 삭제";
            default: return "정책 분기";
        }
    }

    private static void collectShape(TreeSnapshot<Integer, Integer> s, Set<Integer> leafFills,
                                     Set<Integer> internalFills, Set<Integer> rootFills, Set<Integer> heights) {
        rootFills.add(s.root().keys().size());
        int h = 1;
        for (NodeSnapshot<Integer, Integer> x = s.root(); !x.isLeaf(); x = x.children().get(0)) h++;
        heights.add(h);
        for (NodeSnapshot<Integer, Integer> c : s.root().children()) walk(c, leafFills, internalFills);
    }

    private static void walk(NodeSnapshot<Integer, Integer> x, Set<Integer> leafFills, Set<Integer> internalFills) {
        (x.isLeaf() ? leafFills : internalFills).add(x.keys().size());
        for (NodeSnapshot<Integer, Integer> c : x.children()) walk(c, leafFills, internalFills);
    }
}
