package edu.btree.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * 고전적 B-Tree (CLRS 정의, 최소 차수 t).
 *
 * <ul>
 *   <li>모든 노드의 키 수 ≤ 2t-1, 루트를 제외한 노드의 키 수 ≥ t-1</li>
 *   <li>내부 노드의 자식 수 = 키 수 + 1, 모든 리프는 같은 깊이</li>
 *   <li>내부 노드도 (키, 값)을 저장한다. 리프 연결 리스트는 없다 → B+Tree 가 아니다.</li>
 *   <li>중복 키 거부, null 키·값 불허, 없는 키 삭제는 내용과 구조 모두 바꾸지 않는다.</li>
 * </ul>
 * 삽입은 하향식 선분할, 삭제는 하향식(경로의 자식을 미리 t 개 이상으로 보강)으로 한 번에 내려간다.
 * 단일 스레드 전용이다.
 */
public final class BTree<K extends Comparable<K>, V> {

    /** 노드: 디스크 페이지처럼 고정 크기 배열(키·값 2t-1, 자식 2t)을 사용한다. */
    private static final class Node {
        int n;
        final boolean leaf;
        final Object[] keys;
        final Object[] values;
        final Node[] children; // 리프면 null

        Node(int t, boolean leaf) {
            this.leaf = leaf;
            this.keys = new Object[2 * t - 1];
            this.values = new Object[2 * t - 1];
            this.children = leaf ? null : new Node[2 * t];
        }
    }

    /**
     * 허용하는 최대 최소 차수. 노드 배열 크기 2t 가 int 범위를 넘거나(오버플로)
     * 비현실적으로 큰 배열을 할당하지 않도록 제한한다. (결함 D-002 수정)
     */
    public static final int MAX_MIN_DEGREE = 1 << 16;

    private final int t;
    private Node root;
    private int size;
    private final EnumMap<StructureEvent, Long> events = new EnumMap<>(StructureEvent.class);

    /**
     * @param minDegree 최소 차수 t (2 이상 MAX_MIN_DEGREE 이하)
     */
    public BTree(int minDegree) {
        if (minDegree < 2 || minDegree > MAX_MIN_DEGREE) {
            throw new IllegalArgumentException(
                    "최소 차수 t 는 2 이상 " + MAX_MIN_DEGREE + " 이하여야 합니다: " + minDegree);
        }
        this.t = minDegree;
        this.root = new Node(minDegree, true);
    }

    public int minDegree() { return t; }
    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    /** 높이 (루트만 있으면 1) */
    public int height() {
        int h = 1;
        for (Node x = root; !x.leaf; x = x.children[0]) {
            h++;
        }
        return h;
    }

    /** 이벤트별 누적 실행 횟수 */
    public long eventCount(StructureEvent event) {
        Long c = events.get(event);
        return c == null ? 0L : c;
    }

    /** 모든 이벤트의 누적 실행 횟수 (0 포함) */
    public Map<StructureEvent, Long> eventCounts() {
        EnumMap<StructureEvent, Long> copy = new EnumMap<>(StructureEvent.class);
        for (StructureEvent e : StructureEvent.values()) {
            copy.put(e, eventCount(e));
        }
        return copy;
    }

    private void record(StructureEvent event) {
        events.merge(event, 1L, Long::sum);
    }

    @SuppressWarnings("unchecked")
    private K key(Node x, int i) { return (K) x.keys[i]; }

    @SuppressWarnings("unchecked")
    private V value(Node x, int i) { return (V) x.values[i]; }

