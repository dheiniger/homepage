(ns homepage.routes
  (:require
    [compojure.core :refer [GET defroutes]]
    [compojure.route :as route]
    [homepage.core :as h]))

(defroutes routes
           (GET "/" [] (h/blog-page))
           (GET "/projects" [] (h/projects-page))
           (GET "/contact" [] (h/contact-page))
           (GET "/blog/:title" request (h/blog (str (:title (:params request)))))
           (route/resources "/")
           (route/not-found "<h1>Page not found</h1>"))
