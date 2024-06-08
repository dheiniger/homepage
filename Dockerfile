# syntax = docker/dockerfile:1.2
FROM clojure:openjdk-19 AS build

WORKDIR /app
COPY . /app

RUN clj -T:build uber

EXPOSE $PORT

RUN ls
RUN ls target

ENTRYPOINT exec java $JAVA_OPTS -jar target/homepage-standalone.jar