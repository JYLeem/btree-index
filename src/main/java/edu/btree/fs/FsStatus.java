package edu.btree.fs;

/** 파일 시스템 연산 결과 (POSIX 오류 코드에 대응하는 이름) */
public enum FsStatus {
    OK,
    ALREADY_EXISTS,       // EEXIST
    NOT_FOUND,            // ENOENT
    PARENT_NOT_FOUND,     // ENOENT (상위 디렉터리 없음)
    NOT_A_DIRECTORY,      // ENOTDIR
    DIRECTORY_NOT_EMPTY,  // ENOTEMPTY
    INVALID_PATH,         // EINVAL
    INVALID_ARGUMENT,     // EINVAL
    ROOT_PROTECTED        // 루트 삭제 금지
}
