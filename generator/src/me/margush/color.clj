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
