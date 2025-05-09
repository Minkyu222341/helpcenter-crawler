#!/bin/bash
# 스크립트에 실행 권한 부여
chmod +x "$0"

export SPRING_PROFILES_ACTIVE=prod,secret

# 환경 변수 파일 로드
if [ ! -f .env ]; then
  echo ".env 파일을 생성하고 필요한 환경변수를 설정해주세요."
  exit 1
else
  source .env
  echo ".env 파일을 로드했습니다."
fi

# 현재 디렉토리 저장
ROOT_DIR=$(pwd)

# 백엔드 빌드
# shellcheck disable=SC2164
./gradlew -Dorg.gradle.java.home="C:/Program Files/Java/jdk-17" clean bootJar

# 루트 디렉토리로 돌아가기
# shellcheck disable=SC2164
cd "$ROOT_DIR"

# 명시적으로 기존 컨테이너 중지 및 제거
echo "기존 컨테이너 정리 중..."
docker stop redis-main 2>/dev/null || true
docker rm redis-main 2>/dev/null || true
docker stop helpcenter-backend 2>/dev/null || true
docker rm helpcenter-backend 2>/dev/null || true


# Docker 컨테이너 관리
docker compose down
docker compose build
docker image prune -f
docker compose up -d

echo "백엔드 서비스 배포 완료!"