package edu.btree.db;

import edu.btree.core.Entry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * orders 테이블 = 힙 파일 + order_id 기본키 인덱스.
 * 모든 변경은 힙과 인덱스를 함께 갱신해 "인덱스 항목 수 = 힙 레코드 수" 를 유지한다.
 */
public final class OrderTable {

    public enum InsertResult { INSERTED, DUPLICATE_KEY }

    private final HeapFile<OrderRecord> heap;
    private final PrimaryKeyIndex index;

    public OrderTable(int minDegree, int slotsPerPage) {
        this.heap = new HeapFile<>(slotsPerPage);
        this.index = new PrimaryKeyIndex(minDegree);
    }

    /** INSERT. 중복 order_id 는 힙도 건드리지 않고 거부한다. */
    public InsertResult insert(OrderRecord record) {
        Objects.requireNonNull(record, "record");
        if (index.contains(record.orderId())) return InsertResult.DUPLICATE_KEY;
        RecordId rid = heap.insert(record);
        if (!index.add(record.orderId(), rid)) {
            heap.delete(rid); // 방어적 롤백
            return InsertResult.DUPLICATE_KEY;
        }
        return InsertResult.INSERTED;
    }

    /** SELECT ... WHERE order_id = ? (없으면 null) */
    public OrderRecord findById(long orderId) {
        RecordId rid = index.find(orderId);
        return rid == null ? null : heap.read(rid);
    }

    public RecordId ridOf(long orderId) {
        return index.find(orderId);
    }

    /** DELETE ... WHERE order_id = ? (없으면 null, 아무것도 바꾸지 않음) */
    public OrderRecord deleteById(long orderId) {
        RecordId rid = index.remove(orderId);
        return rid == null ? null : heap.delete(rid);
    }

    /** SELECT ... WHERE order_id BETWEEN low AND high ORDER BY order_id */
    public List<OrderRecord> findByIdRange(long low, long high) {
        List<OrderRecord> out = new ArrayList<>();
        for (Entry<Long, RecordId> e : index.range(low, high)) out.add(heap.read(e.value()));
        return out;
    }

    /** 전체 행을 order_id 오름차순으로 */
    public List<OrderRecord> scanAll() {
        List<OrderRecord> out = new ArrayList<>();
        for (Entry<Long, RecordId> e : index.scan()) out.add(heap.read(e.value()));
        return out;
    }

    public int rowCount() { return index.size(); }
    public int heapLiveCount() { return heap.liveCount(); }
    public PrimaryKeyIndex index() { return index; }
}
