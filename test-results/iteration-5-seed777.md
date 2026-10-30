# 테스트 결과: iteration-5-seed777

- 실행 라벨: `iteration-5-seed777`, seed: `777`, 총 소요: 47.91초
- 결과: **PASS 187 / FAIL 0 / NOT RUN 0** (총 187건)
- Java: 21.0.12.1

## 스위트별 소요 시간
- 1. Invariant checker test: 1.15초
- 2. Differential test: 4.44초
- 3. Stateful property-based test: 0.59초
- 4. Edge case test: 0.08초
- 5. Structural coverage test: 0.45초
- 6. Adversarial sequence test: 40.68초
- 7. DB 인덱스 모듈 기능 테스트: 0.07초
- 8. FS 인덱스 모듈 기능 테스트: 0.39초
- 9. 회귀 테스트: 0.00초
- 10. 애플리케이션 E2E 테스트: 0.04초

| 스위트 | ID | 테스트 | 입력 | 예상 결과 | 실제 결과 | 결과 |
|---|---|---|---|---|---|---|
| 1. Invariant checker test | INV-01 | t=2 무작위 삽입/삭제 후 매 연산마다 불변식 검사 | t=2, 키 0~799, 연산 6000회(삽입 60%/삭제 40%), seed=779 | 위반 0건 | 위반 0건 | **PASS** |
| 1. Invariant checker test | INV-02 | t=3 무작위 삽입/삭제 후 매 연산마다 불변식 검사 | t=3, 키 0~799, 연산 6000회(삽입 60%/삭제 40%), seed=780 | 위반 0건 | 위반 0건 | **PASS** |
| 1. Invariant checker test | INV-03 | t=4 무작위 삽입/삭제 후 매 연산마다 불변식 검사 | t=4, 키 0~799, 연산 6000회(삽입 60%/삭제 40%), seed=781 | 위반 0건 | 위반 0건 | **PASS** |
| 1. Invariant checker test | INV-04 | t=5 무작위 삽입/삭제 후 매 연산마다 불변식 검사 | t=5, 키 0~799, 연산 6000회(삽입 60%/삭제 40%), seed=782 | 위반 0건 | 위반 0건 | **PASS** |
| 1. Invariant checker test | INV-05 | t=6 무작위 삽입/삭제 후 매 연산마다 불변식 검사 | t=6, 키 0~799, 연산 6000회(삽입 60%/삭제 40%), seed=783 | 위반 0건 | 위반 0건 | **PASS** |
| 1. Invariant checker test | INV-M00 | 정상 기준 스냅샷은 위반 없음 | ((5) 10 (15)) | 위반 0건 | 위반 0건 | **PASS** |
| 1. Invariant checker test | INV-M01 | 검사기가 손상을 검출: 노드 내 키 순서 뒤바뀜 | t=2 ((6 5) 10 (15)) size=4 | 검출: KEY_ORDER | 검출: KEY_ORDER | **PASS** |
| 1. Invariant checker test | INV-M02 | 검사기가 손상을 검출: 비루트 노드 키 부족 (t=3, 키 1개) | t=3 ((5) 10 (15 16)) size=4 | 검출: KEY_COUNT | 검출: KEY_COUNT | **PASS** |
| 1. Invariant checker test | INV-M03 | 검사기가 손상을 검출: 노드 키 초과 (t=2, 키 4개) | t=2 ((1 2 3 4) 10 (15)) size=6 | 검출: KEY_COUNT | 검출: KEY_COUNT | **PASS** |
| 1. Invariant checker test | INV-M04 | 검사기가 손상을 검출: 자식 키가 구분자 구간 밖 | t=2 ((5) 10 (7)) size=3 | 검출: CHILD_RANGE | 검출: CHILD_RANGE | **PASS** |
| 1. Invariant checker test | INV-M05 | 검사기가 손상을 검출: 내부 노드 자식 수 ≠ 키 수+1 | t=2 ((5) 10 (15) 20 ∅) size=4 | 검출: CHILD_COUNT | 검출: CHILD_COUNT | **PASS** |
| 1. Invariant checker test | INV-M06 | 검사기가 손상을 검출: 리프 깊이 불일치 | t=2 ((5) 10 ((15) 20 (25))) size=5 | 검출: LEAF_DEPTH | 검출: LEAF_DEPTH | **PASS** |
| 1. Invariant checker test | INV-M07 | 검사기가 손상을 검출: 키 없는 내부 루트 | t=2 ((5)) size=1 | 검출: ROOT | 검출: ROOT | **PASS** |
| 1. Invariant checker test | INV-M08 | 검사기가 손상을 검출: 같은 노드 id 가 두 번 참조됨 | t=2 ((5) 10 (15)) size=3 | 검출: ALIAS | 검출: ALIAS | **PASS** |
| 1. Invariant checker test | INV-M09 | 검사기가 손상을 검출: 미사용 슬롯에 참조 잔존 | t=2 ((5) 10 (15)) size=3 | 검출: STALE_SLOT | 검출: STALE_SLOT | **PASS** |
| 1. Invariant checker test | INV-M10 | 검사기가 손상을 검출: 기록된 size 와 실제 키 수 불일치 | t=2 ((5) 10 (15)) size=4 | 검출: SIZE | 검출: SIZE | **PASS** |
| 1. Invariant checker test | INV-M11 | 검사기가 손상을 검출: 키-값 대응 손상 | t=2 ((5) 10 (15)) size=3 | 검출: VALUE | 검출: VALUE | **PASS** |
| 2. Differential test | DIFF-01 | t=2, 키 0~63 무작위 연산이 TreeMap/TreeSet 과 일치 | 삽입40/삭제35/검색15/범위10%, 시드 2개 × 20000회, seed=777 | 모든 반환값·키 집합 일치 | 모든 반환값·키 집합 일치 | **PASS** |
| 2. Differential test | DIFF-02 | t=2, 키 0~4999 무작위 연산이 TreeMap/TreeSet 과 일치 | 삽입40/삭제35/검색15/범위10%, 시드 2개 × 20000회, seed=777 | 모든 반환값·키 집합 일치 | 모든 반환값·키 집합 일치 | **PASS** |
| 2. Differential test | DIFF-03 | t=2, 키 전체 int 무작위 연산이 TreeMap/TreeSet 과 일치 | 삽입40/삭제35/검색15/범위10%, 시드 2개 × 20000회, seed=777 | 모든 반환값·키 집합 일치 | 모든 반환값·키 집합 일치 | **PASS** |
| 2. Differential test | DIFF-04 | t=3, 키 0~63 무작위 연산이 TreeMap/TreeSet 과 일치 | 삽입40/삭제35/검색15/범위10%, 시드 2개 × 20000회, seed=777 | 모든 반환값·키 집합 일치 | 모든 반환값·키 집합 일치 | **PASS** |
| 2. Differential test | DIFF-05 | t=3, 키 0~4999 무작위 연산이 TreeMap/TreeSet 과 일치 | 삽입40/삭제35/검색15/범위10%, 시드 2개 × 20000회, seed=777 | 모든 반환값·키 집합 일치 | 모든 반환값·키 집합 일치 | **PASS** |
| 2. Differential test | DIFF-06 | t=3, 키 전체 int 무작위 연산이 TreeMap/TreeSet 과 일치 | 삽입40/삭제35/검색15/범위10%, 시드 2개 × 20000회, seed=777 | 모든 반환값·키 집합 일치 | 모든 반환값·키 집합 일치 | **PASS** |
| 2. Differential test | DIFF-07 | t=4, 키 0~63 무작위 연산이 TreeMap/TreeSet 과 일치 | 삽입40/삭제35/검색15/범위10%, 시드 2개 × 20000회, seed=777 | 모든 반환값·키 집합 일치 | 모든 반환값·키 집합 일치 | **PASS** |
| 2. Differential test | DIFF-08 | t=4, 키 0~4999 무작위 연산이 TreeMap/TreeSet 과 일치 | 삽입40/삭제35/검색15/범위10%, 시드 2개 × 20000회, seed=777 | 모든 반환값·키 집합 일치 | 모든 반환값·키 집합 일치 | **PASS** |
| 2. Differential test | DIFF-09 | t=4, 키 전체 int 무작위 연산이 TreeMap/TreeSet 과 일치 | 삽입40/삭제35/검색15/범위10%, 시드 2개 × 20000회, seed=777 | 모든 반환값·키 집합 일치 | 모든 반환값·키 집합 일치 | **PASS** |
| 2. Differential test | DIFF-10 | t=7, 키 0~63 무작위 연산이 TreeMap/TreeSet 과 일치 | 삽입40/삭제35/검색15/범위10%, 시드 2개 × 20000회, seed=777 | 모든 반환값·키 집합 일치 | 모든 반환값·키 집합 일치 | **PASS** |
| 2. Differential test | DIFF-11 | t=7, 키 0~4999 무작위 연산이 TreeMap/TreeSet 과 일치 | 삽입40/삭제35/검색15/범위10%, 시드 2개 × 20000회, seed=777 | 모든 반환값·키 집합 일치 | 모든 반환값·키 집합 일치 | **PASS** |
| 2. Differential test | DIFF-12 | t=7, 키 전체 int 무작위 연산이 TreeMap/TreeSet 과 일치 | 삽입40/삭제35/검색15/범위10%, 시드 2개 × 20000회, seed=777 | 모든 반환값·키 집합 일치 | 모든 반환값·키 집합 일치 | **PASS** |
| 2. Differential test | DIFF-13 | t=16, 키 0~63 무작위 연산이 TreeMap/TreeSet 과 일치 | 삽입40/삭제35/검색15/범위10%, 시드 2개 × 20000회, seed=777 | 모든 반환값·키 집합 일치 | 모든 반환값·키 집합 일치 | **PASS** |
| 2. Differential test | DIFF-14 | t=16, 키 0~4999 무작위 연산이 TreeMap/TreeSet 과 일치 | 삽입40/삭제35/검색15/범위10%, 시드 2개 × 20000회, seed=777 | 모든 반환값·키 집합 일치 | 모든 반환값·키 집합 일치 | **PASS** |
| 2. Differential test | DIFF-15 | t=16, 키 전체 int 무작위 연산이 TreeMap/TreeSet 과 일치 | 삽입40/삭제35/검색15/범위10%, 시드 2개 × 20000회, seed=777 | 모든 반환값·키 집합 일치 | 모든 반환값·키 집합 일치 | **PASS** |
| 3. Stateful property-based test | PBT-01 | 무작위 명령 시퀀스에서 속성 P1~P5 성립 | 시퀀스 600개(최대 길이 250, t=2~5, 키 도메인 4~303), seed=777 | 속성 위반 0건 | 속성 위반 0건 | **PASS** |
| 3. Stateful property-based test | PBT-02 | 생성된 명령 수 (실행 규모 기록) | 위와 동일 | 1개 이상 | 1개 이상 | **PASS** |
| 3. Stateful property-based test | PBT-03 | 축소기 자체 검증: 거짓 속성의 반례를 최소로 줄임 | 거짓 속성 '100 이상 키 3개 미만', 원래 반례 172개 명령 | insert 3개(키 ≥ 100)로 축소 | insert 3개(키 ≥ 100)로 축소 | **PASS** |
| 4. Edge case test | EDGE-01 | 빈 트리 검색/삭제/범위/순회 | t=2, 키 없음; search(1), delete(1), rangeSearch(null,null), inOrder | null null [] [] 높이1 () 위반 0건 | null null [] [] 높이1 () 위반 0건 | **PASS** |
| 4. Edge case test | EDGE-02 | 단일 키 삽입·검색·삭제 | t=2, insert 42 → search 42 → delete 42 | (42) 1309 → 1309 () size0 위반 0건 | (42) 1309 → 1309 () size0 위반 0건 | **PASS** |
| 4. Edge case test | EDGE-03 | 존재하지 않는 키의 검색·삭제는 null, 구조 불변 | t=2, 키 1~20; search(99), delete(99), delete(0) | null null null 구조불변 size20 | null null null 구조불변 size20 | **PASS** |
| 4. Edge case test | EDGE-04 | 루트 분할: 가득 찬 루트(2t-1)에 삽입 | t=3, insert 1..5 → insert 6 | (1 2 3 4 5) 높이1 → ((1 2) 3 (4 5 6)) 높이2 ROOT_SPLIT=1 | (1 2 3 4 5) 높이1 → ((1 2) 3 (4 5 6)) 높이2 ROOT_SPLIT=1 | **PASS** |
| 4. Edge case test | EDGE-05 | 루트 축소: 병합으로 루트가 비면 높이 감소 | t=3, insert 1..6, delete 6 → delete 1 | ((1 2) 3 (4 5)) → (2 3 4 5) 높이1 ROOT_SHRINK=1 위반 0건 | ((1 2) 3 (4 5)) → (2 3 4 5) 높이1 ROOT_SHRINK=1 위반 0건 | **PASS** |
| 4. Edge case test | EDGE-06 | 최소 키 노드 삭제 → 오른쪽 형제 재분배 | t=3, ((1 2) 3 (4 5 6)) 에서 delete 1 | ((2 3) 4 (5 6)) BORROW_RIGHT_LEAF=1 | ((2 3) 4 (5 6)) BORROW_RIGHT_LEAF=1 | **PASS** |
| 4. Edge case test | EDGE-07 | 최소 키 노드 삭제 → 왼쪽 형제 재분배 | t=3, insert 1..6, delete 6, insert 0 → delete 4 | ((0 1 2) 3 (4 5)) → ((0 1) 2 (3 5)) BORROW_LEFT_LEAF=1 | ((0 1 2) 3 (4 5)) → ((0 1) 2 (3 5)) BORROW_LEFT_LEAF=1 | **PASS** |
| 4. Edge case test | EDGE-08 | 최소 키 노드 삭제 → 형제와 병합(+루트 축소) | t=3, ((1 2) 3 (4 5)) 에서 delete 4 | (1 2 3 5) LEAF_MERGE=1 | (1 2 3 5) LEAF_MERGE=1 | **PASS** |
| 4. Edge case test | EDGE-09 | 내부 키 삭제 2a: 왼쪽 자식 ≥ t → 선행자로 대체 | t=3, ((0 1 2) 3 (4 5)) 에서 delete 3 | ((0 1) 2 (4 5)) 2a=1 | ((0 1) 2 (4 5)) 2a=1 | **PASS** |
| 4. Edge case test | EDGE-10 | 내부 키 삭제 2b: 오른쪽 자식 ≥ t → 후속자로 대체 | t=3, ((1 2) 3 (4 5 6)) 에서 delete 3 | ((1 2) 4 (5 6)) 2b=1 | ((1 2) 4 (5 6)) 2b=1 | **PASS** |
| 4. Edge case test | EDGE-11 | 내부 키 삭제 2c: 양쪽 자식 t-1 → 병합 후 삭제 | t=3, ((1 2) 3 (4 5)) 에서 delete 3 | (1 2 4 5) 2c=1 ROOT_SHRINK=1 | (1 2 4 5) 2c=1 ROOT_SHRINK=1 | **PASS** |
| 4. Edge case test | EDGE-12 | 중복 키 삽입 거부: 값·구조 유지 | t=2, 키 1..5 에 insert(3, -1) | false 100 구조불변 | false 100 구조불변 | **PASS** |
| 4. Edge case test | EDGE-13 | null 키/값과 잘못된 차수 | insert(null,1), insert(1,null), search(null), delete(null), new BTree(1), new BTree(0) | NullPointerException NullPointerException NullPointerException NullPointerException IllegalArgumentException IllegalArgumentException size1 | NullPointerException NullPointerException NullPointerException NullPointerException IllegalArgumentException IllegalArgumentException size1 | **PASS** |
| 4. Edge case test | EDGE-14 | 정수 최솟값·최댓값 키 | t=2, insert MAX, 0, MIN, -1 → inOrder, rangeSearch(MIN, MIN) | [-2147483648, -1, 0, 2147483647] [-2147483648] | [-2147483648, -1, 0, 2147483647] [-2147483648] | **PASS** |
| 4. Edge case test | EDGE-15 | 범위 검색 경계 조건 | t=2, 키 10..100(10 간격): [50,50] (50,50] [60,40] [35,∞)limit1 limit0 [15,45] (10,30] | [50] [] [] [40] [] [20, 30, 40] [20, 30] | [50] [] [] [40] [] [20, 30, 40] [20, 30] | **PASS** |
| 4. Edge case test | EDGE-16 | 큰 차수 t=64: 2t-1=127개까지 루트 하나 | t=64, insert 0..126 → insert 127 | 높이1 → 높이2 위반 0건 | 높이1 → 높이2 위반 0건 | **PASS** |
| 4. Edge case test | EDGE-17 | 같은 키 삽입/삭제 1,000회 반복 | t=2, (insert 7, delete 7) × 1000 | true () size0 | true () size0 | **PASS** |
| 5. Structural coverage test | COV-01 | 경로 실행 여부: ROOT_SPLIT (루트 변경-분할) | t=2·3, 키 도메인 60/600/6000, 단계마다 삽입 70%↔30% 교대, 각 30,000회, seed=782 | 실행됨 | 실행됨 | **PASS** |
| 5. Structural coverage test | COV-02 | 경로 실행 여부: LEAF_SPLIT (노드 분할) | t=2·3, 키 도메인 60/600/6000, 단계마다 삽입 70%↔30% 교대, 각 30,000회, seed=782 | 실행됨 | 실행됨 | **PASS** |
| 5. Structural coverage test | COV-03 | 경로 실행 여부: INTERNAL_SPLIT (노드 분할) | t=2·3, 키 도메인 60/600/6000, 단계마다 삽입 70%↔30% 교대, 각 30,000회, seed=782 | 실행됨 | 실행됨 | **PASS** |
| 5. Structural coverage test | COV-04 | 경로 실행 여부: LEAF_MERGE (병합) | t=2·3, 키 도메인 60/600/6000, 단계마다 삽입 70%↔30% 교대, 각 30,000회, seed=782 | 실행됨 | 실행됨 | **PASS** |
| 5. Structural coverage test | COV-05 | 경로 실행 여부: INTERNAL_MERGE (병합) | t=2·3, 키 도메인 60/600/6000, 단계마다 삽입 70%↔30% 교대, 각 30,000회, seed=782 | 실행됨 | 실행됨 | **PASS** |
| 5. Structural coverage test | COV-06 | 경로 실행 여부: BORROW_LEFT_LEAF (키 재분배) | t=2·3, 키 도메인 60/600/6000, 단계마다 삽입 70%↔30% 교대, 각 30,000회, seed=782 | 실행됨 | 실행됨 | **PASS** |
| 5. Structural coverage test | COV-07 | 경로 실행 여부: BORROW_LEFT_INTERNAL (키 재분배) | t=2·3, 키 도메인 60/600/6000, 단계마다 삽입 70%↔30% 교대, 각 30,000회, seed=782 | 실행됨 | 실행됨 | **PASS** |
| 5. Structural coverage test | COV-08 | 경로 실행 여부: BORROW_RIGHT_LEAF (키 재분배) | t=2·3, 키 도메인 60/600/6000, 단계마다 삽입 70%↔30% 교대, 각 30,000회, seed=782 | 실행됨 | 실행됨 | **PASS** |
| 5. Structural coverage test | COV-09 | 경로 실행 여부: BORROW_RIGHT_INTERNAL (키 재분배) | t=2·3, 키 도메인 60/600/6000, 단계마다 삽입 70%↔30% 교대, 각 30,000회, seed=782 | 실행됨 | 실행됨 | **PASS** |
| 5. Structural coverage test | COV-10 | 경로 실행 여부: ROOT_SHRINK (루트 변경-축소) | t=2·3, 키 도메인 60/600/6000, 단계마다 삽입 70%↔30% 교대, 각 30,000회, seed=782 | 실행됨 | 실행됨 | **PASS** |
| 5. Structural coverage test | COV-11 | 경로 실행 여부: DELETE_FROM_LEAF (리프 키 삭제) | t=2·3, 키 도메인 60/600/6000, 단계마다 삽입 70%↔30% 교대, 각 30,000회, seed=782 | 실행됨 | 실행됨 | **PASS** |
| 5. Structural coverage test | COV-12 | 경로 실행 여부: DELETE_INTERNAL_BY_PREDECESSOR (내부 노드 키 삭제) | t=2·3, 키 도메인 60/600/6000, 단계마다 삽입 70%↔30% 교대, 각 30,000회, seed=782 | 실행됨 | 실행됨 | **PASS** |
| 5. Structural coverage test | COV-13 | 경로 실행 여부: DELETE_INTERNAL_BY_SUCCESSOR (내부 노드 키 삭제) | t=2·3, 키 도메인 60/600/6000, 단계마다 삽입 70%↔30% 교대, 각 30,000회, seed=782 | 실행됨 | 실행됨 | **PASS** |
| 5. Structural coverage test | COV-14 | 경로 실행 여부: DELETE_INTERNAL_BY_MERGE (내부 노드 키 삭제) | t=2·3, 키 도메인 60/600/6000, 단계마다 삽입 70%↔30% 교대, 각 30,000회, seed=782 | 실행됨 | 실행됨 | **PASS** |
| 5. Structural coverage test | COV-15 | 경로 실행 여부: DUPLICATE_INSERT_REJECTED (정책 분기) | t=2·3, 키 도메인 60/600/6000, 단계마다 삽입 70%↔30% 교대, 각 30,000회, seed=782 | 실행됨 | 실행됨 | **PASS** |
| 5. Structural coverage test | COV-16 | 경로 실행 여부: DELETE_MISSING_KEY (정책 분기) | t=2·3, 키 도메인 60/600/6000, 단계마다 삽입 70%↔30% 교대, 각 30,000회, seed=782 | 실행됨 | 실행됨 | **PASS** |
| 5. Structural coverage test | COV-F1 | 비루트 리프의 모든 채움 수준 관측 | t=3 워크로드, 25연산마다 표본 | 포함 [2, 3, 4, 5] | 포함 [2, 3, 4, 5] | **PASS** |
| 5. Structural coverage test | COV-F2 | 비루트 내부 노드의 모든 채움 수준 관측 | t=3 워크로드, 25연산마다 표본 | 포함 [2, 3, 4, 5] | 포함 [2, 3, 4, 5] | **PASS** |
| 5. Structural coverage test | COV-F3 | 루트의 모든 채움 수준 관측 | t=3 워크로드 | 포함 [0, 1, 2, 3, 4, 5] | 포함 [0, 1, 2, 3, 4, 5] | **PASS** |
| 5. Structural coverage test | COV-F4 | 높이 1~5 관측 (루트 분할·축소가 여러 층에서 발생) | t=3 워크로드 | 포함 [1, 2, 3, 4, 5] | 포함 [1, 2, 3, 4, 5] | **PASS** |
| 6. Adversarial sequence test | ADV-01 | 오름차순 삽입 | t=2,3,5, n=2000, 매 연산 후 전체 검사, seed=777 | 매 연산 후 불변식 유지 + 모델 일치 | 매 연산 후 불변식 유지 + 모델 일치 | **PASS** |
| 6. Adversarial sequence test | ADV-02 | 내림차순 삽입 | t=2,3,5, n=2000, 매 연산 후 전체 검사, seed=777 | 매 연산 후 불변식 유지 + 모델 일치 | 매 연산 후 불변식 유지 + 모델 일치 | **PASS** |
| 6. Adversarial sequence test | ADV-03 | 오름차순 삽입 후 역순(내림차순) 삭제 | t=2,3,5, n=2000, 매 연산 후 전체 검사, seed=777 | 매 연산 후 불변식 유지 + 모델 일치 | 매 연산 후 불변식 유지 + 모델 일치 | **PASS** |
| 6. Adversarial sequence test | ADV-04 | 내림차순 삽입 후 역순(오름차순) 삭제 | t=2,3,5, n=2000, 매 연산 후 전체 검사, seed=777 | 매 연산 후 불변식 유지 + 모델 일치 | 매 연산 후 불변식 유지 + 모델 일치 | **PASS** |
| 6. Adversarial sequence test | ADV-05 | 반복적인 삽입과 삭제 (무작위 키, 10라운드) | t=2,3,5, n=2000, 매 연산 후 전체 검사, seed=777 | 매 연산 후 불변식 유지 + 모델 일치 | 매 연산 후 불변식 유지 + 모델 일치 | **PASS** |
| 6. Adversarial sequence test | ADV-06 | 분할·병합 유도: 같은 키 삽입 직후 삭제 반복 | t=2,3,5, n=2000, 매 연산 후 전체 검사, seed=777 | 매 연산 후 불변식 유지 + 모델 일치 | 매 연산 후 불변식 유지 + 모델 일치 | **PASS** |
| 6. Adversarial sequence test | ADV-07 | 분할·병합 유도: 지그재그 삽입 후 가운데부터 삭제 | t=2,3,5, n=2000, 매 연산 후 전체 검사, seed=777 | 매 연산 후 불변식 유지 + 모델 일치 | 매 연산 후 불변식 유지 + 모델 일치 | **PASS** |
| 6. Adversarial sequence test | ADV-08 | 루트 키 저격: 현재 루트의 키만 삭제 | t=2,3,5, n=2000, 매 연산 후 전체 검사, seed=777 | 매 연산 후 불변식 유지 + 모델 일치 | 매 연산 후 불변식 유지 + 모델 일치 | **PASS** |
| 6. Adversarial sequence test | ADV-09 | 구분자 저격: 내부 노드 키만 골라 삭제 후 재삽입 | t=2,3,5, n=2000, 매 연산 후 전체 검사, seed=777 | 매 연산 후 불변식 유지 + 모델 일치 | 매 연산 후 불변식 유지 + 모델 일치 | **PASS** |
| 6. Adversarial sequence test | ADV-10 | 톱니파: 대량 삽입 후 90% 삭제 × 10 | t=2,3,5, n=2000, 매 연산 후 전체 검사, seed=777 | 매 연산 후 불변식 유지 + 모델 일치 | 매 연산 후 불변식 유지 + 모델 일치 | **PASS** |
| 6. Adversarial sequence test | ADV-11 | 무효 연산 폭주: 중복 삽입·없는 키 삭제·검색 (구조와 이벤트 수 불변) | t=2,3,5, n=2000, 매 연산 후 전체 검사, seed=777 | 매 연산 후 불변식 유지 + 모델 일치 | 매 연산 후 불변식 유지 + 모델 일치 | **PASS** |
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
| 7. DB 인덱스 모듈 기능 테스트 | DB-M01 | 무작위 INSERT/DELETE/SELECT 5,000회가 TreeMap 모델과 일치 | id 1~800, t=3, seed=788 | 일치 | 일치 | **PASS** |
| 7. DB 인덱스 모듈 기능 테스트 | DB-M02 | 인덱스 항목 수 = 힙 레코드 수 = 모델 크기 | 위와 동일 | 495/495/495 | 495/495/495 | **PASS** |
| 7. DB 인덱스 모듈 기능 테스트 | DB-M03 | 작업 후 기본키 인덱스 B-Tree 불변식 | 위와 동일 | 위반 0건 | 위반 0건 | **PASS** |
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
| 8. FS 인덱스 모듈 기능 테스트 | FS-M01 | 무작위 create 3,000 / delete 1,200 의 결과 코드 | 이름 알파벳 'ab-.0z'(‘/’ 인접 문자 포함), t=2, seed=876 | 모두 기대값과 일치 | 모두 기대값과 일치 | **PASS** |
| 8. FS 인덱스 모듈 기능 테스트 | FS-M02 | 경로 항목 수 = inode 수 = 기대 항목 수 | 위와 동일 | 2095/2095 | 2095/2095 | **PASS** |
| 8. FS 인덱스 모듈 기능 테스트 | FS-M03 | 모든 디렉터리의 LS 결과 = 브루트포스 | 디렉터리 881개 | 전부 일치 | 전부 일치 | **PASS** |
| 8. FS 인덱스 모듈 기능 테스트 | FS-M04 | 모든 디렉터리의 FIND 결과 = 브루트포스 | 디렉터리 881개 | 전부 일치 | 전부 일치 | **PASS** |
| 8. FS 인덱스 모듈 기능 테스트 | FS-M05 | 경로 검색과 파일 ID 검색이 같은 메타데이터를 가리킴 | 모든 경로 | 일치 | 일치 | **PASS** |
| 8. FS 인덱스 모듈 기능 테스트 | FS-M06 | 작업 후 경로 인덱스 B-Tree 불변식 | 위와 동일 | 위반 0건 | 위반 0건 | **PASS** |
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
