# ---------- build stage ----------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY . .
RUN chmod +x mvnw

# instala a dependência local (jar em lib/) no repositório do Maven dentro do container
RUN ./mvnw -DskipTests install:install-file \
  -Dfile=lib/simulation-manager-1.0-SNAPSHOT.jar \
  -DgroupId=simula \
  -DartifactId=simulation-manager \
  -Dversion=1.0-SNAPSHOT \
  -Dpackaging=jar

RUN ./mvnw -DskipTests package

# ---------- runtime stage ----------
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
