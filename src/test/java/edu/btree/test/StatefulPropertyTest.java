package edu.btree.test;

import edu.btree.core.BTree;
import edu.btree.core.Entry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import java.util.function.Function;

/**
 * 3. Stateful property-based test
 * 무작위 명령 시퀀스를 생성해 실행하면서 매 명령 후 다음 속성을 검사한다.
 *  P1 반환값 = 모델(TreeMap)
 *  P2 모든 불변식 성립, 내용 = 모델
 *  P3 search / rangeSearch 는 구조를 바꾸지 않는다
 *  P4 중복 insert, 없는 키 delete 는 구조를 바꾸지 않는다
 *  P5 insert 직후 검색됨, delete 직후 검색 안 됨
 * 실패하면 축소(shrinking)로 최소 반례를 찾아 기록한다.
 */
final class StatefulPropertyTest {
    private StatefulPropertyTest() {
    }

    static final class Command {
        final char op; // I D S R
        final int a;
        final int b;
        final boolean loInc;
        final boolean hiInc;
        final int limit;

        Command(char op, int a, int b, boolean loInc, boolean hiInc, int limit) {
            this.op = op;
            this.a = a;
            this.b = b;
            this.loInc = loInc;
            this.hiInc = hiInc;
            this.limit = limit;
        }

        Command withKeys(int na, int nb) {
            return new Command(op, na, nb, loInc, hiInc, limit);
        }

        @Override
        public String toString() {
            switch (op) {
                case 'I': return "insert(" + a + ")";
                case 'D': return "delete(" + a + ")";
                case 'S': return "search(" + a + ")";
                default:
                    return "range(" + (loInc ? "[" : "(") + a + "," + b + (hiInc ? "]" : ")")
                            + (limit == Integer.MAX_VALUE ? "" : ",limit " + limit) + ")";
            }
        }
    }

