#!/usr/bin/env bash
# 사용법: ./build-and-test.sh [라벨] [seed]
# 1) main/test 컴파일 → 2) 전체 테스트 실행 (외부 타임아웃 600초) → test-results/<라벨>.md
set -u
cd "$(dirname "$0")"
LABEL="${1:-run}"
SEED="${2:-20261024}"
rm -rf build && mkdir -p build/main build/test
javac -encoding UTF-8 -Xlint:all -d build/main $(find src/main -name '*.java') || { echo "COMPILE FAIL (main)"; exit 2; }
javac -encoding UTF-8 -Xlint:all -cp build/main -d build/test $(find src/test -name '*.java') || { echo "COMPILE FAIL (test)"; exit 2; }
timeout 600 java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp build/main:build/test edu.btree.test.TestMain "$LABEL" "$SEED" test-results
EOF_STATUS=$?
[ $EOF_STATUS -eq 124 ] && echo "TIMEOUT (600초 초과)"
exit $EOF_STATUS
