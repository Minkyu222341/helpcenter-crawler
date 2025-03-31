FROM amazoncorretto:17.0.14

# 필요한 패키지 설치
RUN yum update -y && yum install -y \
    wget \
    unzip \
    tar \
    gzip

# Google Chrome 설치
RUN wget -q https://dl.google.com/linux/direct/google-chrome-stable_current_x86_64.rpm && \
    yum localinstall -y google-chrome-stable_current_x86_64.rpm && \
    rm google-chrome-stable_current_x86_64.rpm

WORKDIR /app

# 빌드된 JAR 파일 복사
COPY build/libs/app.jar app.jar

# 필요한 리소스 파일 복사
COPY src/main/resources/application.yml /app/src/main/resources/application.yml
COPY src/main/resources/application-secret.yml /app/src/main/resources/application-secret.yml

# Linux 환경에서 ChromeDriver 경로 설정
RUN mkdir -p /usr/bin
COPY src/main/resources/chromedriver/linux/chromedriver /usr/bin/chromedriver
RUN chmod +x /usr/bin/chromedriver

# 환경 변수 설정
ENV SPRING_PROFILES_ACTIVE=prod,secret
ENV CHROME_DRIVER_PATH=/usr/bin/chromedriver

# 포트 노출
EXPOSE 8080

# 실행 명령
ENTRYPOINT ["java", "-jar", "app.jar"]