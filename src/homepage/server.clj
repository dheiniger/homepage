(ns homepage.server
  (:require [org.httpkit.server :as s]))

(defonce server (atom nil))

(defn app [request]
  {:status 200
   :headers {"Content-type" "text/html"}
   :body "<h1>body </h1>"})

(defn start-server [routes]
  (reset! server  (s/run-server routes {:port 8080})))

(defn stop-server [server]
  (when-not (nil? @server)
    (@server :timeout 100)
    (reset! server nil)))


;(start-server)

;(stop-server server)


