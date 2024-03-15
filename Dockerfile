FROM eclipse-temurin:17

ARG JAR_FILE=/build/libs/offcoupon-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} /offcoupon.jar

ENTRYPOINT ["java","-jar","-Dspring.profiles.active=develop", "/offcoupon.jar"]