(ns game-of-life.models.board)

(defrecord Coordinate [x y])

(defrecord Board [width height cells])

(defn rows [w cells]
  (let [head (take w cells)
        tail (nthrest cells w)]
    (if (empty? tail) (list head) (cons head (rows w tail)))))

(defn to-str [board]
  (let [formatted-cells (vec (map (fn [x] (if x "+" ".")) (:cells board)))]
    (clojure.string/join "\n"
      (map (fn [x] (clojure.string/join " " x))
           (rows (:width board) formatted-cells)))))

(defn create-board [width height cells]
  ; Creates a new board. Shouldn't be necessary, but tests can't seem to access
  ; Board constructor directly.
  (Board. width height cells))

(defn create-coordinate [x y]
  (Coordinate. x y))

(defn coordinate-add [coordinate x y]
  (create-coordinate
    (+ x (:x coordinate))
    (+ y (:y coordinate))))

(defn cell-alive? [board coordinate]
  (let [x (:x coordinate)
        y (:y coordinate)]
    (let [pos (+ (* y (:width board)) x)]
      ((:cells board) pos))))

(defn valid-cell? [board coordinate]
  ; The cell has to be within the bounding box.
  (and
    (<= 0 (:x coordinate))
    (< (:x coordinate) (:width board))
    (<= 0 (:y coordinate))
    (< (:y coordinate) (:height board))))

(defn all-coordinates [board]
  (vec
    (for [y (range (:height board))
          x (range (:width board))]
      (create-coordinate x y))))

(defn generate-neighbours [board coordinate]
  (filter
    (fn [x] (valid-cell? board x))
    (for [x (range -1 2)
        y (range -1 2)
        :when (not (and (= x 0) (= y 0)))]
      (coordinate-add coordinate x y))))

(defn count-live-neighbours [board coordinate]
  (count
    (filter (fn [x] x)
      (map (fn [y] (cell-alive? board y))
        (generate-neighbours board coordinate)))))

(defn iterate-cell [board coordinate]
  (let [num-live (count-live-neighbours board coordinate)]
    (if (cell-alive? board coordinate)
      (cond
        (< num-live 2) false
        (> num-live 3) false
        true true)
      (if (= num-live 3) true false))))

(defn iterate-board [board]
  (assoc board :cells
         (vec
           (map (fn [x] (iterate-cell board x)) (all-coordinates board)))))

(defn seed [x]
  (if (< (rand) 0.7) false true))

(defn generate-board [width height]
  (create-board width height
    (vec (map seed (range (* width height))))))

(defn -main [width height]
  (let [width (Integer. width)
        height (Integer. height)]
    (let [board (generate-board width height)]
      (vec (map (fn [x] (println (to-str x) "\n"))
        (take 3 (iterate iterate-board board))))
      nil)))

;(defn generate-raw-neighbour-coordinates [w h [x y]]
;  (for
;    [x2 (range -1 2) y2 (range -1 2)]
;    [(+ x x2) (+ y y2)]))
;
;(defn neighbour-coordinates [w h [x y]]
;  (let [valid? (partial valid-cell? w h)]
;    (filter valid?
;      (generate-raw-neighbour-coordinates w h [x y]))))
;
;(defn live?
;  ([w cells [x y]] (live? cells (to-position w [x y])))
;  ([cells i] (cells i)))
;
;(defn live-neighbour-coordinates [w h cells i]
;  (filter (fn [c] (live? w cells c))
;    (neighbour-coordinates w h
;      (to-coordinate w i))))
;
;(defn num-live-neighbours [w h cells i]
;  (count (live-neighbour-coordinates w h cells i)))
;
;(defn set-live? [w h cells i]
;  ; Whether or not to set cell i to alive
;  (let
;    [num-live (num-live-neighbours w h cells i)]
;    (if (live? cells i)
;      (cond
;        (< num-live 2) false
;        (> num-live 3) false
;        true true)
;      (if (= num-live 3) true false))))
;
;(defn rows [w cells]
;  (let [head (take w cells)
;        tail (nthrest cells w)]
;    (if (empty? tail) (list head) (cons head (rows w tail)))))
;
;(defn seed [x]
;  (if (< (rand) 0.7) false true))
;
;(defn generate-board [width height]
;  (Board. width height
;    (vec (map seed (range (* width height))))))
;
;(defn to-str [w cells]
;  (let [formatted-cells (vec (map (fn [x] (if x "+" ".")) cells))]
;    (clojure.string/join "\n"
;      (map (fn [x] (clojure.string/join " " x))
;           (rows w formatted-cells)))))
;
;(defn next-generation [w h cells]
;  (let [update-cell (partial set-live? w h cells)]
;    (vec (map update-cell (range (* w h))))))
;
;(defn -main [width height]
;  (let [width (Integer. width)
;        height (Integer. height)]
;    (let [board (generate-board width height)
;          next-gen (partial next-generation w h)]
;      (vec (map (fn [x] (println (to-str w x) "\n"))
;        (take 3 (iterate next-gen cells))))
;      nil)))
