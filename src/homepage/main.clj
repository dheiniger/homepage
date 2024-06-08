(ns homepage.main
  (:gen-class)
  (:require [homepage.server :as server]
            [homepage.routes :as r]))

(defn -main [& args]
  (prn "in main")
  (prn "Starting server...")
  (server/start-server #'r/routes))

(comment
  (server/stop-server server/server)
  (-main)
  )