(ns homepage.server
  (:require [org.httpkit.server :as s]))

(defonce server (atom nil))

(defn start-server [routes]
  (reset! server  (s/run-server routes {:port 8080})))

(defn stop-server [server]
  (when-not (nil? @server)
    (@server :timeout 100)
    (reset! server nil)))
