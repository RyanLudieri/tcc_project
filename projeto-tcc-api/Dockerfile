# ---------- build stage ----------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY . .
RUN chmod +x mvnw

# debug rápido: confirma que o jar veio junto no build context
RUN ls -la libs && test -f libs/simulation-manager-1.0-SNAPSHOT.jar

# instala a dependência local no Maven do container
RUN ./mvnw -DskipTests install:install-file \
  -Dfile=libs/simulation-manager-1.0-SNAPSHOT.jar \
  -DgroupId=simula \
  -DartifactId=simulation-manager \
  -Dversion=1.0-SNAPSHOT \
  -Dpackaging=jar

# compila o projeto
RUN ./mvnw -DskipTests package

# ---------- runtime stage ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
