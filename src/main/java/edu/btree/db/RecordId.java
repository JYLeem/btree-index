package edu.btree.db;

/** 레코드 식별자: 힙 파일의 (페이지 번호, 슬롯 번호). 불변 값 객체. */
public final class RecordId {
    private final int page;
    private final int slot;

    public RecordId(int page, int slot) {
        if (page < 0 || slot < 0) {
            throw new IllegalArgumentException("page/slot 은 0 이상이어야 합니다: " + page + "," + slot);
        }
        this.page = page;
        this.slot = slot;
    }

    public int page() { return page; }
    public int slot() { return slot; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecordId)) return false;
        RecordId r = (RecordId) o;
        return page == r.page && slot == r.slot;
    }

    @Override
    public int hashCode() {
        return 31 * page + slot;
    }

    @Override
    public String toString() {
        return "RID(" + page + "," + slot + ")";
    }
}
