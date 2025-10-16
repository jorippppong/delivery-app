FROM eclipse-temurin:17-jre
WORKDIR /app

# 호스트에서 미리 빌드된 JAR만 복사하여 실행하는 경량 런타임 이미지
# JAR 경로는 Gradle 기본 산출물 위치에 맞춤
COPY build/libs/foodorder-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]