    /** x 에서 k 이상인 첫 위치 */
    private int lowerBound(Node x, K k) {
        int lo = 0;
        int hi = x.n;
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (key(x, mid).compareTo(k) < 0) lo = mid + 1; else hi = mid;
        }
        return lo;
    }

    /** x 에서 k 초과인 첫 위치 */
    private int upperBound(Node x, K k) {
        int lo = 0;
        int hi = x.n;
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (key(x, mid).compareTo(k) <= 0) lo = mid + 1; else hi = mid;
        }
        return lo;
    }

    // ===================================================================== search

    /** 키 검색. 내부 노드에서 찾으면 바로 반환한다. 없으면 null. */
    public V search(K k) {
        Objects.requireNonNull(k, "key");
        Node x = root;
        while (true) {
            int i = lowerBound(x, k);
            if (i < x.n && key(x, i).compareTo(k) == 0) return value(x, i);
            if (x.leaf) return null;
            x = x.children[i];
        }
    }

    public boolean contains(K k) {
        return search(k) != null; // 값은 null 이 될 수 없다
    }

    // ===================================================================== insert

    /**
     * 삽입. 이미 있는 키면 false 를 반환하고 아무것도 바꾸지 않는다.
     * 내려가는 경로에서 가득 찬 노드를 미리 분할하므로 리프에는 항상 빈자리가 있다.
     */
    public boolean insert(K k, V v) {
        Objects.requireNonNull(k, "key");
        Objects.requireNonNull(v, "value");
        if (contains(k)) {
            record(StructureEvent.DUPLICATE_INSERT_REJECTED);
            return false;
        }
        if (root.n == 2 * t - 1) {
            Node s = new Node(t, false);
            s.children[0] = root;
            root = s;
            splitChild(s, 0);
            record(StructureEvent.ROOT_SPLIT);
        }
        insertNonFull(root, k, v);
        size++;
        return true;
    }

    /** 가득 찬 x.children[i] 를 [t-1][중앙][t-1] 로 나누고 중앙 항목을 x 로 올린다. */
    private void splitChild(Node x, int i) {
        Node y = x.children[i];
        Node z = new Node(t, y.leaf);
        z.n = t - 1;
        System.arraycopy(y.keys, t, z.keys, 0, t - 1);
        System.arraycopy(y.values, t, z.values, 0, t - 1);
        if (!y.leaf) {
            System.arraycopy(y.children, t, z.children, 0, t);
            Arrays.fill(y.children, t, 2 * t, null);
        }
        Object midKey = y.keys[t - 1];
        Object midValue = y.values[t - 1];
        Arrays.fill(y.keys, t - 1, 2 * t - 1, null);
        Arrays.fill(y.values, t - 1, 2 * t - 1, null);
        y.n = t - 1;

        System.arraycopy(x.children, i + 1, x.children, i + 2, x.n - i);
        x.children[i + 1] = z;
        System.arraycopy(x.keys, i, x.keys, i + 1, x.n - i);
        System.arraycopy(x.values, i, x.values, i + 1, x.n - i);
        x.keys[i] = midKey;
        x.values[i] = midValue;
        x.n++;
        record(y.leaf ? StructureEvent.LEAF_SPLIT : StructureEvent.INTERNAL_SPLIT);
    }

    private void insertNonFull(Node x, K k, V v) {
        while (true) {
            int i = lowerBound(x, k);
            if (x.leaf) {
                System.arraycopy(x.keys, i, x.keys, i + 1, x.n - i);
                System.arraycopy(x.values, i, x.values, i + 1, x.n - i);
                x.keys[i] = k;
                x.values[i] = v;
                x.n++;
                return;
            }
            if (x.children[i].n == 2 * t - 1) {
                splitChild(x, i);
                if (k.compareTo(key(x, i)) > 0) i++;
            }
            x = x.children[i];
        }
    }

    // ===================================================================== delete

    /**
     * 삭제. 삭제된 값을 반환한다. 키가 없으면 null 을 반환하며 내용과 구조를 바꾸지 않는다.
     * (하향식 삭제는 내려가며 미리 병합·재분배하므로, 존재 여부를 먼저 확인한다.)
     */
    public V delete(K k) {
        Objects.requireNonNull(k, "key");
        if (!contains(k)) {
            record(StructureEvent.DELETE_MISSING_KEY);
            return null;
        }
        V removed = delete(root, k);
        if (root.n == 0 && !root.leaf) {
            Node old = root;
            root = root.children[0];
            old.children[0] = null;
            record(StructureEvent.ROOT_SHRINK);
        }
        size--;
        return removed;
    }

    /**
     * 하향식 삭제 (CLRS). 진입하는 노드는 (루트 제외) 키가 t 개 이상임이 보장된다.
     *  경우 1 : 키가 리프 x 에 있음 → 제거
     *  경우 2a: 키가 내부 노드 x 에 있고 왼쪽 자식 키 ≥ t → 선행자로 대체 후 선행자 삭제
     *  경우 2b: 오른쪽 자식 키 ≥ t → 후속자로 대체 후 후속자 삭제
     *  경우 2c: 양쪽 자식 모두 t-1 → 키와 두 자식을 병합한 뒤 병합된 노드에서 삭제
     *  경우 3 : 키가 x 에 없음 → 내려갈 자식이 t-1 개면 형제에게서 재분배(3a), 불가능하면 병합(3b)
     */
    private V delete(Node x, K k) {
        int i = lowerBound(x, k);
        if (i < x.n && key(x, i).compareTo(k) == 0) {
            V found = value(x, i);
            if (x.leaf) {
                removeFromLeaf(x, i);
                record(StructureEvent.DELETE_FROM_LEAF);
                return found;
            }
            Node y = x.children[i];
            Node z = x.children[i + 1];
            if (y.n >= t) {
                Node p = y;
                while (!p.leaf) p = p.children[p.n];
                K predKey = key(p, p.n - 1);
                x.keys[i] = predKey;
                x.values[i] = p.values[p.n - 1];
                record(StructureEvent.DELETE_INTERNAL_BY_PREDECESSOR);
                delete(y, predKey);
            } else if (z.n >= t) {
                Node p = z;
                while (!p.leaf) p = p.children[0];
                K succKey = key(p, 0);
                x.keys[i] = succKey;
                x.values[i] = p.values[0];
                record(StructureEvent.DELETE_INTERNAL_BY_SUCCESSOR);
                delete(z, succKey);
            } else {
                merge(x, i);
                record(StructureEvent.DELETE_INTERNAL_BY_MERGE);
                delete(y, k);
            }
            return found;
        }
        if (x.leaf) {
            // 공개 delete() 가 존재를 먼저 확인하므로 정상 경로에서는 도달하지 않는다.
            throw new IllegalStateException("존재 확인 후에도 키를 찾지 못함: " + k);
        }
        if (x.children[i].n == t - 1) {
            if (i > 0 && x.children[i - 1].n >= t) {
                borrowFromLeft(x, i);
            } else if (i < x.n && x.children[i + 1].n >= t) {
                borrowFromRight(x, i);
            } else if (i < x.n) {
                merge(x, i);
            } else {
                merge(x, i - 1);
                i--;
            }
        }
        return delete(x.children[i], k);
    }

    private void removeFromLeaf(Node x, int i) {
        System.arraycopy(x.keys, i + 1, x.keys, i, x.n - i - 1);
        System.arraycopy(x.values, i + 1, x.values, i, x.n - i - 1);
        x.n--;
        x.keys[x.n] = null;
        x.values[x.n] = null;
    }

    /** 재분배: 왼쪽 형제 마지막 키 → 부모, 부모 구분 키 → children[i] 맨 앞 */
    private void borrowFromLeft(Node x, int i) {
        Node c = x.children[i];
        Node left = x.children[i - 1];
        System.arraycopy(c.keys, 0, c.keys, 1, c.n);
        System.arraycopy(c.values, 0, c.values, 1, c.n);
        c.keys[0] = x.keys[i - 1];
        c.values[0] = x.values[i - 1];
        if (!c.leaf) {
            System.arraycopy(c.children, 0, c.children, 1, c.n + 1);
            c.children[0] = left.children[left.n];
            left.children[left.n] = null;
        }
        x.keys[i - 1] = left.keys[left.n - 1];
        x.values[i - 1] = left.values[left.n - 1];
        left.keys[left.n - 1] = null;
        left.values[left.n - 1] = null;
        left.n--;
        c.n++;
        record(c.leaf ? StructureEvent.BORROW_LEFT_LEAF : StructureEvent.BORROW_LEFT_INTERNAL);
    }

    /** 재분배: 오른쪽 형제 첫 키 → 부모, 부모 구분 키 → children[i] 맨 뒤 */
    private void borrowFromRight(Node x, int i) {
        Node c = x.children[i];
        Node right = x.children[i + 1];
        c.keys[c.n] = x.keys[i];
        c.values[c.n] = x.values[i];
        if (!c.leaf) {
            c.children[c.n + 1] = right.children[0];
            System.arraycopy(right.children, 1, right.children, 0, right.n);
            right.children[right.n] = null;
        }
        x.keys[i] = right.keys[0];
        x.values[i] = right.values[0];
        System.arraycopy(right.keys, 1, right.keys, 0, right.n - 1);
        System.arraycopy(right.values, 1, right.values, 0, right.n - 1);
        right.n--;
        right.keys[right.n] = null;
        right.values[right.n] = null;
        c.n++;
        record(c.leaf ? StructureEvent.BORROW_RIGHT_LEAF : StructureEvent.BORROW_RIGHT_INTERNAL);
    }

    /** 병합: children[i] + 부모 키 i + children[i+1] → children[i]. 오른쪽 노드는 참조를 끊는다. */
    private void merge(Node x, int i) {
        Node y = x.children[i];
        Node z = x.children[i + 1];
        y.keys[y.n] = x.keys[i];
        y.values[y.n] = x.values[i];
        System.arraycopy(z.keys, 0, y.keys, y.n + 1, z.n);
        System.arraycopy(z.values, 0, y.values, y.n + 1, z.n);
        if (!y.leaf) {
            System.arraycopy(z.children, 0, y.children, y.n + 1, z.n + 1);
        }
        y.n += z.n + 1;

        System.arraycopy(x.keys, i + 1, x.keys, i, x.n - i - 1);
        System.arraycopy(x.values, i + 1, x.values, i, x.n - i - 1);
        System.arraycopy(x.children, i + 2, x.children, i + 1, x.n - i - 1);
        x.children[x.n] = null;
        x.n--;
        x.keys[x.n] = null;
        x.values[x.n] = null;
        record(y.leaf ? StructureEvent.LEAF_MERGE : StructureEvent.INTERNAL_MERGE);
    }

    // ===================================================================== range / traversal

    /** low ≤ key ≤ high 인 항목 (양 끝 포함, null 이면 무한대) */
    public List<Entry<K, V>> rangeSearch(K low, K high) {
        return rangeSearch(low, true, high, true, Integer.MAX_VALUE);
    }

    /**
     * 범위 검색. 키 오름차순으로 최대 limit 개를 반환한다.
     * 리프 연결이 없으므로 가지치기한 중위 순회로 구간을 훑는다: O(t·log_t n + 결과 수).
     */
    public List<Entry<K, V>> rangeSearch(K low, boolean lowInclusive, K high, boolean highInclusive, int limit) {
        List<Entry<K, V>> out = new ArrayList<>();
        if (limit <= 0) return out;
        if (low != null && high != null) {
            int c = low.compareTo(high);
            if (c > 0 || (c == 0 && !(lowInclusive && highInclusive))) return out;
        }
        collect(root, low, lowInclusive, high, highInclusive, limit, out);
        return out;
    }

    /** @return 계속 순회해야 하면 true, 상한 초과 또는 limit 도달이면 false */
    private boolean collect(Node x, K low, boolean lowInc, K high, boolean highInc, int limit, List<Entry<K, V>> out) {
        int i = (low == null) ? 0 : (lowInc ? lowerBound(x, low) : upperBound(x, low));
        for (; i < x.n; i++) {
            if (!x.leaf && !collect(x.children[i], low, lowInc, high, highInc, limit, out)) return false;
            K k = key(x, i);
            if (high != null) {
                int c = k.compareTo(high);
                if (c > 0 || (c == 0 && !highInc)) return false;
            }
            out.add(new Entry<>(k, value(x, i)));
            if (out.size() >= limit) return false;
        }
        return x.leaf || collect(x.children[x.n], low, lowInc, high, highInc, limit, out);
    }

    /** 전체 항목을 키 오름차순으로 반환 (중위 순회) */
    public List<Entry<K, V>> inOrder() {
        List<Entry<K, V>> out = new ArrayList<>(size);
        forEach((k, v) -> out.add(new Entry<>(k, v)));
        return out;
    }

    /** 키 오름차순으로 각 항목에 action 을 적용한다. */
    public void forEach(BiConsumer<? super K, ? super V> action) {
        Objects.requireNonNull(action, "action");
        walk(root, action);
    }

    private void walk(Node x, BiConsumer<? super K, ? super V> action) {
        for (int i = 0; i < x.n; i++) {
            if (!x.leaf) walk(x.children[i], action);
            action.accept(key(x, i), value(x, i));
        }
        if (!x.leaf) walk(x.children[x.n], action);
    }

    // ===================================================================== snapshot

    /** 구조 검사용 읽기 전용 복사본을 만든다. O(n) */
    public TreeSnapshot<K, V> snapshot() {
        return new TreeSnapshot<>(t, size, snap(root, new IdentityHashMap<Node, Integer>()));
    }

    private NodeSnapshot<K, V> snap(Node x, IdentityHashMap<Node, Integer> ids) {
        Integer seen = ids.get(x);
        if (seen != null) {
            // 같은 노드가 두 번 참조됨: 순환을 피하려고 내용 없이 같은 id 만 남긴다 (검사기가 ALIAS 로 보고)
            return new NodeSnapshot<>(seen, true, Collections.<K>emptyList(), Collections.<V>emptyList(),
                    Collections.<NodeSnapshot<K, V>>emptyList(), 2 * t - 1, 0);
        }
        int id = ids.size();
        ids.put(x, id);
        List<K> keys = new ArrayList<>(x.n);
        List<V> values = new ArrayList<>(x.n);
        for (int i = 0; i < x.n; i++) {
            keys.add(key(x, i));
            values.add(value(x, i));
        }
        int stale = 0;
        for (int i = x.n; i < x.keys.length; i++) {
            if (x.keys[i] != null || x.values[i] != null) stale++;
        }
        List<NodeSnapshot<K, V>> children = new ArrayList<>();
        if (!x.leaf) {
            for (int i = x.n + 1; i < x.children.length; i++) {
                if (x.children[i] != null) stale++;
            }
            for (int i = 0; i <= x.n; i++) {
                children.add(x.children[i] == null ? null : snap(x.children[i], ids));
            }
        }
        return new NodeSnapshot<>(id, x.leaf, keys, values, children, x.keys.length, stale);
    }
}
