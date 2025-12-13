# ---------- build stage ----------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY . .
RUN chmod +x mvnw

# Install local JARs (NOT in Maven Central)
RUN ./mvnw -DskipTests install:install-file \
  -Dfile=libs/simulation-manager-1.0-SNAPSHOT.jar \
  -DgroupId=simula \
  -DartifactId=simulation-manager \
  -Dversion=1.0-SNAPSHOT \
  -Dpackaging=jar

RUN ./mvnw -DskipTests install:install-file \
  -Dfile=libs/org.eclipse.epf.uma_1.5.0.201805171822.jar \
  -DgroupId=org.eclipse.epf \
  -DartifactId=uma \
  -Dversion=1.5.0 \
  -Dpackaging=jar

RUN ./mvnw -DskipTests install:install-file \
  -Dfile=libs/org.eclipse.epf.uma.ecore_1.5.0.201805171822.jar \
  -DgroupId=org.eclipse.epf \
  -DartifactId=uma.ecore \
  -Dversion=1.5.0 \
  -Dpackaging=jar

RUN ./mvnw -DskipTests install:install-file \
  -Dfile=libs/org.eclipse.epf.uma.edit_1.5.0.201805171822.jar \
  -DgroupId=org.eclipse.epf \
  -DartifactId=uma.edit \
  -Dversion=1.5.0 \
  -Dpackaging=jar

# Build app
RUN ./mvnw -DskipTests package


# ---------- runtime stage ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
