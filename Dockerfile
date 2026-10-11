# ===================================================================
# Stage 1: Build & Package the Application
# ===================================================================
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Cache Maven dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy project source code
COPY src ./src

# Compile application and package classes & WAR
RUN mvn clean package -DskipTests

# Copy runtime dependencies into target/dependency
RUN mvn dependency:copy-dependencies -DincludeScope=runtime -DoutputDirectory=target/dependency

# ===================================================================
# Stage 2: Minimal Production Runtime
# ===================================================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Install curl for Render health checking
RUN apk add --no-cache curl

# Copy runtime artifacts from build stage
COPY --from=builder /app/target/classes ./target/classes
COPY --from=builder /app/target/dependency ./target/dependency
COPY --from=builder /app/src/main/webapp ./src/main/webapp
COPY --from=builder /app/target/reshmamart ./target/reshmamart

# Create persistent storage folder for H2 database
RUN mkdir -p /app/data

# Render dynamically sets $PORT (default to 8080)
ENV PORT=8080
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:${PORT}/api/v1/health || exit 1

# Launch the Embedded Tomcat Server
CMD ["java", "-cp", "target/classes:target/dependency/*", "com.reshmamart.runner.EmbeddedTomcatServer"]
