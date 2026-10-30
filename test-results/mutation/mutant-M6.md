# 테스트 결과: mutant-M6

- 실행 라벨: `mutant-M6`, seed: `20261024`, 총 소요: 8.93초
- 결과: **PASS 107 / FAIL 8 / NOT RUN 0** (총 115건)
- Java: 21.0.12.1

## 스위트별 소요 시간
- 1. Invariant checker test: 0.05초
- 2. Differential test: 0.01초
- 3. Stateful property-based test: 0.01초
- 4. Edge case test: 0.06초
- 5. Structural coverage test: 0.00초
- 6. Adversarial sequence test: 8.50초
- 7. DB 인덱스 모듈 기능 테스트: 0.09초
- 8. FS 인덱스 모듈 기능 테스트: 0.11초
- 9. 회귀 테스트: 0.01초
- 10. 애플리케이션 E2E 테스트: 0.05초

## 실패 / 미실행 목록
- [FAIL] 1. Invariant checker test / CRASH 스위트 실행 중 예외 없이 완료 — 실제: java.lang.ArrayIndexOutOfBoundsException: arraycopy: last destination index 4 out of bounds for object array[3]
- [FAIL] 2. Differential test / CRASH 스위트 실행 중 예외 없이 완료 — 실제: java.lang.ArrayIndexOutOfBoundsException: arraycopy: last destination index 4 out of bounds for object array[3]
- [FAIL] 3. Stateful property-based test / CRASH 스위트 실행 중 예외 없이 완료 — 실제: java.lang.ArrayIndexOutOfBoundsException: arraycopy: last destination index 4 out of bounds for object array[3]
- [FAIL] 4. Edge case test / CRASH 스위트 실행 중 예외 없이 완료 — 실제: java.lang.ArrayIndexOutOfBoundsException: arraycopy: last destination index 6 out of bounds for object array[5]
- [FAIL] 5. Structural coverage test / CRASH 스위트 실행 중 예외 없이 완료 — 실제: java.lang.ArrayIndexOutOfBoundsException: arraycopy: last destination index 4 out of bounds for object array[3]
- [FAIL] 6. Adversarial sequence test / CRASH 스위트 실행 중 예외 없이 완료 — 실제: java.lang.ArrayIndexOutOfBoundsException: arraycopy: last destination index 4 out of bounds for object array[3]
- [FAIL] 7. DB 인덱스 모듈 기능 테스트 / CRASH 스위트 실행 중 예외 없이 완료 — 실제: java.lang.ArrayIndexOutOfBoundsException: arraycopy: last destination index 6 out of bounds for object array[5]
- [FAIL] 8. FS 인덱스 모듈 기능 테스트 / CRASH 스위트 실행 중 예외 없이 완료 — 실제: java.lang.ArrayIndexOutOfBoundsException: arraycopy: last destination index 4 out of bounds for object array[3]

