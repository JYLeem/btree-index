package edu.btree.test;

import edu.btree.core.BTree;
import edu.btree.core.NodeSnapshot;
import edu.btree.core.StructureEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 6. Adversarial sequence test
 * B-Tree 의 분할·병합 경로를 집중적으로 자극하는 연산 순서를 t=2,3,5 에서 실행한다.
 * 매 연산 후 불변식 전체 + 모델 일치를 확인한다.
 */
final class AdversarialSequenceTest {
    private AdversarialSequenceTest() {
    }

    interface Scenario {
        void run(ModelHarness h, int n, Random rnd);
    }

    static void run(TestRecorder r, long seed) {
        r.beginSuite("6. Adversarial sequence test");
        Map<String, Scenario> scenarios = new LinkedHashMap<>();

        scenarios.put("오름차순 삽입", (h, n, rnd) -> {
            for (int k = 0; k < n; k++) h.insert(k);
        });
        scenarios.put("내림차순 삽입", (h, n, rnd) -> {
            for (int k = n - 1; k >= 0; k--) h.insert(k);
        });
        scenarios.put("오름차순 삽입 후 역순(내림차순) 삭제", (h, n, rnd) -> {
            for (int k = 0; k < n; k++) h.insert(k);
            for (int k = n - 1; k >= 0; k--) h.delete(k);
        });
        scenarios.put("내림차순 삽입 후 역순(오름차순) 삭제", (h, n, rnd) -> {
            for (int k = n - 1; k >= 0; k--) h.insert(k);
            for (int k = 0; k < n; k++) h.delete(k);
        });
        scenarios.put("반복적인 삽입과 삭제 (무작위 키, 10라운드)", (h, n, rnd) -> {
            for (int round = 0; round < 10 && h.divergence == null; round++) {
                for (int i = 0; i < n / 5; i++) h.insert(rnd.nextInt(n));
                for (int i = 0; i < n / 5; i++) h.delete(rnd.nextInt(n));
            }
        });
        scenarios.put("분할·병합 유도: 같은 키 삽입 직후 삭제 반복", (h, n, rnd) -> {
            for (int k = 0; k < n; k++) h.insert(k * 2);
            for (int i = 0; i < n * 2; i++) {
                int k = 2 * rnd.nextInt(n) + 1;
                h.insert(k);
                h.delete(k);
            }
        });
        scenarios.put("분할·병합 유도: 지그재그 삽입 후 가운데부터 삭제", (h, n, rnd) -> {
            for (int lo = 0, hi = n - 1; lo <= hi; lo++, hi--) {
                h.insert(lo);
                if (lo != hi) h.insert(hi);
            }
            for (int d = 0; d <= n / 2; d++) {
                h.delete(n / 2 - d);
                h.delete(n / 2 + d);
            }
        });
        scenarios.put("루트 키 저격: 현재 루트의 키만 삭제", (h, n, rnd) -> {
            for (int k = 0; k < n; k++) h.insert(k * 2);
            while (h.tree.size() > 0 && h.divergence == null) {
                List<Integer> rootKeys = h.tree.snapshot().root().keys();
                h.delete(rootKeys.get(rnd.nextInt(rootKeys.size())));
            }
        });
        scenarios.put("구분자 저격: 내부 노드 키만 골라 삭제 후 재삽입", (h, n, rnd) -> {
            for (int k = 0; k < n; k++) h.insert(rnd.nextInt(n * 4));
            for (int i = 0; i < n && h.divergence == null; i++) {
                List<Integer> internal = new ArrayList<>();
                collectInternal(h.tree.snapshot().root(), internal);
                if (internal.isEmpty()) break;
                h.delete(internal.get(rnd.nextInt(internal.size())));
                h.insert(rnd.nextInt(n * 4));
            }
        });
        scenarios.put("톱니파: 대량 삽입 후 90% 삭제 × 10", (h, n, rnd) -> {
            for (int round = 0; round < 10 && h.divergence == null; round++) {
                for (int i = 0; i < n; i++) h.insert(rnd.nextInt(n * 10));
                List<Integer> keys = new ArrayList<>(h.model.keySet());
                Collections.shuffle(keys, rnd);
                for (int i = 0; i < keys.size() * 9 / 10; i++) h.delete(keys.get(i));
            }
        });
        scenarios.put("무효 연산 폭주: 중복 삽입·없는 키 삭제·검색 (구조와 이벤트 수 불변)", (h, n, rnd) -> {
            for (int k = 0; k < n; k++) h.insert(k * 3);
            String snap = h.tree.snapshot().structure();
            long structural = structuralEvents(h.tree);
            for (int i = 0; i < n * 3; i++) {
                switch (rnd.nextInt(4)) {
                    case 0: h.insert(3 * rnd.nextInt(n)); break;
                    case 1: h.delete(3 * rnd.nextInt(n) + 1); break;
                    case 2: h.search(rnd.nextInt(n * 3)); break;
                    default: h.range(rnd.nextInt(n * 3), true, rnd.nextInt(n * 3), rnd.nextBoolean(), 1 + rnd.nextInt(20));
                }
            }
            if (!snap.equals(h.tree.snapshot().structure()) || structural != structuralEvents(h.tree)) {
                if (h.divergence == null) h.divergence = "무효 연산이 구조 또는 구조 이벤트 수를 변경함";
            }
        });

        int[] degrees = {2, 3, 5};
        int n = 2_000;
        int no = 0;
        for (Map.Entry<String, Scenario> s : scenarios.entrySet()) {
            no++;
            String failure = null;
            int maxHeight = 0;
            long ops = 0;
            for (int t : degrees) {
                ModelHarness h = new ModelHarness(t, 1);
                s.getValue().run(h, n, new Random(seed + t + no));
                ops += h.ops();
                if (!h.ok()) {
                    failure = "t=" + t + " " + h.divergence;
                    break;
                }
                for (int ht : h.heights) maxHeight = Math.max(maxHeight, ht);
            }
            r.record(String.format("ADV-%02d", no), s.getKey(),
                    "t=2,3,5, n=" + n + ", 매 연산 후 전체 검사, seed=" + seed,
                    "매 연산 후 불변식 유지 + 모델 일치",
                    failure == null ? "매 연산 후 불변식 유지 + 모델 일치" : "실패 " + failure);
            System.out.println("         (연산 " + ops + "회, 관측 최대 높이 " + maxHeight + ")");
        }
    }

    private static long structuralEvents(BTree<Integer, Integer> tree) {
        long sum = 0;
        for (StructureEvent e : StructureEvent.values()) {
            if (e != StructureEvent.DUPLICATE_INSERT_REJECTED && e != StructureEvent.DELETE_MISSING_KEY) {
                sum += tree.eventCount(e);
            }
        }
        return sum;
    }

    private static void collectInternal(NodeSnapshot<Integer, Integer> x, List<Integer> out) {
        if (x.isLeaf()) return;
        out.addAll(x.keys());
        for (NodeSnapshot<Integer, Integer> c : x.children()) collectInternal(c, out);
    }
}
