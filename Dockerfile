FROM maven:3-amazoncorretto-17 as build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

FROM amazoncorretto:17
COPY --from=build /home/app/target/tg_bot-0.0.1-SNAPSHOT.jar /usr/local/lib/tg_bot.jar
ENTRYPOINT ["java","-jar","/usr/local/lib/tg_bot.jar"]