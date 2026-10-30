package edu.btree.core;

/**
 * B-Tree 내부에서 일어나는 구조 변경/분기 이벤트.
 * 트리는 이벤트별 실행 횟수를 세어 두고, 테스트는 이를 이용해 구조 커버리지를 확인한다.
 */
public enum StructureEvent {
    /** 루트가 가득 차 새 루트를 만들고 분할 (높이 +1) */
    ROOT_SPLIT,
    /** 리프 노드 분할 */
    LEAF_SPLIT,
    /** 내부 노드 분할 */
    INTERNAL_SPLIT,
    /** 리프 노드 두 개 병합 */
    LEAF_MERGE,
    /** 내부 노드 두 개 병합 */
    INTERNAL_MERGE,
    /** 왼쪽 형제에서 키 재분배 (리프) */
    BORROW_LEFT_LEAF,
    /** 왼쪽 형제에서 키 재분배 (내부 노드, 자식 포인터 이동 포함) */
    BORROW_LEFT_INTERNAL,
    /** 오른쪽 형제에서 키 재분배 (리프) */
    BORROW_RIGHT_LEAF,
    /** 오른쪽 형제에서 키 재분배 (내부 노드) */
    BORROW_RIGHT_INTERNAL,
    /** 루트가 비어 유일한 자식이 새 루트가 됨 (높이 -1) */
    ROOT_SHRINK,
    /** 삭제 경우 1: 리프에서 직접 제거 */
    DELETE_FROM_LEAF,
    /** 삭제 경우 2a: 내부 노드 키를 선행자로 대체 */
    DELETE_INTERNAL_BY_PREDECESSOR,
    /** 삭제 경우 2b: 내부 노드 키를 후속자로 대체 */
    DELETE_INTERNAL_BY_SUCCESSOR,
    /** 삭제 경우 2c: 내부 노드 키의 양쪽 자식을 병합한 뒤 삭제 */
    DELETE_INTERNAL_BY_MERGE,
    /** 중복 키 삽입 거부 */
    DUPLICATE_INSERT_REJECTED,
    /** 존재하지 않는 키 삭제 요청 */
    DELETE_MISSING_KEY
}