| 스위트 | ID | 테스트 | 입력 | 예상 결과 | 실제 결과 | 결과 |
|---|---|---|---|---|---|---|
| 1. Invariant checker test | CRASH | 스위트 실행 중 예외 없이 완료 | - | 예외 없음 | java.lang.ArrayIndexOutOfBoundsException: arraycopy: last destination index 4 out of bounds for object array[3] | **FAIL** |
| 2. Differential test | CRASH | 스위트 실행 중 예외 없이 완료 | - | 예외 없음 | java.lang.ArrayIndexOutOfBoundsException: arraycopy: last destination index 4 out of bounds for object array[3] | **FAIL** |
| 3. Stateful property-based test | CRASH | 스위트 실행 중 예외 없이 완료 | - | 예외 없음 | java.lang.ArrayIndexOutOfBoundsException: arraycopy: last destination index 4 out of bounds for object array[3] | **FAIL** |
| 4. Edge case test | EDGE-01 | 빈 트리 검색/삭제/범위/순회 | t=2, 키 없음; search(1), delete(1), rangeSearch(null,null), inOrder | null null [] [] 높이1 () 위반 0건 | null null [] [] 높이1 () 위반 0건 | **PASS** |
| 4. Edge case test | EDGE-02 | 단일 키 삽입·검색·삭제 | t=2, insert 42 → search 42 → delete 42 | (42) 1309 → 1309 () size0 위반 0건 | (42) 1309 → 1309 () size0 위반 0건 | **PASS** |
| 4. Edge case test | EDGE-03 | 존재하지 않는 키의 검색·삭제는 null, 구조 불변 | t=2, 키 1~20; search(99), delete(99), delete(0) | null null null 구조불변 size20 | null null null 구조불변 size20 | **PASS** |
| 4. Edge case test | EDGE-04 | 루트 분할: 가득 찬 루트(2t-1)에 삽입 | t=3, insert 1..5 → insert 6 | (1 2 3 4 5) 높이1 → ((1 2) 3 (4 5 6)) 높이2 ROOT_SPLIT=1 | (1 2 3 4 5) 높이1 → ((1 2) 3 (4 5 6)) 높이2 ROOT_SPLIT=1 | **PASS** |
| 4. Edge case test | EDGE-05 | 루트 축소: 병합으로 루트가 비면 높이 감소 | t=3, insert 1..6, delete 6 → delete 1 | ((1 2) 3 (4 5)) → (2 3 4 5) 높이1 ROOT_SHRINK=1 위반 0건 | ((1 2) 3 (4 5)) → (2 3 4 5) 높이1 ROOT_SHRINK=1 위반 0건 | **PASS** |
| 4. Edge case test | EDGE-06 | 최소 키 노드 삭제 → 오른쪽 형제 재분배 | t=3, ((1 2) 3 (4 5 6)) 에서 delete 1 | ((2 3) 4 (5 6)) BORROW_RIGHT_LEAF=1 | ((2 3) 4 (5 6)) BORROW_RIGHT_LEAF=1 | **PASS** |
| 4. Edge case test | EDGE-07 | 최소 키 노드 삭제 → 왼쪽 형제 재분배 | t=3, insert 1..6, delete 6, insert 0 → delete 4 | ((0 1 2) 3 (4 5)) → ((0 1) 2 (3 5)) BORROW_LEFT_LEAF=1 | ((0 1 2) 3 (4 5)) → ((0 1) 2 (3 5)) BORROW_LEFT_LEAF=1 | **PASS** |
| 4. Edge case test | EDGE-08 | 최소 키 노드 삭제 → 형제와 병합(+루트 축소) | t=3, ((1 2) 3 (4 5)) 에서 delete 4 | (1 2 3 5) LEAF_MERGE=1 | (1 2 3 5) LEAF_MERGE=1 | **PASS** |
| 4. Edge case test | CRASH | 스위트 실행 중 예외 없이 완료 | - | 예외 없음 | java.lang.ArrayIndexOutOfBoundsException: arraycopy: last destination index 6 out of bounds for object array[5] | **FAIL** |
| 5. Structural coverage test | CRASH | 스위트 실행 중 예외 없이 완료 | - | 예외 없음 | java.lang.ArrayIndexOutOfBoundsException: arraycopy: last destination index 4 out of bounds for object array[3] | **FAIL** |
| 6. Adversarial sequence test | ADV-01 | 오름차순 삽입 | t=2,3,5, n=2000, 매 연산 후 전체 검사, seed=20261024 | 매 연산 후 불변식 유지 + 모델 일치 | 매 연산 후 불변식 유지 + 모델 일치 | **PASS** |
| 6. Adversarial sequence test | ADV-02 | 내림차순 삽입 | t=2,3,5, n=2000, 매 연산 후 전체 검사, seed=20261024 | 매 연산 후 불변식 유지 + 모델 일치 | 매 연산 후 불변식 유지 + 모델 일치 | **PASS** |
| 6. Adversarial sequence test | ADV-03 | 오름차순 삽입 후 역순(내림차순) 삭제 | t=2,3,5, n=2000, 매 연산 후 전체 검사, seed=20261024 | 매 연산 후 불변식 유지 + 모델 일치 | 매 연산 후 불변식 유지 + 모델 일치 | **PASS** |
| 6. Adversarial sequence test | ADV-04 | 내림차순 삽입 후 역순(오름차순) 삭제 | t=2,3,5, n=2000, 매 연산 후 전체 검사, seed=20261024 | 매 연산 후 불변식 유지 + 모델 일치 | 매 연산 후 불변식 유지 + 모델 일치 | **PASS** |
| 6. Adversarial sequence test | CRASH | 스위트 실행 중 예외 없이 완료 | - | 예외 없음 | java.lang.ArrayIndexOutOfBoundsException: arraycopy: last destination index 4 out of bounds for object array[3] | **FAIL** |
| 7. DB 인덱스 모듈 기능 테스트 | DB-C01 | 명령 처리기 출력 | INSERT 1001 C-ALICE 15000 2026-10-01 | OK INSERTED 1001 -> RID(0,0) | OK INSERTED 1001 -> RID(0,0) | **PASS** |
| 7. DB 인덱스 모듈 기능 테스트 | DB-C02 | 명령 처리기 출력 | INSERT 1002 C-BOB 22000 2026-10-02 | OK INSERTED 1002 -> RID(0,1) | OK INSERTED 1002 -> RID(0,1) | **PASS** |
| 7. DB 인덱스 모듈 기능 테스트 | DB-C03 | 명령 처리기 출력 | SELECT 1001 | OK 1001 C-ALICE 15000 2026-10-01 @RID(0,0) | OK 1001 C-ALICE 15000 2026-10-01 @RID(0,0) | **PASS** |
| 7. DB 인덱스 모듈 기능 테스트 | DB-C04 | 명령 처리기 출력 | INSERT 1001 C-EVE 1 2026-10-03 | FAIL DUPLICATE_KEY 1001 | FAIL DUPLICATE_KEY 1001 | **PASS** |
| 7. DB 인덱스 모듈 기능 테스트 | DB-C05 | 명령 처리기 출력 | SELECT 1001 | OK 1001 C-ALICE 15000 2026-10-01 @RID(0,0) | OK 1001 C-ALICE 15000 2026-10-01 @RID(0,0) | **PASS** |
| 7. DB 인덱스 모듈 기능 테스트 | DB-C06 | 명령 처리기 출력 | SELECT 9999 | FAIL NOT_FOUND 9999 | FAIL NOT_FOUND 9999 | **PASS** |
| 7. DB 인덱스 모듈 기능 테스트 | DB-C07 | 명령 처리기 출력 | DELETE 1001 | OK DELETED 1001 | OK DELETED 1001 | **PASS** |
| 7. DB 인덱스 모듈 기능 테스트 | DB-C08 | 명령 처리기 출력 | DELETE 1001 | FAIL NOT_FOUND 1001 | FAIL NOT_FOUND 1001 | **PASS** |
| 7. DB 인덱스 모듈 기능 테스트 | DB-C09 | 명령 처리기 출력 | INSERT 1003 C-CAROL 5000 2026-10-03 | OK INSERTED 1003 -> RID(0,0) | OK INSERTED 1003 -> RID(0,0) | **PASS** |
| 7. DB 인덱스 모듈 기능 테스트 | DB-C10 | 명령 처리기 출력 | RANGE 1000 1002 | OK 1 [1002] | OK 1 [1002] | **PASS** |
| 7. DB 인덱스 모듈 기능 테스트 | DB-C11 | 명령 처리기 출력 | SCAN | OK 2 [1002, 1003] | OK 2 [1002, 1003] | **PASS** |
| 7. DB 인덱스 모듈 기능 테스트 | DB-C12 | 명령 처리기 출력 | RANGE 5 1 | ERROR 범위 하한이 상한보다 큽니다: 5 > 1 | ERROR 범위 하한이 상한보다 큽니다: 5 > 1 | **PASS** |
| 7. DB 인덱스 모듈 기능 테스트 | DB-C13 | 명령 처리기 출력 | INSERT -1 C 1 2026-10-01 | ERROR order_id 는 양수여야 합니다: -1 | ERROR order_id 는 양수여야 합니다: -1 | **PASS** |
| 7. DB 인덱스 모듈 기능 테스트 | DB-C14 | 명령 처리기 출력 | INSERT 1 C 1 2026-02-30 | ERROR order_date 가 올바른 yyyy-MM-dd 날짜가 아닙니다: 2026-02-30 | ERROR order_date 가 올바른 yyyy-MM-dd 날짜가 아닙니다: 2026-02-30 | **PASS** |
| 7. DB 인덱스 모듈 기능 테스트 | DB-C15 | 명령 처리기 출력 | INSERT 1 C -5 2026-01-01 | ERROR amount 는 0 이상이어야 합니다: -5 | ERROR amount 는 0 이상이어야 합니다: -5 | **PASS** |
| 7. DB 인덱스 모듈 기능 테스트 | DB-C16 | 명령 처리기 출력 | SELECT abc | ERROR order_id 는 정수여야 합니다: abc | ERROR order_id 는 정수여야 합니다: abc | **PASS** |
| 7. DB 인덱스 모듈 기능 테스트 | DB-C17 | 명령 처리기 출력 | SELECT | ERROR 인자 개수 오류, 사용법: SELECT <order_id> | ERROR 인자 개수 오류, 사용법: SELECT <order_id> | **PASS** |
| 7. DB 인덱스 모듈 기능 테스트 | DB-C18 | 명령 처리기 출력 | UPDATE 1 | ERROR 알 수 없는 명령: UPDATE | ERROR 알 수 없는 명령: UPDATE | **PASS** |
| 7. DB 인덱스 모듈 기능 테스트 | DB-C19 | 명령 처리기 출력 |    # 주석 | null | null | **PASS** |
| 7. DB 인덱스 모듈 기능 테스트 | DB-C20 | 명령 처리기 출력 |  | null | null | **PASS** |
| 7. DB 인덱스 모듈 기능 테스트 | DB-R01 | BETWEEN 10 AND 30 결과가 order_id 오름차순 | id 1,4,…,49 (17개)를 섞어서 삽입, t=2 | [10, 13, 16, 19, 22, 25, 28] | [10, 13, 16, 19, 22, 25, 28] | **PASS** |
| 7. DB 인덱스 모듈 기능 테스트 | DB-R02 | SCAN 이 전체를 오름차순으로 반환 | 위와 같은 테이블 | [1, 4, 7, 10, 13, 16, 19, 22, 25, 28, 31, 34, 37, 40, 43, 46, 49] | [1, 4, 7, 10, 13, 16, 19, 22, 25, 28, 31, 34, 37, 40, 43, 46, 49] | **PASS** |
| 7. DB 인덱스 모듈 기능 테스트 | DB-R03 | 삭제된 힙 슬롯 재사용 | id 1..10 삽입, delete 5, insert 100 | RID(1,0) | RID(1,0) | **PASS** |
| 7. DB 인덱스 모듈 기능 테스트 | CRASH | 스위트 실행 중 예외 없이 완료 | - | 예외 없음 | java.lang.ArrayIndexOutOfBoundsException: arraycopy: last destination index 6 out of bounds for object array[5] | **FAIL** |
| 8. FS 인덱스 모듈 기능 테스트 | FS-C01 | 명령 처리기 출력 | MKDIR /home leem | OK /home | OK /home | **PASS** |
| 8. FS 인덱스 모듈 기능 테스트 | FS-C02 | 명령 처리기 출력 | MKDIR /home/leem leem | OK /home/leem | OK /home/leem | **PASS** |
| 8. FS 인덱스 모듈 기능 테스트 | FS-C03 | 명령 처리기 출력 | CREATE /home/leem/b.txt 10 leem | OK /home/leem/b.txt | OK /home/leem/b.txt | **PASS** |
| 8. FS 인덱스 모듈 기능 테스트 | FS-C04 | 명령 처리기 출력 | CREATE /home/leem/b-x 5 leem | OK /home/leem/b-x | OK /home/leem/b-x | **PASS** |
| 8. FS 인덱스 모듈 기능 테스트 | FS-C05 | 명령 처리기 출력 | MKDIR /home/leem/b leem | OK /home/leem/b | OK /home/leem/b | **PASS** |
| 8. FS 인덱스 모듈 기능 테스트 | FS-C06 | 명령 처리기 출력 | CREATE /home/leem/b/c 1 leem | OK /home/leem/b/c | OK /home/leem/b/c | **PASS** |
| 8. FS 인덱스 모듈 기능 테스트 | FS-C07 | 명령 처리기 출력 | CREATE /home/leem/b0 1 leem | OK /home/leem/b0 | OK /home/leem/b0 | **PASS** |
| 8. FS 인덱스 모듈 기능 테스트 | FS-C08 | 명령 처리기 출력 | LS /home/leem | OK 4 [/home/leem/b, /home/leem/b-x, /home/leem/b.txt, /home/leem/b0] | OK 4 [/home/leem/b, /home/leem/b-x, /home/leem/b.txt, /home/leem/b0] | **PASS** |
| 8. FS 인덱스 모듈 기능 테스트 | FS-C09 | 명령 처리기 출력 | FIND /home | OK 6 [/home/leem, /home/leem/b, /home/leem/b-x, /home/leem/b.txt, /home/leem/b/c, /home/leem/b0] | OK 6 [/home/leem, /home/leem/b, /home/leem/b-x, /home/leem/b.txt, /home/leem/b/c, /home/leem/b0] | **PASS** |
| 8. FS 인덱스 모듈 기능 테스트 | FS-C10 | 명령 처리기 출력 | LS / | OK 1 [/home] | OK 1 [/home] | **PASS** |
| 8. FS 인덱스 모듈 기능 테스트 | FS-C11 | 명령 처리기 출력 | STAT /home/leem/b.txt | OK /home/leem/b.txt FILE id=4 size=10 owner=leem | OK /home/leem/b.txt FILE id=4 size=10 owner=leem | **PASS** |
| 8. FS 인덱스 모듈 기능 테스트 | FS-C12 | 명령 처리기 출력 | STAT /home/leem/zzz | FAIL NOT_FOUND /home/leem/zzz | FAIL NOT_FOUND /home/leem/zzz | **PASS** |
| 8. FS 인덱스 모듈 기능 테스트 | FS-C13 | 명령 처리기 출력 | CREATE /home/leem/b.txt 1 leem | FAIL ALREADY_EXISTS /home/leem/b.txt | FAIL ALREADY_EXISTS /home/leem/b.txt | **PASS** |
| 8. FS 인덱스 모듈 기능 테스트 | FS-C14 | 명령 처리기 출력 | CREATE /tmp/x 1 leem | FAIL PARENT_NOT_FOUND /tmp/x | FAIL PARENT_NOT_FOUND /tmp/x | **PASS** |
| 8. FS 인덱스 모듈 기능 테스트 | FS-C15 | 명령 처리기 출력 | CREATE /home/leem/b.txt/y 1 leem | FAIL NOT_A_DIRECTORY /home/leem/b.txt/y | FAIL NOT_A_DIRECTORY /home/leem/b.txt/y | **PASS** |
| 8. FS 인덱스 모듈 기능 테스트 | FS-C16 | 명령 처리기 출력 | CREATE home/x 1 leem | FAIL INVALID_PATH home/x | FAIL INVALID_PATH home/x | **PASS** |
| 8. FS 인덱스 모듈 기능 테스트 | FS-C17 | 명령 처리기 출력 | CREATE /home//x 1 leem | FAIL INVALID_PATH /home//x | FAIL INVALID_PATH /home//x | **PASS** |
| 8. FS 인덱스 모듈 기능 테스트 | FS-C18 | 명령 처리기 출력 | CREATE /home/../x 1 leem | FAIL INVALID_PATH /home/../x | FAIL INVALID_PATH /home/../x | **PASS** |
| 8. FS 인덱스 모듈 기능 테스트 | FS-C19 | 명령 처리기 출력 | CREATE /home/x -3 leem | FAIL INVALID_ARGUMENT /home/x | FAIL INVALID_ARGUMENT /home/x | **PASS** |
| 8. FS 인덱스 모듈 기능 테스트 | FS-C20 | 명령 처리기 출력 | DELETE /home/leem/b | FAIL DIRECTORY_NOT_EMPTY /home/leem/b | FAIL DIRECTORY_NOT_EMPTY /home/leem/b | **PASS** |
| 8. FS 인덱스 모듈 기능 테스트 | FS-C21 | 명령 처리기 출력 | DELETE /home/leem/b/c | OK /home/leem/b/c | OK /home/leem/b/c | **PASS** |
| 8. FS 인덱스 모듈 기능 테스트 | FS-C22 | 명령 처리기 출력 | DELETE /home/leem/b | OK /home/leem/b | OK /home/leem/b | **PASS** |
| 8. FS 인덱스 모듈 기능 테스트 | FS-C23 | 명령 처리기 출력 | DELETE /home/leem/b | FAIL NOT_FOUND /home/leem/b | FAIL NOT_FOUND /home/leem/b | **PASS** |
| 8. FS 인덱스 모듈 기능 테스트 | FS-C24 | 명령 처리기 출력 | DELETE / | FAIL ROOT_PROTECTED / | FAIL ROOT_PROTECTED / | **PASS** |
| 8. FS 인덱스 모듈 기능 테스트 | FS-C25 | 명령 처리기 출력 | RANGE /home/leem/b /home/leem/b0 | OK 2 [/home/leem/b-x, /home/leem/b.txt] | OK 2 [/home/leem/b-x, /home/leem/b.txt] | **PASS** |
| 8. FS 인덱스 모듈 기능 테스트 | FS-C26 | 명령 처리기 출력 | LS /home/leem/b.txt | ERROR 디렉터리가 아닙니다: /home/leem/b.txt | ERROR 디렉터리가 아닙니다: /home/leem/b.txt | **PASS** |
| 8. FS 인덱스 모듈 기능 테스트 | FS-C27 | 명령 처리기 출력 | LS /nope | ERROR 디렉터리가 없습니다: /nope | ERROR 디렉터리가 없습니다: /nope | **PASS** |
| 8. FS 인덱스 모듈 기능 테스트 | FS-C28 | 명령 처리기 출력 | CREATE /a 1 | ERROR 인자 개수 오류, 사용법: CREATE <path> <size> <owner> | ERROR 인자 개수 오류, 사용법: CREATE <path> <size> <owner> | **PASS** |
| 8. FS 인덱스 모듈 기능 테스트 | FS-C29 | 명령 처리기 출력 | CREATE /a x leem | ERROR size 는 정수여야 합니다: x | ERROR size 는 정수여야 합니다: x | **PASS** |
| 8. FS 인덱스 모듈 기능 테스트 | FS-C30 | 명령 처리기 출력 | MOVE /a /b | ERROR 알 수 없는 명령: MOVE | ERROR 알 수 없는 명령: MOVE | **PASS** |
| 8. FS 인덱스 모듈 기능 테스트 | FS-P01 | 경로 규칙 유틸리티 | prefixUpperBound("/a/"), prefixUpperBound("\uffff"), parent("/a"), parent("/a/b"), 이름 255자/256자 | /a0 null / /a true false | /a0 null / /a true false | **PASS** |
| 8. FS 인덱스 모듈 기능 테스트 | CRASH | 스위트 실행 중 예외 없이 완료 | - | 예외 없음 | java.lang.ArrayIndexOutOfBoundsException: arraycopy: last destination index 4 out of bounds for object array[3] | **FAIL** |
| 9. 회귀 테스트 | R-001 | 없는 키 delete 가 구조를 바꾸지 않음 (최소 반례) | t=2, insert 0,3,2,1 → delete 4 | null ((0 1) 2 (3)) 구조 이벤트 0회 | null ((0 1) 2 (3)) 구조 이벤트 0회 | **PASS** |
| 9. 회귀 테스트 | R-002a | 매우 큰 최소 차수는 IllegalArgumentException | new BTree(Integer.MAX_VALUE), new BTree(65537) | IllegalArgumentException IllegalArgumentException | IllegalArgumentException IllegalArgumentException | **PASS** |
| 9. 회귀 테스트 | R-002b | 허용 상한(65536)의 최소 차수는 생성 가능 | new BTree(65536) | 생성됨 | 생성됨 | **PASS** |
| 10. 애플리케이션 E2E 테스트 | E2E-DB-N | 출력 줄 수 | samples/db-commands.txt → build/e2e/db-output.txt | 17 | 17 | **PASS** |
| 10. 애플리케이션 E2E 테스트 | E2E-DB-L01 | 출력 줄 일치 | samples/db-commands.txt 출력 1번째 줄 | 2: INSERT 1005 C-ALICE 15000 2026-10-01 => OK INSERTED 1005 -> RID(0,0) | 2: INSERT 1005 C-ALICE 15000 2026-10-01 => OK INSERTED 1005 -> RID(0,0) | **PASS** |
| 10. 애플리케이션 E2E 테스트 | E2E-DB-L02 | 출력 줄 일치 | samples/db-commands.txt 출력 2번째 줄 | 3: INSERT 1001 C-BOB 22000 2026-10-02 => OK INSERTED 1001 -> RID(0,1) | 3: INSERT 1001 C-BOB 22000 2026-10-02 => OK INSERTED 1001 -> RID(0,1) | **PASS** |
| 10. 애플리케이션 E2E 테스트 | E2E-DB-L03 | 출력 줄 일치 | samples/db-commands.txt 출력 3번째 줄 | 4: INSERT 1003 C-CAROL 5000 2026-10-03 => OK INSERTED 1003 -> RID(0,2) | 4: INSERT 1003 C-CAROL 5000 2026-10-03 => OK INSERTED 1003 -> RID(0,2) | **PASS** |
| 10. 애플리케이션 E2E 테스트 | E2E-DB-L04 | 출력 줄 일치 | samples/db-commands.txt 출력 4번째 줄 | 5: INSERT 1002 C-DAVE 8000 2026-10-04 => OK INSERTED 1002 -> RID(0,3) | 5: INSERT 1002 C-DAVE 8000 2026-10-04 => OK INSERTED 1002 -> RID(0,3) | **PASS** |
| 10. 애플리케이션 E2E 테스트 | E2E-DB-L05 | 출력 줄 일치 | samples/db-commands.txt 출력 5번째 줄 | 6: INSERT 1004 C-EVE 12000 2026-10-05 => OK INSERTED 1004 -> RID(1,0) | 6: INSERT 1004 C-EVE 12000 2026-10-05 => OK INSERTED 1004 -> RID(1,0) | **PASS** |
| 10. 애플리케이션 E2E 테스트 | E2E-DB-L06 | 출력 줄 일치 | samples/db-commands.txt 출력 6번째 줄 | 7: INSERT 1003 C-FRANK 1 2026-10-06 => FAIL DUPLICATE_KEY 1003 | 7: INSERT 1003 C-FRANK 1 2026-10-06 => FAIL DUPLICATE_KEY 1003 | **PASS** |
| 10. 애플리케이션 E2E 테스트 | E2E-DB-L07 | 출력 줄 일치 | samples/db-commands.txt 출력 7번째 줄 | 8: SELECT 1003 => OK 1003 C-CAROL 5000 2026-10-03 @RID(0,2) | 8: SELECT 1003 => OK 1003 C-CAROL 5000 2026-10-03 @RID(0,2) | **PASS** |
| 10. 애플리케이션 E2E 테스트 | E2E-DB-L08 | 출력 줄 일치 | samples/db-commands.txt 출력 8번째 줄 | 9: SELECT 2000 => FAIL NOT_FOUND 2000 | 9: SELECT 2000 => FAIL NOT_FOUND 2000 | **PASS** |
| 10. 애플리케이션 E2E 테스트 | E2E-DB-L09 | 출력 줄 일치 | samples/db-commands.txt 출력 9번째 줄 | 10: RANGE 1002 1004 => OK 3 [1002, 1003, 1004] | 10: RANGE 1002 1004 => OK 3 [1002, 1003, 1004] | **PASS** |
| 10. 애플리케이션 E2E 테스트 | E2E-DB-L10 | 출력 줄 일치 | samples/db-commands.txt 출력 10번째 줄 | 11: DELETE 1002 => OK DELETED 1002 | 11: DELETE 1002 => OK DELETED 1002 | **PASS** |
| 10. 애플리케이션 E2E 테스트 | E2E-DB-L11 | 출력 줄 일치 | samples/db-commands.txt 출력 11번째 줄 | 12: DELETE 1002 => FAIL NOT_FOUND 1002 | 12: DELETE 1002 => FAIL NOT_FOUND 1002 | **PASS** |
| 10. 애플리케이션 E2E 테스트 | E2E-DB-L12 | 출력 줄 일치 | samples/db-commands.txt 출력 12번째 줄 | 13: RANGE 1000 1010 => OK 4 [1001, 1003, 1004, 1005] | 13: RANGE 1000 1010 => OK 4 [1001, 1003, 1004, 1005] | **PASS** |
| 10. 애플리케이션 E2E 테스트 | E2E-DB-L13 | 출력 줄 일치 | samples/db-commands.txt 출력 13번째 줄 | 14: INSERT 1006 C-GRACE 3000 2026-02-30 => ERROR order_date 가 올바른 yyyy-MM-dd 날짜가 아닙니다: 2026-02-30 | 14: INSERT 1006 C-GRACE 3000 2026-02-30 => ERROR order_date 가 올바른 yyyy-MM-dd 날짜가 아닙니다: 2026-02-30 | **PASS** |
| 10. 애플리케이션 E2E 테스트 | E2E-DB-L14 | 출력 줄 일치 | samples/db-commands.txt 출력 14번째 줄 | 15: INSERT 1006 C-GRACE 3000 2026-10-07 => OK INSERTED 1006 -> RID(0,3) | 15: INSERT 1006 C-GRACE 3000 2026-10-07 => OK INSERTED 1006 -> RID(0,3) | **PASS** |
| 10. 애플리케이션 E2E 테스트 | E2E-DB-L15 | 출력 줄 일치 | samples/db-commands.txt 출력 15번째 줄 | 16: SCAN => OK 5 [1001, 1003, 1004, 1005, 1006] | 16: SCAN => OK 5 [1001, 1003, 1004, 1005, 1006] | **PASS** |
| 10. 애플리케이션 E2E 테스트 | E2E-DB-L16 | 출력 줄 일치 | samples/db-commands.txt 출력 16번째 줄 | 17: FOO 1 => ERROR 알 수 없는 명령: FOO | 17: FOO 1 => ERROR 알 수 없는 명령: FOO | **PASS** |
| 10. 애플리케이션 E2E 테스트 | E2E-DB-L17 | 출력 줄 일치 | samples/db-commands.txt 출력 17번째 줄 | # summary ok=11 fail=3 error=2 rows=5 indexHeight=1 | # summary ok=11 fail=3 error=2 rows=5 indexHeight=1 | **PASS** |
| 10. 애플리케이션 E2E 테스트 | E2E-FS-N | 출력 줄 수 | samples/fs-commands.txt → build/e2e/fs-output.txt | 16 | 16 | **PASS** |
| 10. 애플리케이션 E2E 테스트 | E2E-FS-L01 | 출력 줄 일치 | samples/fs-commands.txt 출력 1번째 줄 | 2: MKDIR /home leem => OK /home | 2: MKDIR /home leem => OK /home | **PASS** |
| 10. 애플리케이션 E2E 테스트 | E2E-FS-L02 | 출력 줄 일치 | samples/fs-commands.txt 출력 2번째 줄 | 3: MKDIR /home/leem leem => OK /home/leem | 3: MKDIR /home/leem leem => OK /home/leem | **PASS** |
| 10. 애플리케이션 E2E 테스트 | E2E-FS-L03 | 출력 줄 일치 | samples/fs-commands.txt 출력 3번째 줄 | 4: MKDIR /home/leem/photos leem => OK /home/leem/photos | 4: MKDIR /home/leem/photos leem => OK /home/leem/photos | **PASS** |
| 10. 애플리케이션 E2E 테스트 | E2E-FS-L04 | 출력 줄 일치 | samples/fs-commands.txt 출력 4번째 줄 | 5: CREATE /home/leem/photos/2026-10-01.jpg 1200 leem => OK /home/leem/photos/2026-10-01.jpg | 5: CREATE /home/leem/photos/2026-10-01.jpg 1200 leem => OK /home/leem/photos/2026-10-01.jpg | **PASS** |
| 10. 애플리케이션 E2E 테스트 | E2E-FS-L05 | 출력 줄 일치 | samples/fs-commands.txt 출력 5번째 줄 | 6: CREATE /home/leem/photos/2026-10-05.jpg 1500 leem => OK /home/leem/photos/2026-10-05.jpg | 6: CREATE /home/leem/photos/2026-10-05.jpg 1500 leem => OK /home/leem/photos/2026-10-05.jpg | **PASS** |
| 10. 애플리케이션 E2E 테스트 | E2E-FS-L06 | 출력 줄 일치 | samples/fs-commands.txt 출력 6번째 줄 | 7: CREATE /home/leem/photos/2026-10-09.jpg 1800 leem => OK /home/leem/photos/2026-10-09.jpg | 7: CREATE /home/leem/photos/2026-10-09.jpg 1800 leem => OK /home/leem/photos/2026-10-09.jpg | **PASS** |
| 10. 애플리케이션 E2E 테스트 | E2E-FS-L07 | 출력 줄 일치 | samples/fs-commands.txt 출력 7번째 줄 | 8: CREATE /home/leem/todo.txt 120 leem => OK /home/leem/todo.txt | 8: CREATE /home/leem/todo.txt 120 leem => OK /home/leem/todo.txt | **PASS** |
| 10. 애플리케이션 E2E 테스트 | E2E-FS-L08 | 출력 줄 일치 | samples/fs-commands.txt 출력 8번째 줄 | 9: LS /home/leem => OK 2 [/home/leem/photos, /home/leem/todo.txt] | 9: LS /home/leem => OK 2 [/home/leem/photos, /home/leem/todo.txt] | **PASS** |
| 10. 애플리케이션 E2E 테스트 | E2E-FS-L09 | 출력 줄 일치 | samples/fs-commands.txt 출력 9번째 줄 | 10: RANGE /home/leem/photos/2026-10-02 /home/leem/photos/2026-10-10 => OK 2 [/home/leem/photos/2026-10-05.jpg, /home/leem/photos/2026-10-09.jpg] | 10: RANGE /home/leem/photos/2026-10-02 /home/leem/photos/2026-10-10 => OK 2 [/home/leem/photos/2026-10-05.jpg, /home/leem/photos/2026-10-09.jpg] | **PASS** |
| 10. 애플리케이션 E2E 테스트 | E2E-FS-L10 | 출력 줄 일치 | samples/fs-commands.txt 출력 10번째 줄 | 11: STAT /home/leem/todo.txt => OK /home/leem/todo.txt FILE id=8 size=120 owner=leem | 11: STAT /home/leem/todo.txt => OK /home/leem/todo.txt FILE id=8 size=120 owner=leem | **PASS** |
| 10. 애플리케이션 E2E 테스트 | E2E-FS-L11 | 출력 줄 일치 | samples/fs-commands.txt 출력 11번째 줄 | 12: DELETE /home/leem/photos => FAIL DIRECTORY_NOT_EMPTY /home/leem/photos | 12: DELETE /home/leem/photos => FAIL DIRECTORY_NOT_EMPTY /home/leem/photos | **PASS** |
| 10. 애플리케이션 E2E 테스트 | E2E-FS-L12 | 출력 줄 일치 | samples/fs-commands.txt 출력 12번째 줄 | 13: DELETE /home/leem/photos/2026-10-01.jpg => OK /home/leem/photos/2026-10-01.jpg | 13: DELETE /home/leem/photos/2026-10-01.jpg => OK /home/leem/photos/2026-10-01.jpg | **PASS** |
| 10. 애플리케이션 E2E 테스트 | E2E-FS-L13 | 출력 줄 일치 | samples/fs-commands.txt 출력 13번째 줄 | 14: FIND /home => OK 5 [/home/leem, /home/leem/photos, /home/leem/photos/2026-10-05.jpg, /home/leem/photos/2026-10-09.jpg, /home/leem/todo.txt] | 14: FIND /home => OK 5 [/home/leem, /home/leem/photos, /home/leem/photos/2026-10-05.jpg, /home/leem/photos/2026-10-09.jpg, /home/leem/todo.txt] | **PASS** |
| 10. 애플리케이션 E2E 테스트 | E2E-FS-L14 | 출력 줄 일치 | samples/fs-commands.txt 출력 14번째 줄 | 15: CREATE /home/leem/todo.txt 1 leem => FAIL ALREADY_EXISTS /home/leem/todo.txt | 15: CREATE /home/leem/todo.txt 1 leem => FAIL ALREADY_EXISTS /home/leem/todo.txt | **PASS** |
| 10. 애플리케이션 E2E 테스트 | E2E-FS-L15 | 출력 줄 일치 | samples/fs-commands.txt 출력 15번째 줄 | 16: CREATE relative.txt 1 leem => FAIL INVALID_PATH relative.txt | 16: CREATE relative.txt 1 leem => FAIL INVALID_PATH relative.txt | **PASS** |
| 10. 애플리케이션 E2E 테스트 | E2E-FS-L16 | 출력 줄 일치 | samples/fs-commands.txt 출력 16번째 줄 | # summary ok=12 fail=3 error=0 entries=7 indexHeight=2 | # summary ok=12 fail=3 error=0 entries=7 indexHeight=2 | **PASS** |
| 10. 애플리케이션 E2E 테스트 | E2E-ERR1 | 입력 파일이 없으면 오류 메시지 출력, 출력 파일 생성 안 함 | DbIndexApp samples/does-not-exist.txt | 오류 메시지 출력 / 출력 파일 없음 | 오류 메시지 출력 / 출력 파일 없음 | **PASS** |
| 10. 애플리케이션 E2E 테스트 | E2E-ERR2 | 잘못된 최소 차수 인자 처리 | FsIndexApp ... 1 | [오류] 최소 차수는 2 이상 65536 이하의 정수여야 합니다. | [오류] 최소 차수는 2 이상 65536 이하의 정수여야 합니다. | **PASS** |
| 10. 애플리케이션 E2E 테스트 | E2E-ERR3 | 범위를 넘는 최소 차수 인자도 비정상 종료 없이 처리 (R-002) | DbIndexApp ... 2000000000 | [오류] 최소 차수는 2 이상 65536 이하의 정수여야 합니다. | [오류] 최소 차수는 2 이상 65536 이하의 정수여야 합니다. | **PASS** |
