package edu.btree.db;

import java.util.ArrayList;
import java.util.List;

/**
 * DB 명령 한 줄을 실행하고 결과 문자열을 돌려준다. (파일 입출력과 분리해 테스트하기 쉽게 함)
 * <pre>
 * INSERT id customer amount yyyy-MM-dd | SELECT id | DELETE id | RANGE low high | SCAN
 * </pre>
 * 결과: "OK ...", "FAIL &lt;사유&gt; ...", "ERROR &lt;메시지&gt;". 빈 줄·주석(#)은 null.
 */
public final class DbCommandProcessor {
    private final OrderTable table;
    private int ok;
    private int fail;
    private int error;

    public DbCommandProcessor(OrderTable table) {
        this.table = table;
    }

    public String process(String line) {
        if (line == null) return null;
        String trimmed = line.trim();
        if (trimmed.isEmpty() || trimmed.startsWith("#")) return null;
        String[] p = trimmed.split("\\s+");
        String cmd = p[0].toUpperCase();
        try {
            String result;
            switch (cmd) {
                case "INSERT": {
                    expectArgs(p, 5, "INSERT <order_id> <customer_id> <amount> <yyyy-MM-dd>");
                    OrderRecord r = OrderRecord.of(parseLong(p[1], "order_id"), p[2], parseLong(p[3], "amount"), p[4]);
                    if (table.insert(r) == OrderTable.InsertResult.INSERTED) {
                        result = "OK INSERTED " + r.orderId() + " -> " + table.ridOf(r.orderId());
                    } else {
                        result = "FAIL DUPLICATE_KEY " + r.orderId();
                    }
                    break;
                }
                case "SELECT": {
                    expectArgs(p, 2, "SELECT <order_id>");
                    long id = parseLong(p[1], "order_id");
                    OrderRecord r = table.findById(id);
                    result = r == null ? "FAIL NOT_FOUND " + id : "OK " + r + " @" + table.ridOf(id);
                    break;
                }
                case "DELETE": {
                    expectArgs(p, 2, "DELETE <order_id>");
                    long id = parseLong(p[1], "order_id");
                    result = table.deleteById(id) == null ? "FAIL NOT_FOUND " + id : "OK DELETED " + id;
                    break;
                }
                case "RANGE": {
                    expectArgs(p, 3, "RANGE <low> <high>");
                    List<OrderRecord> rows = table.findByIdRange(parseLong(p[1], "low"), parseLong(p[2], "high"));
                    result = "OK " + rows.size() + " " + ids(rows);
                    break;
                }
                case "SCAN": {
                    expectArgs(p, 1, "SCAN");
                    List<OrderRecord> rows = table.scanAll();
                    result = "OK " + rows.size() + " " + ids(rows);
                    break;
                }
                default:
                    throw new IllegalArgumentException("알 수 없는 명령: " + p[0]);
            }
            if (result.startsWith("OK")) ok++; else fail++;
            return result;
        } catch (IllegalArgumentException e) {
            error++;
            return "ERROR " + e.getMessage();
        }
    }

    private static List<Long> ids(List<OrderRecord> rows) {
        List<Long> ids = new ArrayList<>();
        for (OrderRecord r : rows) ids.add(r.orderId());
        return ids;
    }

    private static void expectArgs(String[] p, int n, String usage) {
        if (p.length != n) throw new IllegalArgumentException("인자 개수 오류, 사용법: " + usage);
    }

    private static long parseLong(String s, String name) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(name + " 는 정수여야 합니다: " + s);
        }
    }

    public String summary() {
        return "# summary ok=" + ok + " fail=" + fail + " error=" + error + " rows=" + table.rowCount()
                + " indexHeight=" + table.index().height();
    }
}
