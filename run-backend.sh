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
# JAVA_HOME 환경 변수가 설정되어 있으면 사용, 아니면 기본 JDK 사용
if [ -n "$JAVA_HOME" ]; then
  ./gradlew -Dorg.gradle.java.home="$JAVA_HOME" clean bootJar
else
  ./gradlew clean bootJar
fi

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
if ! docker info > /dev/null 2>&1; then
  echo "Docker가 실행 중이지 않습니다. Docker를 시작한 후 다시 시도해 주세요."
  exit 1
fi

# Docker 명령 실행 및 오류 처리
if ! docker compose down; then
  echo "Docker Compose down 명령 실패!"
  exit 1
fi

if ! docker compose build; then
  echo "Docker Compose build 명령 실패!"
  exit 1
fi

docker image prune -f

if ! docker compose up -d; then
  echo "Docker Compose up 명령 실패!"
  exit 1
fi

echo "백엔드 서비스 배포 완료!"