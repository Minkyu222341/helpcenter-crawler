#!/bin/bash

# 스크립트에 실행 권한 부여
chmod +x "$0"

echo "애플리케이션 빌드 시작..."
./gradlew clean bootJar -Dorg.gradle.java.home="C:/Program Files/Java/jdk-17"

echo "Docker 컨테이너 중지 및 삭제..."
docker compose down

echo "Docker 이미지 빌드..."
docker compose build --no-cache

echo "불필요한 이미지 정리..."
docker image prune -f

echo "Docker 컨테이너 시작..."
docker compose up -d

echo "배포 완료!"