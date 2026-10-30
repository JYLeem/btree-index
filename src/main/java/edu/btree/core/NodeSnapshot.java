package edu.btree.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 노드의 읽기 전용 복사본. 테스트의 불변식 검사기가 트리 내부를 직접 건드리지 않고
 * 구조를 검사할 수 있게 한다. 손상된 구조를 직접 만들어 검사기를 검증하는 데도 쓴다.
 */
public final class NodeSnapshot<K, V> {
    private final int id;
    private final boolean leaf;
    private final List<K> keys;
    private final List<V> values;
    private final List<NodeSnapshot<K, V>> children;
    private final int keyCapacity;
    private final int staleSlots;

    /**
     * @param id          노드 식별 번호 (같은 노드 객체는 같은 번호)
     * @param leaf        리프 여부
     * @param keys        사용 중인 키 (오름차순이어야 함)
     * @param values      키와 같은 순서의 값
     * @param children    자식 스냅샷 (리프면 빈 목록, null 원소는 누락된 자식)
     * @param keyCapacity 노드 배열의 키 용량 (2t-1)
     * @param staleSlots  사용하지 않는 슬롯에 남아 있는 참조 수 (0 이어야 함)
     */
    public NodeSnapshot(int id, boolean leaf, List<K> keys, List<V> values,
                        List<NodeSnapshot<K, V>> children, int keyCapacity, int staleSlots) {
        this.id = id;
        this.leaf = leaf;
        this.keys = Collections.unmodifiableList(new ArrayList<>(keys));
        this.values = Collections.unmodifiableList(new ArrayList<>(values));
        this.children = Collections.unmodifiableList(new ArrayList<>(children));
        this.keyCapacity = keyCapacity;
        this.staleSlots = staleSlots;
    }

    public int id() { return id; }
    public boolean isLeaf() { return leaf; }
    public List<K> keys() { return keys; }
    public List<V> values() { return values; }
    public List<NodeSnapshot<K, V>> children() { return children; }
    public int keyCapacity() { return keyCapacity; }
    public int staleSlots() { return staleSlots; }

    /** 구조 문자열. 리프 "(1 2)", 내부 노드 "((1 2) 3 (4 5))", 누락된 자식 "∅" */
    public String structure() {
        StringBuilder sb = new StringBuilder();
        append(this, sb);
        return sb.toString();
    }

    private static <K, V> void append(NodeSnapshot<K, V> node, StringBuilder sb) {
        if (node == null) {
            sb.append('∅');
            return;
        }
        sb.append('(');
        for (int i = 0; i < node.keys.size(); i++) {
            if (!node.leaf) {
                append(i < node.children.size() ? node.children.get(i) : null, sb);
                sb.append(' ');
            }
            sb.append(node.keys.get(i));
            if (i < node.keys.size() - 1 || !node.leaf) sb.append(' ');
        }
        if (!node.leaf) {
            int last = node.keys.size();
            append(last < node.children.size() ? node.children.get(last) : null, sb);
        }
        sb.append(')');
    }
}
