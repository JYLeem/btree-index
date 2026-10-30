# Phase 1. 요구사항 분석

## 1. 활용 사례와 데이터 구조

| 구분 | 사례 A: 데이터베이스 인덱스 | 사례 B: 파일 시스템 인덱스 |
|---|---|---|
| 목적 | `orders` 테이블의 기본키(`order_id`) 유일 인덱스 | 볼륨 내 파일·디렉터리의 경로 인덱스 |
| 인덱스 키 | `Long order_id` (> 0) | `String` 전체 절대 경로 (예: `/home/leem/a.txt`) |
| 인덱스 값 | `RecordId(page, slot)` — 힙 파일 내 레코드 위치 | `Long fileId` — inode 테이블의 메타데이터 식별자 |
| 실제 데이터 | `HeapFile` 의 `OrderRecord` (정렬되지 않음) | `HashMap<fileId, FileMetadata>` (inode 테이블) |
| 범위 검색 용도 | `BETWEEN lo AND hi`, 전체 정렬 스캔 | 디렉터리 목록(접두사 구간), 이름 사전순 구간 |

공통 알고리즘은 `edu.btree.core.BTree<K, V>` 하나로 두고, 두 모듈은 이를 **조합(composition)** 해서 사용한다.

## 2. B-Tree 결정 사항

- 구현 대상: **고전적 B-Tree** (CLRS 정의). 내부 노드도 (키, 값)을 저장하고 리프 연결 리스트는 없다. B+Tree 가 아니다.
- 최소 차수 `2 ≤ t ≤ 65536` 을 생성자에서 설정한다. (상한은 반복 3에서 발견한 결함 D-002 로 추가: 2t 배열 크기 오버플로 방지) 노드당 키 수: 루트 0 ~ 2t−1 (내부 루트는 1 이상), 비루트 t−1 ~ 2t−1, 자식 수 = 키 수 + 1 (최대 2t).
- 기본값: DB 모듈 t = 3, FS 모듈 t = 3 (명령행 인자로 변경 가능).
- **중복 키 정책: 거부.** 이미 있는 키의 insert 는 `false` 를 반환하고 기존 값·구조를 바꾸지 않는다 (덮어쓰기 없음).
- **없는 키 delete: `null` 반환, 내용과 구조 모두 불변.** (이전 세션에서 발견한 결함 R-001 의 요구사항화)
- `null` 키·값은 허용하지 않는다 (`NullPointerException`).
- 범위 검색 경계: 하한/상한 각각 포함 여부 지정, `null` 은 무한대, `limit ≤ 0` 은 빈 결과, 하한 > 상한(또는 같고 한쪽이라도 배타)은 빈 결과.

### 충돌 조건 정리
- "파일 이름 또는 파일 ID" → 키는 **전체 경로**로 결정. 이름만 쓰면 다른 디렉터리의 같은 이름이 충돌하고, ID 만 쓰면 이름 검색·목록이 불가능하다. ID 검색은 inode 테이블(해시)로 별도 제공한다.
- "실제 파일 시스템 변경 금지" ↔ "파일 삭제" → 삭제는 인덱스·메타데이터에서만 수행한다.

## 3. 입출력 형식

### DB 명령 파일 (`db-commands.txt`, 한 줄에 명령 하나, `#` 주석·빈 줄 무시)
```
INSERT <order_id> <customer_id> <amount> <yyyy-MM-dd>
SELECT <order_id>
DELETE <order_id>
RANGE  <low> <high>        # 양 끝 포함
SCAN                       # 전체를 order_id 오름차순으로
```
### FS 명령 파일 (`fs-commands.txt`)
```
MKDIR  <path> <owner>
CREATE <path> <size> <owner>
STAT   <path>
DELETE <path>
LS     <dir>               # 직속 항목
FIND   <dir>               # 모든 하위 항목
RANGE  <from> <to>         # from ≤ path < to
```
경로에 공백은 지원하지 않는다(토큰 구분자).

