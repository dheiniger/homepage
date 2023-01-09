(ns homepage.core
  (:require [homepage.server :as server :refer [start-server]]
            [compojure.route :as route :refer [resources not-found]]
            [compojure.core :refer [defroutes GET]]
            [hiccup.core :refer :all]
            [hiccup.page :as p :refer [html5 include-css]]))

(defn navigation []
  [:nav.right
   [:a#active {:href "/"} "Home"]
   [:a {:href "/projects"} "Projects"]
   [:a {:href "/contact"} "Contact"]])

(defn header []
  [:header
   (navigation)
   [:h1 "Daniel Heiniger"]
   [:h3.subheading "Software Engineer"]])

(defn footer[]
  [:footer
   [:div.bottom-nav
    [:nav.left
     [:a {:href "https://github.com/dheiniger"} [:img {:src "/images/github-mark-white.svg" :height "25" :alt "Github"}] "Github"]
     [:a {:href "https://www.linkedin.com/in/daniel-heiniger-01507113b"} [:img {:src "/images/linked-in.png" :height "25" :alt "Linked-in"}] "LinkedIn"]]]
   [:div.bottom-disclaimer [:a {:href ""} "Created by Daniel Heiniger"]]])

(defn page [& content]
  (p/html5 [:head
            [:title "Daniel Heiniger"]
            [:link {:rel "icon" :type "image/x-icon" :href "/images/favicon.svg"}]]
           (include-css "/style/main.css")
           [:body
            (header)
            [:section.content content]
            (footer )]))

(defn home-page []
  (page [:p "I'm a Software Engineer based in Des Moines Iowa.  I have experience designing and implementing web apps and prefer programming in Clojure/Clojurescript."]))

(defroutes routes
  (GET "/" [] (home-page))
  (route/resources "/")
  (route/not-found "<h1>Page not found</h1>"))

;(start-server #'routes)
;(home-page "test")
