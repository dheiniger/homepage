(ns homepage.wordle
  (:require [replicant.dom :as r]
            [clojure.string :as string]
            [cljs.core.async :refer [go <!]]
            [cljs.core.async.interop :refer-macros [<p!]]
            [clojure.edn :as edn]
            [clojure.string :as str]))

(defn- =ignore-case [& strs]
  (apply = (map string/lower-case strs)))

(defn update-box! [store [x y] m]
  (swap! store update-in [:board y x] merge m))

(defn current-pos [store]
  (let [{:keys[board]} @store
        complete-rows (take-while #(every? (comp (partial not= :unanswered) :status) %) board)
        row-num  (count complete-rows)
        col-num (count (take-while :letter (get board row-num)))]
    [col-num row-num]))

(defn dimensions [game-state]
  (let [{:keys[board]} @game-state
        height (count board)
        width (count (first board))]
    [width height]))

(defn add-letter [game-state k]
  (let [[width height] (dimensions game-state)
        [x y :as pos] (current-pos game-state)]
    (when (< x width)
      (update-box! game-state pos {:letter (string/upper-case k)}))))

(defn remove-letter [game-state]
  (let [[width] (dimensions game-state)
        [x y] (current-pos game-state)]
    (when (pos? x)
      (update-box! game-state [(dec x) y] {:letter nil}))))

(defn letter-status [word guess]
  (let [guess (string/upper-case guess)
        word (string/upper-case word)
        status-fn (fn[x y]
                    (cond (= x y) :correct
                          (contains? (set word) x) :close
                          :else :incorrect))]
    (map (juxt first status-fn) guess word)))

(defn capitalize [letter]
  (when letter
    (string/upper-case letter)))

(defn update-game-status! [game-state guess]
  (let [{:keys[board word]} @game-state
        [_ height] (dimensions game-state)
        [x y] (current-pos game-state)]
    (cond-> game-state
      (= height (inc y)) (swap! assoc :status :lost)
      (=ignore-case guess word) (swap! assoc :status :won)
      :always (do
                (dorun
                 (map-indexed
                  (fn[x [_ status]]
                    (update-box! game-state [x y] {:status status}))
                  (letter-status word guess)))
                (swap! game-state update :guesses conj guess)))))

(defn reveal [game-state]
  (let [{:keys[word board words guesses]} @game-state
        [width] (dimensions game-state)
        [x y] (current-pos game-state)
        row (nth board y)
        guess (apply str (map (comp capitalize :letter) row))]
    (when (= width x)
      (cond
        (not (contains? (set words) (string/lower-case guess)))
        (swap! game-state assoc :status :invalid-guess)

        (contains? guesses guess)
        (swap! game-state assoc :status :already-guessed)

        :else (update-game-status! game-state guess)))))

(defn handle-keydown[game-state e]
  (let [{:keys[status]} @game-state
        k (.-key e)
        valid-statuses #{:running :not-started :invalid-guess :already-guessed}]
    (when (contains? valid-statuses status)
      (cond
        (re-matches #"[a-zA-Z]" k) (add-letter game-state k)
        (or (= "Backspace" k) (= "<<" k))(remove-letter game-state)
        (=ignore-case "Enter" k) (reveal game-state)))))

(defn status->disp-text [status word]
  (case status
    :won "You Won!"
    :lost word
    :invalid-guess "Not in word list!"
    :already-guessed "Word already guessed!"
    nil))

(defn guess-stat-groups [letter-stats]
  (reduce (fn [acc [k v]]
            (merge-with concat acc {k [v]}))
          {} letter-stats))

(defn letter-stat [letter-stats letter]
  (let [letter-group (set (get letter-stats letter))
        stat (cond-> {:stat nil}
               (contains? letter-group :incorrect) (assoc :stat :incorrect)
               (contains? letter-group :close) (assoc :stat :close)
               (contains? letter-group :correct) (assoc :stat :correct))]
    {letter (:stat stat)}))

(defn keyboard [guessed-words word]
  (let [ks [["Q" "W" "E" "R" "T" "Y" "U" "I" "O" "P"]
            ["A" "S" "D" "F" "G" "H" "J" "K" "L"]
            ["ENTER" "Z" "X" "C" "V" "B" "N" "M" "<<"]]
        guessed-letters (apply (comp set str) guessed-words)
        letter-groups (->> (mapcat (partial letter-status word) guessed-words)
                           guess-stat-groups)
        letter-statuses (apply merge (map (partial letter-stat letter-groups) guessed-letters))]
    (for [row ks]
      [:div.btn-row
       (map (fn[k] (let [stat (get letter-statuses (string/upper-case k) :default)]
                     [:button.letter {:on {:click [:key-clicked k]} :id (str "letter-" k) :class stat} k])) row)])))

(defn draw [store]
  (let [{:keys[board status word guesses]} @store
        [width height] (dimensions store)
        [x y] (current-pos store)]
    [:div
     [:div.board
      (when-let[text (status->disp-text status word)]
        [:div.centered.fade-out.alert {:on {:animationend [:reset-status store]}} text])
      (map (fn [row]
             [:div
              (map (fn[{:keys[letter status]}]
                     [:div.box {:class status} letter]) row)])
           board)]
     (keyboard guesses word)
     (when (contains? #{:won :lost} status)
       [:div
        [:div[:button.button {:on {:click [:new-game store]}} "Play Again!"]]])]))

(defn- word-list []
  (go (let [response (<p! (js/fetch "/wordle/words" #_"public/wordle/words.edn"))
            words (<p! (.text response))]
        (edn/read-string words))))

(defn init-state [w h words]
    {:status :not-started
     :guesses #{}
     :board (vec (repeat h (vec (repeat w {:letter nil :status :unanswered}))))
     :words words
     :word (rand-nth words)})

(defn main [w h]
  (go (let [words (<! (word-list))
            store (atom (init-state w h words))
            el (js/document.getElementById "app")]
        (.addEventListener js/window "keydown" (partial handle-keydown store))
        (r/set-dispatch!
         (fn [event-data [action state]]
           (.preventDefault (:replicant/dom-event event-data))
           (case action
             :key-clicked (handle-keydown store (clj->js {:key state}))
             :new-game (reset! state (init-state w h words))
             :reset-status (when-not (contains? #{:won :lost} (:status @state))
                             (swap! state assoc :status :running)))))

        (add-watch store ::render
                   (fn [_ _ _ game]
                     (r/render el (draw store))))
        (swap! store assoc :status :running))))

;;start game
(main 5 6)

(comment
  [:div (mapv (fn [row]
                [:div (mapv (fn[box][:div.box (:letter box)]) row)])
              board)]
  (draw store)
  (dimensions store)
  (update-box! store 0 0 {:letter "x"})
  (update-box! store 1 0 {:letter "x"})
  (update-box! store 2 0 {:letter "x"})
  (update-box! store 3 0 {:letter "x"})
  (update-box! store 4 0 {:letter "x"})
  (update-box! store 0 1 {:letter "x"})
  (current-pos store)

  (or (take-while #(every? :letter %) (:board store)) 0);;num rows
  ;;

  (key-stat (group-by first ls))
  (into {} ls)
  (apply merge letter-statuses)

  (letter-stat ls "A")

  (cond-> {:stat nil}
    (contains? (set (get ls "A")) :incorrect) (assoc :stat :incorrect)
    (contains? (set (get ls "A")) :close) (assoc :stat :close)
    (contains? (set (get ls "A")) :correct) (assoc :stat :correct)
    )
  (#(sorted-set :correct :incorrect :close) [:incorrect :close :incorrect :incorrect])


  )
