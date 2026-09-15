# syntax=docker/dockerfile:1

##########################
# 1-BOSQICH: Build
##########################
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Avval faqat pom.xml ni ko'chiramiz -> dependency'lar alohida layerda cache bo'ladi.
COPY pom.xml .
RUN mvn -B -DskipTests dependency:go-offline -q

# Loyiha kodini ko'chiramiz va extract qilamiz (tools rejimi bilan)
COPY src ./src
RUN mvn -B clean package -DskipTests -q \
    && java -Djarmode=tools -jar target/*.jar extract --layers --launcher --destination target/extracted

##########################
# 2-BOSQICH: Runtime
##########################
FROM eclipse-temurin:21-jre-alpine AS runtime

# Xavfsizlik: root emas, alohida non-root user ostida ishga tushiramiz
RUN addgroup -S spring && adduser -S spring -G spring

# curl - healthcheck uchun kerak
RUN apk add --no-cache curl

WORKDIR /app

# Spring Boot qatlamlarini ko'chiramiz
COPY --from=build /workspace/target/extracted/dependencies/ ./
COPY --from=build /workspace/target/extracted/spring-boot-loader/ ./
COPY --from=build /workspace/target/extracted/snapshot-dependencies/ ./
COPY --from=build /workspace/target/extracted/application/ ./

USER spring:spring

EXPOSE 8080

ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]