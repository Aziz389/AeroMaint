# ---------- Stage 1: build the jar with Maven ----------
# This image has Maven + a full JDK. It is only used to compile; it is not shipped.
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml first and download dependencies: Docker caches this layer,
# so dependencies are only downloaded again when pom.xml changes.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q package -DskipTests

# ---------- Stage 2: run the jar with a small JRE image ----------
# Only the Java runtime and our jar: no Maven, no source code, no JDK. Smaller and safer.
FROM eclipse-temurin:17-jre
WORKDIR /app

# Run as a normal user instead of root.
RUN useradd --system --uid 1001 aeromaint
USER aeromaint

COPY --from=build /app/target/aeromaint-backend-*.jar app.jar

EXPOSE 8080

# Use up to 75% of the container's memory limit for the Java heap.
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
