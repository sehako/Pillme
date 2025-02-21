FROM openjdk:17-jdk
LABEL maintainer="sehako"

# JAR 파일 경로 (Maven build 후 target 폴더 안에 위치한다고 가정)
ARG JAR_FILE=./build/libs/app.jar

# JAR 파일 복사
COPY ${JAR_FILE} app.jar

# 앱 실행 명령어 추가 (필요시)
ENTRYPOINT ["java", "-jar", "/app.jar"]
