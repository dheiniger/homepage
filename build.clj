(ns build
  (:refer-clojure :exclude [test])
  (:require [clojure.tools.build.api :as b]
            [clojure.java.shell :refer [sh]]))

(def lib 'homepage)
(def class-dir "target/classes")
(def target-dir "target")
(def uber-file (format "%s/%s-standalone.jar" target-dir (name lib)))

(defn uber [_]
  (prn "building uber")
  (let [basis (b/create-basis {:project "deps.edn"})]
    (b/delete {:path "target"})
    (sh "clj" "-M" "-m" "cljs.main" "--optimizations" "advanced" "--output-dir" "resources/public/wordle/out" "-c" "homepage.wordle")
    (b/copy-dir {:src-dirs   ["src" "resources"]
                 :target-dir class-dir})
    (b/compile-clj {:basis      basis
                    :ns-compile '[homepage.main]
                    :class-dir  class-dir})

    (b/uber {:class-dir class-dir
             :uber-file uber-file
             :basis basis
             :main 'homepage.main})))
