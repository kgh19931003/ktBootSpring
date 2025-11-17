FROM eclipse-temurin:21-jdk

WORKDIR /app

# xargs를 포함한 유틸 설치
RUN apt-get update && apt-get install -y findutils


# 사전 빌드한 JAR 파일만 복사
COPY build/libs/portfolio_api-0.0.1-SNAPSHOT.jar app.jar


ENTRYPOINT ["java", "-jar", "app.jar"]

