FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-25

WORKDIR /app

ENV TZ="Europe/Oslo"

COPY build/libs/pensjon-opptjening-hendelse-api.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]