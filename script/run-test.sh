#!/bin/bash

# 스크립트에 실행 권한 부여
chmod +x "$0"

export SPRING_PROFILES_ACTIVE=dev

if [ ! -f .env.domain ]; then
    echo ".env 파일을 생성하고 필요한 환경변수를 설정해주세요."
    exit 1
else
    source .env.domain
fi

echo "애플리케이션 빌드 시작..."
./gradlew -Dorg.gradle.java.home="C:/Program Files/Java/jdk-17" clean bootJar

echo "Docker 컨테이너 중지 및 삭제..."
docker compose down

echo "Docker 이미지 빌드..."
docker compose build

echo "불필요한 이미지 정리..."
docker image prune -f

echo "Docker 컨테이너 시작..."
docker compose up -d

echo "컨테이너 상태 확인..."
docker ps -a

echo "MySQL 컨테이너 로그 확인..."
docker logs helpcenter-mysql-db

echo "배포 완료!"