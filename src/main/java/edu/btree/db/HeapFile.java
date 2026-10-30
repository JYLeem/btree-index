package edu.btree.db;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * 힙 파일 (교육용, 메모리 내 시뮬레이션): 레코드를 정렬 없이 페이지/슬롯에 저장한다.
 * 삭제된 슬롯은 free list 로 재사용한다. 디스크 I/O 는 없다.
 */
public final class HeapFile<R> {
    private final int slotsPerPage;
    private final List<Object[]> pages = new ArrayList<>();
    private final Deque<RecordId> freeSlots = new ArrayDeque<>();
    private int allocatedSlots;
    private int liveCount;

    public HeapFile(int slotsPerPage) {
        if (slotsPerPage < 1) {
            throw new IllegalArgumentException("페이지당 슬롯 수는 1 이상이어야 합니다: " + slotsPerPage);
        }
        this.slotsPerPage = slotsPerPage;
    }

    public RecordId insert(R record) {
        if (record == null) throw new NullPointerException("record");
        RecordId rid;
        if (!freeSlots.isEmpty()) {
            rid = freeSlots.pop();
        } else {
            int page = allocatedSlots / slotsPerPage;
            if (page == pages.size()) pages.add(new Object[slotsPerPage]);
            rid = new RecordId(page, allocatedSlots % slotsPerPage);
            allocatedSlots++;
        }
        pages.get(rid.page())[rid.slot()] = record;
        liveCount++;
        return rid;
    }

    @SuppressWarnings("unchecked")
    public R read(RecordId rid) {
        if (rid == null || rid.page() >= pages.size() || rid.slot() >= slotsPerPage) return null;
        return (R) pages.get(rid.page())[rid.slot()];
    }

    public R delete(RecordId rid) {
        R old = read(rid);
        if (old == null) return null;
        pages.get(rid.page())[rid.slot()] = null;
        freeSlots.push(rid);
        liveCount--;
        return old;
    }

    public int liveCount() { return liveCount; }
    public int pageCount() { return pages.size(); }
}
