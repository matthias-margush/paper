(ns me.margush.color
  (:require [clojure.string :as str])
  (:refer-clojure :exclude [read]))

(defn- parse-hex
  ""
  [input]
  (let [hex (Integer/parseInt input 16)]
    [(bit-shift-right (bit-and hex 0xFF0000) 16)
     (bit-shift-right (bit-and hex 0x00FF00) 8)
     (bit-and hex 0x0000FF)]))

(defn read
  ""
  [color]
  (cond
    (and (string? color) (str/starts-with? color "#"))
    (parse-hex (subs color 1))

    (string? color)
    (parse-hex color)

    (vector? color)
    color))

(defn hex-format
  ""
  [color]
  (let [[r g b] (read color)]
    (format "%02X%02X%02X" r g b)))

(defn- invert-basic-color
  ""
  [c]
  (if (> c 0x7F)
    (- c 0x80)
    (- 0xFF (- 0x7F c))))

(defn invert
  ""
  [color]
  (let [[r g b] (read color)]
    [(invert-basic-color r)
     (invert-basic-color g)
     (invert-basic-color b)]))


(defn mid-basic-color
  ""
  [c1 c2]
  (let [c1' (min c1 c2)
        c2' (max c1 c2)]
    (int (+ c1' (/ (- c2' c1') 2)))))

(defn mid
  ""
  [color1 color2]
  (let [[r1 g1 b1] (read color1)
        [r2 g2 b2] (read color2)]
    [(mid-basic-color r1 r2)
     (mid-basic-color g1 g2)
     (mid-basic-color b1 b2)]))

