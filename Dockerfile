FROM clojure:lein

MAINTAINER Daniel Heiniger <daniel.r.heiniger@gmail.com>

COPY . /app

WORKDIR /app

CMD ["lein", "run"]