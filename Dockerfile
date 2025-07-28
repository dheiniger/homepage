# syntax = docker/dockerfile:1.2
FROM clojure:openjdk-19 AS build

WORKDIR /app
RUN clj -T:build uber
COPY . /app



EXPOSE $PORT

RUN ls
RUN ls resources
RUN ls resources/public
RUN ls resources/public/wordle
RUN ls resources/public/wordle/out
RUN ls target
RUN ls target/public
RUN ls target/public/wordle
RUN ls target/public/wordle/out

ENTRYPOINT exec java $JAVA_OPTS -jar target/homepage-standalone.jar
