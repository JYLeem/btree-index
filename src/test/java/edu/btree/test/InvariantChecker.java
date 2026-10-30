package edu.btree.test;

import edu.btree.core.NodeSnapshot;
import edu.btree.core.TreeSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;

/**
 * 독립 불변식 검사기. BTree 의 구현 코드와 무관하게 스냅샷만 보고 판정한다.
 * 위반 문자열은 "코드 설명" 형식이며 코드는 다음 중 하나다.
 *
 *  KEY_ORDER   노드 내·전역 중위 순회 키의 엄격한 오름차순
 *  KEY_COUNT   노드 키 수: 루트 ≤ 2t-1, 비루트 t-1 ~ 2t-1, 노드 용량 2t-1
 *  CHILD_RANGE 자식의 키가 부모 구분 키가 정한 구간 (low, high) 안에 있음
 *  CHILD_COUNT 내부 노드 자식 수 = 키 수 + 1 (누락 없음), 리프는 자식 없음
 *  LEAF_DEPTH  모든 리프의 깊이가 같음
 *  ROOT        루트 존재, 내부 루트는 키 ≥ 1, 빈 트리의 루트는 빈 리프
 *  ALIAS       한 노드가 두 번 이상 참조되지 않음
 *  STALE_SLOT  사용하지 않는 슬롯에 참조가 남지 않음
 *  SIZE        기록된 size = 실제 키 수
 *  VALUE       키-값 개수 일치 및 값 규칙
 *  HEIGHT      높이가 이론 범위 log_2t(n+1) ≤ h ≤ log_t((n+1)/2)+1 안에 있음
 */
public final class InvariantChecker {
    private static final int MAX_VIOLATIONS = 30;

    private InvariantChecker() {
    }

    private static final class State<K> {
        final Set<Integer> ids = new HashSet<>();
        int leafDepth = -1;
        int count;
        K prev;
    }

    public static <K extends Comparable<K>, V> List<String> check(TreeSnapshot<K, V> s, BiPredicate<K, V> valueRule) {
        List<String> v = new ArrayList<>();
        int t = s.minDegree();
        NodeSnapshot<K, V> root = s.root();
        if (t < 2) add(v, "ROOT 최소 차수가 2 미만: " + t);
        if (root == null) {
            add(v, "ROOT 루트가 null");
            return v;
        }
        if (!root.isLeaf() && root.keys().isEmpty()) add(v, "ROOT 키가 없는 내부 루트");
        if (s.size() == 0 && !(root.isLeaf() && root.keys().isEmpty())) add(v, "ROOT 빈 트리의 루트가 빈 리프가 아님");

        State<K> st = new State<>();
        visit(root, true, null, null, 0, t, valueRule, v, st);

        if (st.count != s.size()) add(v, "SIZE 기록 " + s.size() + " ≠ 실제 " + st.count);
        int height = st.leafDepth + 1;
        int n = st.count;
        if (n > 0 && st.leafDepth >= 0) {
            double max = Math.log((n + 1) / 2.0) / Math.log(t) + 1 + 1e-9;
            double min = Math.log(n + 1) / Math.log(2.0 * t) - 1e-9;
            if (height > max || height < min) {
                add(v, String.format("HEIGHT 높이 %d 가 이론 범위 [%.2f, %.2f] 밖 (n=%d)", height, min, max, n));
            }
        }
        return v;
    }

    private static <K extends Comparable<K>, V> void visit(NodeSnapshot<K, V> x, boolean isRoot, K low, K high,
                                                         int depth, int t, BiPredicate<K, V> rule,
                                                         List<String> v, State<K> st) {
        if (!st.ids.add(x.id())) {
            add(v, "ALIAS 노드 id " + x.id() + " 가 두 번 이상 참조됨");
            return;
        }
        List<K> keys = x.keys();
        int n = keys.size();
        if (x.values().size() != n) add(v, "VALUE 노드 " + x.id() + " 키 " + n + "개, 값 " + x.values().size() + "개");
        if (x.keyCapacity() != 2 * t - 1) add(v, "KEY_COUNT 노드 " + x.id() + " 용량 " + x.keyCapacity() + " ≠ 2t-1");
        if (n > 2 * t - 1) add(v, "KEY_COUNT 노드 " + x.id() + " 키 " + n + "개 > 2t-1=" + (2 * t - 1));
        if (!isRoot && n < t - 1) add(v, "KEY_COUNT 노드 " + x.id() + " 키 " + n + "개 < t-1=" + (t - 1));
        if (x.staleSlots() > 0) add(v, "STALE_SLOT 노드 " + x.id() + " 미사용 슬롯 참조 " + x.staleSlots() + "개");

        for (int i = 0; i < n; i++) {
            K k = keys.get(i);
            if (k == null) {
                add(v, "KEY_ORDER 노드 " + x.id() + " 에 null 키");
                return;
            }
            if (i > 0 && keys.get(i - 1).compareTo(k) >= 0) {
                add(v, "KEY_ORDER 노드 " + x.id() + " 내 " + keys.get(i - 1) + " ≥ " + k);
            }
            if ((low != null && k.compareTo(low) <= 0) || (high != null && k.compareTo(high) >= 0)) {
                add(v, "CHILD_RANGE 노드 " + x.id() + " 키 " + k + " 가 구간 (" + low + ", " + high + ") 밖");
            }
            if (i < x.values().size() && rule != null && !rule.test(k, x.values().get(i))) {
                add(v, "VALUE 키 " + k + " 의 값 " + x.values().get(i) + " 이 규칙 위반");
            }
        }

        if (x.isLeaf()) {
            if (!x.children().isEmpty()) add(v, "CHILD_COUNT 리프 노드 " + x.id() + " 가 자식을 가짐");
            if (st.leafDepth == -1) {
                st.leafDepth = depth;
            } else if (st.leafDepth != depth) {
                add(v, "LEAF_DEPTH 리프 깊이 " + st.leafDepth + " 와 " + depth + " 불일치");
            }
            for (K k : keys) inorder(k, v, st);
            return;
        }
        if (x.children().size() != n + 1) {
            add(v, "CHILD_COUNT 노드 " + x.id() + " 키 " + n + "개, 자식 " + x.children().size() + "개");
            return;
        }
        for (int i = 0; i <= n; i++) {
            NodeSnapshot<K, V> c = x.children().get(i);
            if (c == null) {
                add(v, "CHILD_COUNT 노드 " + x.id() + " 의 자식 " + i + " 누락");
                return;
            }
            visit(c, false, i == 0 ? low : keys.get(i - 1), i == n ? high : keys.get(i), depth + 1, t, rule, v, st);
            if (i < n) inorder(keys.get(i), v, st);
        }
    }

    private static <K extends Comparable<K>> void inorder(K k, List<String> v, State<K> st) {
        if (st.prev != null && st.prev.compareTo(k) >= 0) {
            add(v, "KEY_ORDER 중위 순회에서 " + st.prev + " ≥ " + k);
        }
        st.prev = k;
        st.count++;
    }

    private static void add(List<String> v, String s) {
        if (v.size() < MAX_VIOLATIONS) v.add(s);
    }

    /** 위반 목록에서 코드만 추출 */
    public static Set<String> codes(List<String> violations) {
        Set<String> out = new java.util.TreeSet<>();
        for (String s : violations) out.add(s.split(" ", 2)[0]);
        return out;
    }

    /** "위반 0건" 또는 "위반 n건: 첫 위반" */
    public static String summary(List<String> violations) {
        return violations.isEmpty() ? "위반 0건" : "위반 " + violations.size() + "건: " + violations.get(0);
    }
}
