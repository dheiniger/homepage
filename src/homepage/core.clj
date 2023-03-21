(ns homepage.core
  (:require [homepage.server :as server :refer [start-server]]
            [compojure.route :as route :refer [resources not-found]]
            [compojure.core :refer [defroutes GET]]
            [hiccup.core :refer :all]
            [hiccup.page :as p :refer [html5 include-css]]))

(defn nav-item [active-page page-name path]
  (let [class (when (= active-page page-name) "active")]
    [:a {:href path :class class} page-name]))

(defn navigation [active-page]
  [:nav.right
   (nav-item active-page "Home" "/")
   (nav-item active-page "Projects" "/projects")
   (nav-item active-page "Contact" "/contact")
   [:a.button {:href "/files/resume.pdf" :target "blank"} "Resume"]])

(defn header [page-name]
  [:header
   (navigation page-name)
   [:h1 "Daniel Heiniger"]
   [:h3.subheading "Software Engineer"]])

(defn footer[]
  [:footer
   [:div.bottom-nav
    [:nav.left
     [:a {:href "https://github.com/dheiniger"} [:img {:src "/images/github-mark-white.svg" :height "25" :alt "Github"}] "Github"]
     [:a {:href "https://www.linkedin.com/in/daniel-heiniger-01507113b"} [:img {:src "/images/linked-in.png" :height "25" :alt "Linked-in"}] "LinkedIn"]]]
   [:div.bottom-disclaimer [:a {:href "https://github.com/dheiniger/homepage"} "Created by Daniel Heiniger"]]])

(defn page [page-name & content]
  (p/html5 [:head
            [:title "Daniel Heiniger"]
            [:link {:rel "icon" :type "image/x-icon" :href "/images/favicon.svg"}]]
           (include-css "/style/main.css")
           [:body
            (header page-name)
            [:section.content content]
            (footer )]))

(defn home-page []
  (page "Home"
        ;;[:p "I'm a Software Engineer based in Des Moines, Iowa.  I have experience designing and implementing web applications and prefer programming in Clojure/Clojurescript."]
        ))

(defn projects-page []
  (page "Projects"
        (comment[:p  "Here are some of my recent side projects.  They are not polished feature-rich projects, most of them are not even finished.  I like to start small projects as a way to get familiar with a tool or language."]
        [:ol
         [:li
          [:p
           "This " [:a {:href "https://github.com/dheiniger/homepage"} "Website"] ".  The website you're viewing is a very basic, static site that was built using Clojure with very few dependencies.  For informational sites, I prefer to not rely on javascript, so it is written using Clojure and Hiccup."]]
         [:li
          [:p
           [:a {:href "https://github.com/dheiniger/movie-night"} "Movie Night"] " is a proof of concept built with Clojurescript, " [:a {:href "https://reagent-project.github.io/"} "Reagent"]", and " [:a {:href "https://github.com/day8/re-frame"} "re-frame"]". It's a simple application that requests movies from an API and chooses one at random.  It was an experiment that I used as a way to learn how Re-frame works. "]]
         [:li
          [:p
           [:a {:href "https://github.com/dheiniger/Redeemer-Mobile"} "Redeemer Mobile"] " was the start of a mobile application using Clojurescript and React Native.  It uses a wrapper library called "[:a {:href "https://github.com/drapanjanas/re-natal"}"re-natal"]" that wraps Reagent and Re-frame."]]])))

(defn contact-page[]
  (page "Contact"
        [:p.centered "You can reach me at daniel.r.heiniger@proton.me"]))

(defroutes routes
  (GET "/" [] (home-page))
  (GET "/projects" [] (projects-page))
  (GET "/contact" [] (contact-page))
  (route/resources "/")
  (route/not-found "<h1>Page not found</h1>"))


(defn -main [& args]
  (start-server #'routes))


;;(start-server #'routes)

                                        
;;(home-page "test")
