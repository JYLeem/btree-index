package edu.btree.db;

import edu.btree.core.BTree;
import edu.btree.core.Entry;
import edu.btree.core.TreeSnapshot;

import java.util.List;

/**
 * 유일(UNIQUE) 기본키 인덱스: order_id → RecordId.
 * 공통 B-Tree 를 조합해서 쓰며, DB 쪽 의미(유일성, 양 끝 포함 범위)만 덧붙인다.
 */
public final class PrimaryKeyIndex {
    private final BTree<Long, RecordId> tree;

    public PrimaryKeyIndex(int minDegree) {
        this.tree = new BTree<>(minDegree);
    }

    /** @return 새 키면 true, 이미 있으면 false (UNIQUE 위반) */
    public boolean add(long key, RecordId rid) {
        return tree.insert(key, rid);
    }

    public RecordId find(long key) {
        return tree.search(key);
    }

    public boolean contains(long key) {
        return tree.contains(key);
    }

    public RecordId remove(long key) {
        return tree.delete(key);
    }

    /** low ≤ key ≤ high (양 끝 포함) */
    public List<Entry<Long, RecordId>> range(long low, long high) {
        if (low > high) {
            throw new IllegalArgumentException("범위 하한이 상한보다 큽니다: " + low + " > " + high);
        }
        return tree.rangeSearch(low, high);
    }

    public List<Entry<Long, RecordId>> scan() {
        return tree.inOrder();
    }

    public int size() { return tree.size(); }
    public int height() { return tree.height(); }
    public TreeSnapshot<Long, RecordId> snapshot() { return tree.snapshot(); }
    /** 구조 이벤트 통계 확인용 (테스트·리포트) */
    public BTree<Long, RecordId> tree() { return tree; }
}