    static List<Command> generate(Random rnd, int maxLength) {
        int domain = 4 + rnd.nextInt(rnd.nextBoolean() ? 30 : 300);
        int length = 1 + rnd.nextInt(maxLength);
        List<Command> cmds = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            int p = rnd.nextInt(100);
            int a = rnd.nextInt(domain);
            if (p < 45) cmds.add(new Command('I', a, 0, true, true, 0));
            else if (p < 80) cmds.add(new Command('D', a, 0, true, true, 0));
            else if (p < 90) cmds.add(new Command('S', a, 0, true, true, 0));
            else cmds.add(new Command('R', a, a + rnd.nextInt(domain / 2 + 1), rnd.nextBoolean(), rnd.nextBoolean(),
                        rnd.nextInt(4) == 0 ? rnd.nextInt(4) : Integer.MAX_VALUE));
        }
        return cmds;
    }

    /** 시퀀스 실행. 모든 속성이 성립하면 null, 아니면 위반 설명. */
    static String runSequence(int t, List<Command> cmds) {
        BTree<Integer, Integer> tree = new BTree<>(t);
        TreeMap<Integer, Integer> model = new TreeMap<>();
        for (int i = 0; i < cmds.size(); i++) {
            Command c = cmds.get(i);
            String before = tree.snapshot().structure();
            boolean mustNotChange;
            switch (c.op) {
                case 'I': {
                    boolean expected = !model.containsKey(c.a);
                    if (expected) model.put(c.a, ModelHarness.valueOf(c.a));
                    boolean actual = tree.insert(c.a, ModelHarness.valueOf(c.a));
                    if (actual != expected) return "P1 단계 " + i + " " + c + " 반환 " + actual + " ≠ " + expected;
                    if (tree.search(c.a) == null) return "P5 단계 " + i + " " + c + " 직후 검색 실패";
                    mustNotChange = !expected;
                    break;
                }
                case 'D': {
                    Integer expected = model.remove(c.a);
                    Integer actual = tree.delete(c.a);
                    if (expected == null ? actual != null : !expected.equals(actual)) {
                        return "P1 단계 " + i + " " + c + " 반환 " + actual + " ≠ " + expected;
                    }
                    if (tree.search(c.a) != null) return "P5 단계 " + i + " " + c + " 직후에도 검색됨";
                    mustNotChange = expected == null;
                    break;
                }
                case 'S': {
                    Integer expected = model.get(c.a);
                    Integer actual = tree.search(c.a);
                    if (expected == null ? actual != null : !expected.equals(actual)) {
                        return "P1 단계 " + i + " " + c + " 반환 " + actual + " ≠ " + expected;
                    }
                    mustNotChange = true;
                    break;
                }
                default: {
                    List<Integer> actual = new ArrayList<>();
                    for (Entry<Integer, Integer> e : tree.rangeSearch(c.a, c.loInc, c.b, c.hiInc, c.limit)) {
                        actual.add(e.key());
                    }
                    List<Integer> expected = ModelHarness.expectedRange(model, c.a, c.loInc, c.b, c.hiInc, c.limit);
                    if (!actual.equals(expected)) return "P1 단계 " + i + " " + c + " 결과 " + actual + " ≠ " + expected;
                    mustNotChange = true;
                    break;
                }
            }
            if (mustNotChange && !before.equals(tree.snapshot().structure())) {
                return "P3/P4 단계 " + i + " " + c + " 가 구조를 변경함";
            }
            List<String> v = InvariantChecker.check(tree.snapshot(), ModelHarness.VALUE_RULE);
            if (!v.isEmpty()) return "P2 단계 " + i + " " + c + " 후 " + InvariantChecker.summary(v);
            List<Integer> keys = new ArrayList<>();
            for (Entry<Integer, Integer> e : tree.inOrder()) keys.add(e.key());
            if (!keys.equals(new ArrayList<>(model.keySet()))) return "P2 단계 " + i + " " + c + " 후 내용 ≠ 모델";
        }
        return null;
    }

    /** 축소: 명령 덩어리 제거(delta debugging) → 키 단순화. 반복 상한 50회. */
    static List<Command> shrink(List<Command> failing, Function<List<Command>, String> property) {
        List<Command> cur = new ArrayList<>(failing);
        boolean progress = true;
        int rounds = 0;
        while (progress && rounds++ < 50) {
            progress = false;
            for (int chunk = Math.max(1, cur.size() / 2); chunk >= 1; chunk /= 2) {
                for (int start = 0; start + chunk <= cur.size(); ) {
                    List<Command> cand = new ArrayList<>(cur.subList(0, start));
                    cand.addAll(cur.subList(start + chunk, cur.size()));
                    if (!cand.isEmpty() && property.apply(cand) != null) {
                        cur = cand;
                        progress = true;
                    } else {
                        start += chunk;
                    }
                }
            }
            for (int i = 0; i < cur.size(); i++) {
                Command c = cur.get(i);
                for (int[] ab : new int[][]{{0, 0}, {c.a / 2, c.b / 2}, {c.a - 1, c.b - 1}}) {
                    if (ab[0] < 0 || (ab[0] == c.a && ab[1] == c.b)) continue;
                    List<Command> cand = new ArrayList<>(cur);
                    cand.set(i, c.withKeys(ab[0], Math.max(ab[0], ab[1])));
                    if (property.apply(cand) != null) {
                        cur = cand;
                        progress = true;
                        break;
                    }
                }
            }
        }
        return cur;
    }

    static void run(TestRecorder r, long seed) {
        r.beginSuite("3. Stateful property-based test");
        Random rnd = new Random(seed ^ 0x5DEECE66DL);
        int runs = 600;
        long commands = 0;
        String failure = null;
        String minimalText = "";
        for (int run = 0; run < runs && failure == null; run++) {
            int t = 2 + rnd.nextInt(4);
            List<Command> cmds = generate(rnd, 250);
            commands += cmds.size();
            String result = runSequence(t, cmds);
            if (result != null) {
                final int ft = t;
                List<Command> minimal = shrink(cmds, c -> runSequence(ft, c));
                failure = result;
                minimalText = " | 최소 반례(t=" + t + ", " + cmds.size() + "→" + minimal.size() + "개): " + minimal
                        + " → " + runSequence(t, minimal);
            }
        }
        r.record("PBT-01", "무작위 명령 시퀀스에서 속성 P1~P5 성립",
                "시퀀스 " + runs + "개(최대 길이 250, t=2~5, 키 도메인 4~303), seed=" + seed,
                "속성 위반 0건",
                failure == null ? "속성 위반 0건" : "위반: " + failure + minimalText);
        r.record("PBT-02", "생성된 명령 수 (실행 규모 기록)", "위와 동일", "1개 이상", commands > 0 ? "1개 이상" : "0개");

        // 축소기 자체 검증: 일부러 거짓인 속성 "100 이상인 키가 동시에 3개 이상 존재하지 않는다"
        Function<List<Command>, String> falseProperty = cmds -> {
            BTree<Integer, Integer> tree = new BTree<>(2);
            for (Command c : cmds) {
                if (c.op == 'I') tree.insert(c.a, ModelHarness.valueOf(c.a));
                else if (c.op == 'D') tree.delete(c.a);
                if (tree.rangeSearch(100, true, null, true, 3).size() >= 3) return "100 이상 키 3개";
            }
            return null;
        };
        Random r2 = new Random(seed + 7);
        List<Command> big = null;
        for (int attempt = 0; attempt < 10_000 && big == null; attempt++) {
            List<Command> cand = generate(r2, 250);
            if (cand.size() > 50 && falseProperty.apply(cand) != null) big = cand;
        }
        String actual;
        if (big == null) {
            actual = "반례 생성 실패";
        } else {
            List<Command> minimal = shrink(big, falseProperty);
            boolean shape = minimal.size() == 3;
            for (Command c : minimal) shape &= c.op == 'I' && c.a >= 100;
            actual = shape ? "insert 3개(키 ≥ 100)로 축소" : "축소 결과 " + minimal;
        }
        r.record("PBT-03", "축소기 자체 검증: 거짓 속성의 반례를 최소로 줄임",
                "거짓 속성 '100 이상 키 3개 미만', 원래 반례 " + (big == null ? 0 : big.size()) + "개 명령",
                "insert 3개(키 ≥ 100)로 축소", actual);
    }
}
