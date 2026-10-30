package edu.btree.db;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/** orders 테이블의 행. 생성 시 모든 값을 검증하므로 잘못된 레코드는 만들어질 수 없다. */
public final class OrderRecord {
    private final long orderId;
    private final String customerId;
    private final long amount;
    private final LocalDate orderDate;

    public OrderRecord(long orderId, String customerId, long amount, LocalDate orderDate) {
        if (orderId <= 0) {
            throw new IllegalArgumentException("order_id 는 양수여야 합니다: " + orderId);
        }
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException("customer_id 가 비어 있습니다.");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("amount 는 0 이상이어야 합니다: " + amount);
        }
        this.orderId = orderId;
        this.customerId = customerId;
        this.amount = amount;
        this.orderDate = Objects.requireNonNull(orderDate, "order_date");
    }

    /** "yyyy-MM-dd" 문자열 날짜를 받는 생성 도우미. 존재하지 않는 날짜(2026-02-30)는 거부한다. */
    public static OrderRecord of(long orderId, String customerId, long amount, String date) {
        LocalDate parsed;
        try {
            parsed = LocalDate.parse(Objects.requireNonNull(date, "order_date"));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("order_date 가 올바른 yyyy-MM-dd 날짜가 아닙니다: " + date);
        }
        return new OrderRecord(orderId, customerId, amount, parsed);
    }

    public long orderId() { return orderId; }
    public String customerId() { return customerId; }
    public long amount() { return amount; }
    public LocalDate orderDate() { return orderDate; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderRecord)) return false;
        OrderRecord r = (OrderRecord) o;
        return orderId == r.orderId && amount == r.amount
                && customerId.equals(r.customerId) && orderDate.equals(r.orderDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, customerId, amount, orderDate);
    }

    @Override
    public String toString() {
        return orderId + " " + customerId + " " + amount + " " + orderDate;
    }
}
