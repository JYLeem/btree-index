# B-Tree 인덱스 (DB · 파일 시스템) + 테스트 루프

![test](https://github.com/JYLeem/btree-index/actions/workflows/test.yml/badge.svg)

고전 B-Tree(CLRS) 하나를 공통 모듈로 만들고, **주문 DB의 기본키 인덱스**와 **파일 시스템 경로 인덱스**에 적용한 고급알고리즘 과제입니다.
구현보다 "테스트로 결함을 찾고, 고치고, 다시 확인하는 반복"에 집중했습니다. 반복 기록은 [`docs/LOOP_LOG.md`](docs/LOOP_LOG.md)에 있습니다.

| 지표 | 결과 |
|---|---|
| 테스트 | 187개 통과 (시드 3개로 재확인), 외부 라이브러리 없음 |
| 테스트 종류 | 불변식 검사기, TreeMap 차분 테스트, 상태 기반 속성 테스트(자동 축소), 경계값, 구조 커버리지, 적대적 시퀀스, DB·FS 모듈, 앱 E2E |
| 변이 테스트 | 심은 결함 9종 모두 탐지 (최종 코드 기준) |
| 찾아서 고친 결함 | **D-001** 없는 키를 삭제해도 트리 구조가 바뀜 → 154개 연산을 5개로 축소한 재현 사례로 원인 확인 후 수정<br>**D-002** 최소 차수 상한이 없어 `new BTree(Integer.MAX_VALUE)`에서 정수 오버플로 → 범위 검사 추가 |

## CI
`.github/workflows/test.yml`: push · PR마다 시드 3개(20261024, 777, 424242)로 전체 테스트, 수동 실행 시 변이 테스트

---

## 상세

고전적 B-Tree(CLRS, 최소 차수 t) 하나를 공통 모듈로 두고, 두 활용 모듈이 조합해서 사용한다. B+Tree 가 아니다.

```
src/main/java/edu/btree/
  core/  BTree, Entry, StructureEvent, NodeSnapshot, TreeSnapshot   ← 공통 알고리즘
  db/    RecordId, OrderRecord, HeapFile, PrimaryKeyIndex, OrderTable, DbCommandProcessor, DbIndexApp
  fs/    FileType, FsStatus, FileMetadata, PathRules, FileSystemIndex, FsCommandProcessor, FsIndexApp
src/test/java/edu/btree/test/   TestMain + 10개 스위트, InvariantChecker, ModelHarness, TestRecorder
samples/   명령 파일과 손으로 작성한 기대 출력
docs/      REQUIREMENTS.md (Phase 1), LOOP_LOG.md (반복·결함 기록)
test-results/  반복별 결과(입력/예상/실제/통과 표), mutation 결과
```

## 요구 환경
JDK 8 이상 (JDK 21 로 실행 검증, `--release 8` 컴파일 확인). 외부 라이브러리 없음.

## 빌드와 전체 테스트
```bash
./build-and-test.sh            # → test-results/run.md, 실패 시 종료 코드 1
./build-and-test.sh mylabel 777  # 라벨·시드 지정
```
수동으로 (Windows 는 `:` 대신 `;`):
```bash
mkdir -p build/main build/test
javac -encoding UTF-8 -d build/main $(find src/main -name '*.java')
javac -encoding UTF-8 -cp build/main -d build/test $(find src/test -name '*.java')
java -cp build/main:build/test edu.btree.test.TestMain run 20261024 test-results
```
테스트는 **프로젝트 루트에서** 실행해야 한다(E2E 테스트가 `samples/` 를 읽음).

## 프로그램 실행
```bash
java -cp build/main edu.btree.db.DbIndexApp samples/db-commands.txt db-output.txt 3
java -cp build/main edu.btree.fs.FsIndexApp samples/fs-commands.txt fs-output.txt 3
```
인자: `[명령파일] [출력파일] [최소차수 2~65536]`. 명령 형식은 `docs/REQUIREMENTS.md` 3절.

## IntelliJ
`src/main/java` 를 Sources Root, `src/test/java` 를 Test Sources Root 로 지정하고 `TestMain`, `DbIndexApp`, `FsIndexApp` 의 main 을 실행한다. Working directory 는 프로젝트 루트로 둔다. (IntelliJ 에서의 실행은 검증하지 않았다.)

## Mutation testing (테스트 유효성 검증, 선택)
```bash
python3 mutation-test.py        # BTree 에 결함 9종을 하나씩 심고 테스트가 잡아내는지 확인 (수 분 소요)
```
