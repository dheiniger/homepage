(ns homepage.routes
  (:require
    [compojure.core :refer [GET defroutes]]
    [compojure.route :as route]
    [homepage.core :as h]))

(defroutes routes
           (GET "/" [] (h/home-page))
           (GET "/projects" [] (h/projects-page))
           (GET "/contact" [] (h/contact-page))
           (route/resources "/")
           (route/not-found "<h1>Page not found</h1>"))
