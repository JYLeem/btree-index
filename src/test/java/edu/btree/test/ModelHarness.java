package edu.btree.test;

import edu.btree.core.BTree;
import edu.btree.core.Entry;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BiPredicate;

/**
 * 하네스: B-Tree 와 기준 모델(TreeMap 값 모델 + TreeSet 키 집합)을 같은 순서로 조작하고,
 * 매 연산의 반환값과 주기적인 전체 상태(불변식·키 집합)를 비교한다.
 * 처음 발견한 불일치를 divergence 에 남기고 이후는 기록만 멈춘다.
 */
final class ModelHarness {
    static int valueOf(int key) {
        return key * 31 + 7;
    }

    static final BiPredicate<Integer, Integer> VALUE_RULE = (k, v) -> v != null && v == valueOf(k);

    final BTree<Integer, Integer> tree;
    final TreeMap<Integer, Integer> model = new TreeMap<>();
    final TreeSet<Integer> keySet = new TreeSet<>();
    final Set<Integer> heights = new TreeSet<>();
    private final int checkEvery;
    private long ops;
    String divergence;

    ModelHarness(int t, int checkEvery) {
        this.tree = new BTree<>(t);
        this.checkEvery = Math.max(1, checkEvery);
    }

    boolean insert(int k) {
        boolean expected = !model.containsKey(k);
        if (expected) {
            model.put(k, valueOf(k));
            keySet.add(k);
        }
        boolean actual = tree.insert(k, valueOf(k));
        if (actual != expected) diverge("insert(" + k + ") 반환 " + actual + ", 기대 " + expected);
        after();
        return actual;
    }

    Integer delete(int k) {
        Integer expected = model.remove(k);
        keySet.remove(k);
        Integer actual = tree.delete(k);
        if (!same(expected, actual)) diverge("delete(" + k + ") 반환 " + actual + ", 기대 " + expected);
        after();
        return actual;
    }

    Integer search(int k) {
        Integer expected = model.get(k);
        Integer actual = tree.search(k);
        if (!same(expected, actual)) diverge("search(" + k + ") 반환 " + actual + ", 기대 " + expected);
        after();
        return actual;
    }

    void range(Integer lo, boolean loInc, Integer hi, boolean hiInc, int limit) {
        List<Integer> expected = expectedRange(model, lo, loInc, hi, hiInc, limit);
        List<Integer> actual = new ArrayList<>();
        for (Entry<Integer, Integer> e : tree.rangeSearch(lo, loInc, hi, hiInc, limit)) {
            actual.add(e.key());
            if (e.value() != valueOf(e.key())) diverge("rangeSearch 값 불일치 key=" + e.key());
        }
        if (!actual.equals(expected)) {
            diverge("rangeSearch(" + lo + (loInc ? "[" : "(") + "," + hi + (hiInc ? "]" : ")") + ",limit " + limit
                    + ") = " + abbreviate(actual) + ", 기대 " + abbreviate(expected));
        }
        after();
    }

    private void after() {
        ops++;
        if (tree.size() != model.size()) diverge("size " + tree.size() + " ≠ 모델 " + model.size());
        if (divergence == null && ops % checkEvery == 0) fullCheck();
    }

    /** 불변식 + 중위 순회 키 = TreeSet 키 집합 + 값 = TreeMap */
    void fullCheck() {
        if (divergence != null) return;
        List<String> violations = InvariantChecker.check(tree.snapshot(), VALUE_RULE);
        heights.add(tree.height());
        if (!violations.isEmpty()) {
            diverge("불변식 " + InvariantChecker.summary(violations));
            return;
        }
        List<Integer> keys = new ArrayList<>();
        for (Entry<Integer, Integer> e : tree.inOrder()) keys.add(e.key());
        if (!keys.equals(new ArrayList<>(keySet))) diverge("중위 순회 키 ≠ TreeSet (" + keys.size() + " vs " + keySet.size() + ")");
    }

    boolean ok() {
        fullCheck();
        return divergence == null;
    }

    long ops() { return ops; }

    private void diverge(String msg) {
        if (divergence == null) divergence = "연산 #" + (ops + 1) + ": " + msg;
    }

    private static boolean same(Integer a, Integer b) {
        return a == null ? b == null : a.equals(b);
    }

    /** 기준 범위 결과: TreeMap 의 tailMap/headMap 으로 독립 계산 */
    static <K extends Comparable<K>> List<K> expectedRange(NavigableMap<K, ?> m, K lo, boolean loInc, K hi,
                                                          boolean hiInc, int limit) {
        List<K> out = new ArrayList<>();
        if (limit <= 0) return out;
        if (lo != null && hi != null) {
            int c = lo.compareTo(hi);
            if (c > 0 || (c == 0 && !(loInc && hiInc))) return out;
        }
        NavigableMap<K, ?> view = m;
        if (lo != null) view = view.tailMap(lo, loInc);
        if (hi != null) view = view.headMap(hi, hiInc);
        for (K k : view.keySet()) {
            if (out.size() >= limit) break;
            out.add(k);
        }
        return out;
    }

    static String abbreviate(List<?> list) {
        return list.size() <= 8 ? list.toString() : list.subList(0, 8) + "…(" + list.size() + "개)";
    }
}
