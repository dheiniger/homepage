(ns homepage.blog
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import (java.io File)))

(set! *warn-on-reflection* true)

(def posts-directory "resources/public/posts")
(def posts-file (io/file posts-directory))

(defn- blog-file? [file-name]
  (str/ends-with? file-name ".md"))

(defn- get-post-files []
  (filter blog-file? (file-seq posts-file)))

(defn- get-file-name [^File file]
  (.getName file))

(defn- parse-file-name [filename]
  (-> filename
      (str/replace #".md" "")
      (str/split #"#")))

(defn ->post
  "Returns a tuple consisting of the title, date, and contents of a file, where file is named
  with the convention of post-title#mm-dd-yyyy.md"
  [file]
  (let [file-name (get-file-name file)
        contents (slurp file)
        [title date] (parse-file-name file-name)]
    [title date contents]))

(defn get-all-posts []
  (map ->post (get-post-files)))

(defn get-post [^String title]
  (->> (get-all-posts)
       (filter (comp #(.equalsIgnoreCase title %) first))
       first))

(comment
  (slurp (first (get-post-files)))
  (->post (first (get-post-files)))
  (parse-file-name (get-file-name (first (get-post-files))))
  (filter (comp #(= "Creating a Blog" %) first) (get-all-posts))
  (get-post "Creating a Blog")
  )