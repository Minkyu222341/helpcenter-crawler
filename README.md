![CodeRabbit Pull Request Reviews](https://img.shields.io/coderabbit/prs/github/Minkyu222341/helpcenter-crawler?utm_source=oss&utm_medium=github&utm_campaign=Minkyu222341%2Fhelpcenter-crawler&labelColor=171717&color=FF570A&link=https%3A%2F%2Fcoderabbit.ai&label=CodeRabbit+Reviews)
---

# 헬프센터 크롤러

## 빌드 및 실행 방법

### 개발 환경 요구사항
- Java 17 이상
- Gradle 8.13 이상
- Docker 및 Docker Compose

---

### Docker를 사용한 빌드 및 실행

#### 🔹 자동화 스크립트 사용 (권장)
아래 명령어를 실행하면 전체 과정이 자동으로 수행됩니다.
```bash
./run-backend.sh
```

#### 🔹 수동 실행
1. 애플리케이션 빌드
   ```bash
   ./gradlew clean bootJar
   ```
2. Docker 이미지 빌드
   ```bash
   docker compose build --no-cache
   ```
3. Docker 컨테이너 실행
   ```bash
   docker compose up -d
   ```

---

### Docker 컨테이너 관리

#### 🔹 컨테이너 중지 및 삭제
```bash
docker compose down
```

#### 🔹 로그 확인
```bash
docker compose logs -f
```

#### 🔹 컨테이너 상태 확인
```bash
docker ps
```

#### 🔹 이미지 목록 확인
```bash
docker images
```

#### 🔹 불필요한 이미지 정리
```bash
docker image prune
```
---

### ⚠️ 주의사항
- `application-secret.yml` 파일은 민감한 정보를 포함하고 있으므로 GitHub에 커밋하지 마세요.
- Docker 환경에서는 Chrome이 Headless 모드로 실행되므로 브라우저 창이 표시되지 않습니다.
- 첫 실행 시 Docker 이미지 빌드에 시간이 소요될 수 있습니다.

