(ns game-of-life.views.board
  (:require [noir.content.getting-started]
            [game-of-life.models.board :as board])
  (:use [noir.core :only [defpage defpartial]]
        [noir.response :only [json]]
        [hiccup.page :only [include-css html5 include-js]]))

(defpartial layout [& content]
  (html5
    [:head
     [:title "Game of Life"]
     (include-css "/css/reset.css")
     (include-js "https://ajax.googleapis.com/ajax/libs/jquery/1.9.0/jquery.min.js")
     (include-js "/js/life.js")]
    [:body
     [:table#wrapper
      content]]))

(defn bool-to-bit [bool]
  (if bool \1 \0))

(defn bit-to-bool [bit]
  (case bit
    \1 true
    \0 false))

(defn cells-to-bitstr [cells]
  (apply str (map bool-to-bit cells)))

(defn bitstr-to-cells [cell-str]
  (vec (map bit-to-bool cell-str)))

(defpage "/" []
  (layout))

(defpage [:post "/board"] {:keys [width height cells]}
         (let [cells (bitstr-to-cells cells)
               width (Integer. width)
               height (Integer. height)]
           (let [board (board/iterate-board (board/create-board width height cells))]
             (json {:width width
                    :height height
                    :cells (cells-to-bitstr (:cells board))}))))
