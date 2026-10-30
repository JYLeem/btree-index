package edu.btree.test;

import edu.btree.core.TreeSnapshot;
import edu.btree.db.DbCommandProcessor;
import edu.btree.db.OrderRecord;
import edu.btree.db.OrderTable;
import edu.btree.db.RecordId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

/** 사례 A(데이터베이스 인덱스) 기능 테스트 — 기대 출력은 손으로 정한 값 또는 TreeMap 모델 */
final class DbModuleTest {
    private DbModuleTest() {
    }

    static void run(TestRecorder r, long seed) {
        r.beginSuite("7. DB 인덱스 모듈 기능 테스트");

        DbCommandProcessor p = new DbCommandProcessor(new OrderTable(2, 4));
        String[][] script = {
                {"INSERT 1001 C-ALICE 15000 2026-10-01", "OK INSERTED 1001 -> RID(0,0)"},
                {"INSERT 1002 C-BOB 22000 2026-10-02", "OK INSERTED 1002 -> RID(0,1)"},
                {"SELECT 1001", "OK 1001 C-ALICE 15000 2026-10-01 @RID(0,0)"},
                {"INSERT 1001 C-EVE 1 2026-10-03", "FAIL DUPLICATE_KEY 1001"},
                {"SELECT 1001", "OK 1001 C-ALICE 15000 2026-10-01 @RID(0,0)"},
                {"SELECT 9999", "FAIL NOT_FOUND 9999"},
                {"DELETE 1001", "OK DELETED 1001"},
                {"DELETE 1001", "FAIL NOT_FOUND 1001"},
                {"INSERT 1003 C-CAROL 5000 2026-10-03", "OK INSERTED 1003 -> RID(0,0)"},
                {"RANGE 1000 1002", "OK 1 [1002]"},
                {"SCAN", "OK 2 [1002, 1003]"},
                {"RANGE 5 1", "ERROR 범위 하한이 상한보다 큽니다: 5 > 1"},
                {"INSERT -1 C 1 2026-10-01", "ERROR order_id 는 양수여야 합니다: -1"},
                {"INSERT 1 C 1 2026-02-30", "ERROR order_date 가 올바른 yyyy-MM-dd 날짜가 아닙니다: 2026-02-30"},
                {"INSERT 1 C -5 2026-01-01", "ERROR amount 는 0 이상이어야 합니다: -5"},
                {"SELECT abc", "ERROR order_id 는 정수여야 합니다: abc"},
                {"SELECT", "ERROR 인자 개수 오류, 사용법: SELECT <order_id>"},
                {"UPDATE 1", "ERROR 알 수 없는 명령: UPDATE"},
                {"   # 주석", "null"},
                {"", "null"},
        };
        for (int i = 0; i < script.length; i++) {
            r.record(String.format("DB-C%02d", i + 1), "명령 처리기 출력", script[i][0], script[i][1], p.process(script[i][0]));
        }

        // 범위 검색: 섞인 순서로 삽입한 id 의 BETWEEN
        OrderTable table = new OrderTable(2, 4);
        List<Long> ids = new ArrayList<>();
        for (long id = 1; id <= 49; id += 3) ids.add(id);
        Collections.shuffle(ids, new Random(seed));
        for (long id : ids) table.insert(OrderRecord.of(id, "C" + id, id * 100, "2026-10-01"));
        List<Long> got = new ArrayList<>();
        for (OrderRecord o : table.findByIdRange(10, 30)) got.add(o.orderId());
        r.record("DB-R01", "BETWEEN 10 AND 30 결과가 order_id 오름차순", "id 1,4,…,49 (17개)를 섞어서 삽입, t=2",
                "[10, 13, 16, 19, 22, 25, 28]", got);
        List<Long> all = new ArrayList<>();
        for (OrderRecord o : table.scanAll()) all.add(o.orderId());
        List<Long> sorted = new ArrayList<>(ids);
        Collections.sort(sorted);
        r.record("DB-R02", "SCAN 이 전체를 오름차순으로 반환", "위와 같은 테이블", sorted, all);

        // RID 재사용과 인덱스·힙 일관성
        OrderTable reuse = new OrderTable(3, 4);
        for (long id = 1; id <= 10; id++) reuse.insert(OrderRecord.of(id, "C", 1, "2026-10-01"));
        RecordId freed = reuse.ridOf(5);
        reuse.deleteById(5);
        reuse.insert(OrderRecord.of(100, "C", 1, "2026-10-01"));
        r.record("DB-R03", "삭제된 힙 슬롯 재사용", "id 1..10 삽입, delete 5, insert 100", freed, reuse.ridOf(100));

        // 무작위 작업을 TreeMap 모델과 비교
        Random rnd = new Random(seed + 11);
        OrderTable rt = new OrderTable(3, 8);
        TreeMap<Long, OrderRecord> model = new TreeMap<>();
        String divergence = null;
        for (int i = 0; i < 5_000 && divergence == null; i++) {
            long id = 1 + rnd.nextInt(800);
            int op = rnd.nextInt(10);
            if (op < 5) {
                OrderRecord rec = OrderRecord.of(id, "C" + rnd.nextInt(50), rnd.nextInt(100_000), "2026-10-" + (10 + rnd.nextInt(20)));
                boolean expected = !model.containsKey(id);
                if (expected) model.put(id, rec);
                boolean actual = rt.insert(rec) == OrderTable.InsertResult.INSERTED;
                if (actual != expected) divergence = "insert " + id;
            } else if (op < 8) {
                OrderRecord expected = model.remove(id);
                OrderRecord actual = rt.deleteById(id);
                if (expected == null ? actual != null : !expected.equals(actual)) divergence = "delete " + id;
            } else {
                OrderRecord expected = model.get(id);
                OrderRecord actual = rt.findById(id);
                if (expected == null ? actual != null : !expected.equals(actual)) divergence = "select " + id;
            }
        }
        if (divergence == null && !rt.scanAll().equals(new ArrayList<>(model.values()))) divergence = "SCAN ≠ 모델";
        r.record("DB-M01", "무작위 INSERT/DELETE/SELECT 5,000회가 TreeMap 모델과 일치", "id 1~800, t=3, seed=" + (seed + 11),
                "일치", divergence == null ? "일치" : "불일치: " + divergence);
        r.record("DB-M02", "인덱스 항목 수 = 힙 레코드 수 = 모델 크기", "위와 동일",
                model.size() + "/" + model.size() + "/" + model.size(),
                rt.rowCount() + "/" + rt.heapLiveCount() + "/" + model.size());
        TreeSnapshot<Long, RecordId> snap = rt.index().snapshot();
        r.record("DB-M03", "작업 후 기본키 인덱스 B-Tree 불변식", "위와 동일", "위반 0건",
                InvariantChecker.summary(InvariantChecker.check(snap, (k, v) -> v != null)));
    }
}
