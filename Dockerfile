# syntax = docker/dockerfile:1.2
FROM clojure:openjdk-19 AS build

RUN clj -T:build uber
WORKDIR /app
COPY . /app

EXPOSE $PORT

RUN ls
RUN ls resources
RUN ls resources/public
RUN ls resources/public/wordle
RUN ls target

ENTRYPOINT exec java $JAVA_OPTS -jar target/homepage-standalone.jar
