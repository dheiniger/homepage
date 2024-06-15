(ns homepage.core
  (:require [hiccup.core :refer :all]
            [hiccup.page :as p :refer [include-css]]))

(defn nav-item [active-page page-name path]
  (let [class (when (= active-page page-name) "active")]
    [:a {:href path :class class} page-name]))

(defn navigation [active-page]
  [:nav.right
   (nav-item active-page "Blog" "/")
   (nav-item active-page "Projects" "/projects")
   (nav-item active-page "Contact" "/contact")
   [:a.button {:href "/files/resume.pdf" :target "blank"} "Resume"]])

(defn header [page-name]
  [:header
   (navigation page-name)
   [:h1 "Daniel Heiniger"]
   [:h3.subheading "Software Engineer"]])

(defn footer []
  [:footer
   [:div.bottom-nav
    [:nav.left
     [:a {:href "https://github.com/dheiniger"} [:img {:src "/images/github-mark-white.svg" :height "25" :alt "Github"}] "Github"]
     [:a {:href "https://www.linkedin.com/in/daniel-heiniger-01507113b"} [:img {:src "/images/linked-in.png" :height "25" :alt "Linked-in"}] "LinkedIn"]]]
   [:div.bottom-disclaimer [:a {:href "https://github.com/dheiniger/homepage"} "Created by Daniel Heiniger"]]])

(defn page [page-name & content]
  (p/html5 [:head
            [:title "Daniel Heiniger"]
            [:link {:rel "icon" :type "image/x-icon" :href "/images/favicon.png"}]
            [:meta {:name "viewport" :content "width=device-width, initial-scale=0.8"}]]
           (include-css "/style/main.css")
           [:body
            (header page-name)
            [:section.content content]
            (footer)]))

(defn blog-page []
  (page "Blog"

        (comment
          [:h3 "Clojure Books"]
          [:p "I've found that reading books is one of the best ways for me to learn.  With my renewed interest in Clojure, here are some books I've read on the subject, in no particular order:"]
          [:ul
           [:ul "Getting Clojure"]
           [:ul "Clojure for the Brave and True"]
           [:ul "Clojure Applied: From Practice to Practitioner"]
           [:ul "Programming Clojure"]]
          [:em {:style {"font-size" "x-small"}} "Published: 06/11/2024"])
        ))

(defn projects-page []
  (page "Projects"
        (comment [:p "Here are some of my recent side projects.  They are not polished feature-rich projects, most of them are not even finished.  I like to start small projects as a way to get familiar with a tool or language."]
                 [:ol
                  [:li
                   [:p
                    "This " [:a {:href "https://github.com/dheiniger/homepage"} "Website"] ".  The website you're viewing is a very basic, static site that was built using Clojure with very few dependencies.  For informational sites, I prefer to not rely on javascript, so it is written using Clojure and Hiccup."]]
                  [:li
                   [:p
                    [:a {:href "https://github.com/dheiniger/movie-night"} "Movie Night"] " is a proof of concept built with Clojurescript, " [:a {:href "https://reagent-project.github.io/"} "Reagent"] ", and " [:a {:href "https://github.com/day8/re-frame"} "re-frame"] ". It's a simple application that requests movies from an API and chooses one at random.  It was an experiment that I used as a way to learn how Re-frame works. "]]
                  [:li
                   [:p
                    [:a {:href "https://github.com/dheiniger/Redeemer-Mobile"} "Redeemer Mobile"] " was the start of a mobile application using Clojurescript and React Native.  It uses a wrapper library called " [:a {:href "https://github.com/drapanjanas/re-natal"} "re-natal"] " that wraps Reagent and Re-frame."]]])))

(defn contact-page []
  (page "Contact"
        [:p.centered "You can reach me at daniel.r.heiniger@proton.me"]))