### 출력 파일 (두 앱 공통 형식)
```
<줄번호>: <원래 명령> => <결과>
```
결과는 `OK ...`, `FAIL <사유코드> ...`, `ERROR <메시지>` 중 하나로 시작한다. 마지막에 `# summary` 줄로 건수를 적는다.

## 4. 예상 오류 상황과 경계 조건

| 분류 | 상황 | 기대 동작 |
|---|---|---|
| B-Tree | t < 2 또는 t > 65536 | `IllegalArgumentException` (앱은 오류 메시지 후 종료) |
| B-Tree | null 키/값 | `NullPointerException`, 트리 불변 |
| B-Tree | 중복 insert / 없는 키 delete·search | false / null, 구조 불변 |
| B-Tree | 빈 트리 연산, 키 1개, 정수 최소·최대값 키 | 정상 처리 |
| B-Tree | 루트 가득 참(2t−1) 후 삽입 / 루트 키 소진 | 루트 분할(높이+1) / 루트 축소(높이−1) |
| B-Tree | 최소 키(t−1) 노드에서 삭제 | 재분배 또는 병합 후 삭제 |
| DB | order_id ≤ 0, 빈 고객 ID, 음수 금액, 존재하지 않는 날짜(2026-02-30) | `ERROR` / `IllegalArgumentException` |
| DB | 하한 > 상한 | `IllegalArgumentException` (`ERROR`) |
| FS | 상대 경로, `//`, 끝 `/`, `.`/`..`, 255자 초과 이름 | `INVALID_PATH` |
| FS | 중복 경로 / 상위 없음 / 상위가 파일 | `ALREADY_EXISTS` / `PARENT_NOT_FOUND` / `NOT_A_DIRECTORY` |
| FS | 비어 있지 않은 디렉터리 삭제 / 루트 삭제 | `DIRECTORY_NOT_EMPTY` / `ROOT_PROTECTED` |
| 앱 | 명령 파일 없음·읽기 실패, 알 수 없는 명령, 인자 개수·형식 오류 | 오류 메시지 출력 후 해당 줄만 건너뜀 |

## 5. 테스트 항목과 합격 기준

모든 테스트는 **입력 / 예상 결과 / 실제 결과 / 통과 여부**를 기록한다. 예상 결과는 기준 모델(TreeMap/TreeSet), 브루트포스, 또는 손으로 계산한 값에서만 얻는다.

| 범주 | 합격 기준 |
|---|---|
| 1. Invariant checker | 정상 트리에서 위반 0건, 손상시킨 스냅샷 10종을 모두 검출 |
| 2. Differential | t ∈ {2,3,4,7,16} × 키 도메인 3종에서 모든 연산 반환값·키 집합이 TreeMap/TreeSet 과 일치 |
| 3. Stateful PBT | 무작위 시퀀스 600개에서 속성 P1~P5 성립. 실패 시 축소된 최소 반례 기록 |
| 4. Edge case | 손으로 계산한 정확한 트리 구조 문자열과 일치 |
| 5. Structural coverage | 분할(리프/내부)·병합(리프/내부)·재분배(좌/우 × 리프/내부)·루트 분할/축소·내부 키 삭제(2a/2b/2c) 이벤트가 모두 1회 이상 |
| 6. Adversarial | 각 시나리오에서 매 연산 후 불변식 유지 + 모델 일치 |
| DB / FS 기능 | 손으로 정한 기대 출력과 일치, 무작위 작업 후 인덱스와 저장소 개수 일치 |
| 회귀 | 발견된 모든 결함의 최소 재현 테스트 통과 |

## 6. 루프 종료 조건과 상한
- 종료: 위 테스트 전부 통과 + 컴파일 오류 없음.
- 상한: **반복 최대 5회**, 테스트 실행 1회당 내부 시간 상한 **300초**(초과 시 남은 스위트는 NOT RUN 으로 기록), 외부 실행 타임아웃 600초.
