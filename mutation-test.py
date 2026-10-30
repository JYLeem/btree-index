#!/usr/bin/env python3
"""테스트 유효성 검증: BTree.java 에 결함(돌연변이)을 하나씩 심고 전체 테스트가 실패하는지 확인한다.
사용법: python3 mutation-test.py M1 M2 ...   (인자 없으면 전체)"""
import os, re, shutil, subprocess, sys
MUTANTS = {
 "M1": ("재분배(오른쪽) 시 자식 포인터를 옮기지 않음",
        "c.children[c.n + 1] = right.children[0];\n            System.arraycopy(right.children, 1, right.children, 0, right.n);",
        "System.arraycopy(right.children, 1, right.children, 0, right.n);"),
 "M2": ("병합 후 부모의 마지막 자식 슬롯을 비우지 않음",
        "x.children[x.n] = null;\n        x.n--;", "x.n--;"),
 "M3": ("upperBound 가 lowerBound 처럼 동작 (배타적 하한이 포함으로 바뀜)",
        "if (key(x, mid).compareTo(k) <= 0) lo = mid + 1; else hi = mid;", "if (key(x, mid).compareTo(k) < 0) lo = mid + 1; else hi = mid;"),
 "M4": ("delete 의 존재 확인 제거 (R-001 재발)",
        "if (!contains(k)) {\n            record(StructureEvent.DELETE_MISSING_KEY);\n            return null;\n        }\n        V removed = delete(root, k);",
        "V removed;\n        try { removed = delete(root, k); } catch (IllegalStateException e) { removed = null; }\n        if (removed == null) record(StructureEvent.DELETE_MISSING_KEY);"),
 "M9": ("R-001 원래 코드 그대로 복원 (존재 확인 없음, 리프에서 null 반환)",
        "if (!contains(k)) {\n            record(StructureEvent.DELETE_MISSING_KEY);\n            return null;\n        }\n        V removed = delete(root, k);",
        "V removed = delete(root, k);"),
 "M5": ("자식 분할 후 내려갈 자식 위치를 보정하지 않음",
        "if (k.compareTo(key(x, i)) > 0) i++;", ";"),
 "M6": ("경우 2a 조건 y.n >= t 를 y.n > t 로 (병합 시 넘침)",
        "if (y.n >= t) {\n                Node p = y;", "if (y.n > t) {\n                Node p = y;"),
 "M7": ("범위 검색 limit 비교 off-by-one",
        "if (out.size() >= limit) return false;", "if (out.size() > limit) return false;"),
 "M8": ("리프에서 키 제거 후 마지막 슬롯을 비우지 않음",
        "x.n--;\n        x.keys[x.n] = null;\n        x.values[x.n] = null;\n    }\n\n    /** 재분배: 왼쪽", "x.n--;\n    }\n\n    /** 재분배: 왼쪽"),
}
sel = sys.argv[1:] or list(MUTANTS)
src = open("src/main/java/edu/btree/core/BTree.java", encoding="utf-8").read()
os.makedirs("test-results/mutation", exist_ok=True)
env = dict(os.environ, JAVA_TOOL_OPTIONS="")
for mid in sel:
    desc, old, new = MUTANTS[mid]
    assert src.count(old) == 1, (mid, src.count(old))
    work = f"build/mut-{mid}"
    shutil.rmtree(work, ignore_errors=True)
    shutil.copytree("src/main", f"{work}/src")
    mutated = src.replace(old, new)
    if mid == "M9":
        extra_old = 'throw new IllegalStateException("존재 확인 후에도 키를 찾지 못함: " + k);'
        assert mutated.count(extra_old) == 1
        mutated = mutated.replace(extra_old, "return null;")
        mutated = mutated.replace("        size--;\n        return removed;", "        if (removed != null) size--;\n        return removed;")
    open(f"{work}/src/java/edu/btree/core/BTree.java", "w", encoding="utf-8").write(mutated)
    mains = [os.path.join(d, f) for d, _, fs in os.walk(f"{work}/src") for f in fs if f.endswith(".java")]
    tests = [os.path.join(d, f) for d, _, fs in os.walk("src/test") for f in fs if f.endswith(".java")]
    c1 = subprocess.run(["javac", "-encoding", "UTF-8", "-nowarn", "-d", f"{work}/main"] + mains, capture_output=True, text=True, env=env)
    c2 = subprocess.run(["javac", "-encoding", "UTF-8", "-nowarn", "-cp", f"{work}/main", "-d", f"{work}/test"] + tests, capture_output=True, text=True, env=env)
    if c1.returncode or c2.returncode:
        print(f"{mid}\t컴파일 실패(무효 돌연변이)\t{desc}"); continue
    p = subprocess.run(["timeout", "300", "java", "-Dstdout.encoding=UTF-8", "-cp", f"{work}/main:{work}/test",
                        "edu.btree.test.TestMain", f"mutant-{mid}", "20261024", "test-results/mutation"],
                       capture_output=True, text=True, env=env)
    m = re.search(r"총 PASS (\d+) / FAIL (\d+) / NOT RUN (\d+)", p.stdout)
    killed = [l.strip() for l in p.stdout.splitlines() if l.strip().startswith("[FAIL]") and len(l) - len(l.lstrip()) == 2]
    status = "KILLED" if (m and int(m.group(2)) > 0) else ("TIMEOUT" if p.returncode == 124 else "SURVIVED")
    summary = f"PASS {m.group(1)} / FAIL {m.group(2)} / NOT RUN {m.group(3)}" if m else f"exit {p.returncode}"
    print(f"{mid}\t{status}\t{desc}\t{summary}\t첫 실패: {(killed[0][:110] if killed else '-')}")
