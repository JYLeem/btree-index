# Loop Engineering 기록

상한: 반복 최대 5회 / 테스트 실행 1회 내부 상한 300초 / 외부 타임아웃 600초. 모든 수치는 실제 실행 결과이며 원본은 `test-results/` 에 있다.

| 반복 | 변경 | 결과 (실제 실행) | 결과 파일 |
|---|---|---|---|
| 1 | 초기 구현 + 테스트 9개 스위트 | PASS 147 / FAIL 0 / NOT RUN 0, 50.4초, 컴파일 경고 3건(테스트 코드 varargs) | `iteration-1.md` |
| 2 | 앱 E2E 스위트(샘플 파일 + 손으로 쓴 기대 출력) 추가 | PASS 184 / FAIL 0 / NOT RUN 0, 49.5초, 경고 2건 | `iteration-2.md` |
| 2′ | 테스트 유효성 검증: BTree 에 결함 9종 주입(mutation) | 9/9 KILLED | `mutation/` |
| 3 | 결함 D-002 재현 테스트 추가, R-001 기대 조건 강화, 경고 제거 | **PASS 184 / FAIL 3** / NOT RUN 0, 46.7초, 경고 0 | `iteration-3.md` |
| 4 | D-002 수정 (BTree 상한 검사 + 앱 인자 검사) | PASS 187 / FAIL 0 / NOT RUN 0, 47.4초 | `iteration-4.md` |
| 5 | 최종 확인: 다른 시드 3개(1, 777, 424242) | 각각 PASS 187 / FAIL 0 / NOT RUN 0 | `iteration-5-seed*.md` |

## 결함 목록

### D-001 (R-001) — 없는 키 delete 가 구조를 바꿈 *(이전 세션에서 발견·수정, 이번 루프에서는 회귀 테스트로만 관리)*
- **증상**: `delete(없는 키)` 가 `null` 을 반환하지만 트리 구조(노드 배치)가 바뀜. 내용과 불변식은 정상.
- **원인**: CLRS 하향식 삭제는 내려가는 경로에서 자식이 t-1 개면 미리 재분배·병합한다(경우 3). 키가 끝내 없어도 이 사전 조정은 이미 수행된 뒤다.
- **수정**: 공개 `delete()` 에서 `contains()` 로 존재를 먼저 확인. 내부 재귀가 리프에서 키를 못 찾으면 `IllegalStateException`(도달 불가 경로).
- **최소 재현**: t=2, insert 0,3,2,1 → delete 4. 원래 코드(M9)에서는 `((0 1) 2 (3))` → `((0) 1 (2 3))` 로 **재분배**가 일어남.
- **반복 3의 테스트 보강**: R-001 의 기대 조건 "병합 0회" 는 실제 원인(재분배)을 보지 못하는 약한 조건이었으므로 "구조 이벤트 전체 0회" 로 강화했다(기준을 더 엄격하게 바꾼 것이며, 실제 결과에 맞춰 완화한 것이 아님). 구조 문자열 비교는 원래부터 포함되어 있었다.

### D-002 (R-002) — 최소 차수 상한 미검사로 정수 오버플로 *(반복 3에서 발견, 반복 4에서 수정)*
- **발견 경위**: 반복 1·2 가 전부 통과해 코드 리뷰로 입력 검증 경계를 점검. 의심 지점을 먼저 실패 테스트로 작성해 실제 실행으로 확인했다.
- **보존한 실패 입력/결과** (`iteration-3.md`)
  - `new BTree(Integer.MAX_VALUE)` → 예상 `IllegalArgumentException`, 실제 **`NegativeArraySizeException`**
  - `new BTree(65537)` → 예상 `IllegalArgumentException`, 실제 예외 없음(상한 없음)
  - `DbIndexApp samples/db-commands.txt out 2000000000` → 예상 오류 메시지, 실제 **`NegativeArraySizeException` 으로 비정상 종료**
- **원인 (코드 위치)**: `BTree(int)` 생성자가 `minDegree < 2` 만 검사하고, `Node` 생성자에서 `new Object[2 * t - 1]` 계산 시 `2 * Integer.MAX_VALUE - 1 = -3` 으로 오버플로. 앱(`DbIndexApp`/`FsIndexApp`)은 `NumberFormatException` 만 처리해 런타임 예외가 그대로 전파됨.
- **수정 (최소 변경)**
  - `BTree`: `MAX_MIN_DEGREE = 65536` 상수 추가, 생성자 검사를 `2 ≤ t ≤ MAX_MIN_DEGREE` 로 변경.
  - `DbIndexApp`, `FsIndexApp`: 같은 범위를 검사하고 메시지를 "2 이상 65536 이하" 로 변경.
  - `docs/REQUIREMENTS.md`: t 범위 요구사항 갱신.
- **회귀**: R-002a, R-002b, E2E-ERR3 추가. 반복 4 에서 전부 통과.

## 회귀(이전 PASS → 이후 비PASS) 추적

`iteration-N.md` 의 ID별 상태를 비교한 결과.

| 구간 | 이전 PASS 중 이후 PASS 아님 | 설명 |
|---|---|---|
| 1 → 2 | 0건 | 새 테스트 37건 |
| 2 → 3 | **1건: E2E-ERR2** | 코드 회귀가 아니라 **요구사항 변경**: D-002 로 t 상한을 요구사항에 추가하면서 오류 메시지 명세를 "2 이상 65536 이하" 로 바꿨고, 기대값을 **수정 전에** 새 명세로 갱신했다. 반복 3 에서는 구현이 아직 옛 메시지라 실패했고, 반복 4 의 수정으로 통과. |
| 3 → 4 | 0건 | — |

## 테스트 유효성 검증 (mutation testing, 반복 2 코드 기준)
`mutation-test.py` 가 `BTree.java` 에 결함을 하나씩 심고 전체 테스트를 실행한다. 9개 모두 1개 이상의 테스트가 실패(KILLED). 상세는 `test-results/mutation/summary.tsv`. 반복 4 의 수정은 돌연변이를 심은 위치와 무관한 생성자 검사뿐이므로 결과에 영향이 없다고 판단하지만, **최종 코드로 mutation 을 다시 실행하지는 않았다.**

## 추가: 최종 코드 기준 mutation 재실행
반복 4 이후 코드로 `mutation-test.py` 를 다시 실행해 9/9 KILLED 를 확인했다 (`test-results/mutation/`).
