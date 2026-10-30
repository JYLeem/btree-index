package edu.btree.core;

/** 트리 전체의 읽기 전용 복사본 (최소 차수, 기록된 크기, 루트) */
public final class TreeSnapshot<K, V> {
    private final int minDegree;
    private final int size;
    private final NodeSnapshot<K, V> root;

    public TreeSnapshot(int minDegree, int size, NodeSnapshot<K, V> root) {
        this.minDegree = minDegree;
        this.size = size;
        this.root = root;
    }

    public int minDegree() { return minDegree; }
    public int size() { return size; }
    public NodeSnapshot<K, V> root() { return root; }

    public String structure() {
        return root == null ? "null" : root.structure();
    }
}
