(ns me.margush.theme-ideas
  (:require [clj-uuid :as uuid]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [me.margush.color :as color]))

(defn map-values [f m]
  (into (empty m) (for [[k v] m] [k (f v)])))

(defn map-keys [f m]
  (into (empty m) (for [[k v] m] [(f k) v])))

(def themes
  "Themes"
  [{:theme       "Paper"
    :description "A paper-inspired theme for jetbrains IDE HIs."
    :dark        false
    :colors      {:highlight     "#ddd9cc"
                  :foreground    "#2a2a2a"
                  :dimForeground "#3a3a3a"
                  :background    "#e8e6e2"
                  :dimBackground "#f8f6f2"
                  :red           "#775555"
                  :green         "#557755"
                  :blue          "#555577"
                  :purple        "#775577"
                  :yellow        "#777755"
                  :grey          "#777777"}}
   {:theme       "CI"
    :description "A CI theme for Jetbrains IDEs."
    :dark        "true"
    :colors      {:highlight     "#30363D"
                  :foreground    "#F6F8FA"
                  :dimForeground "#F6F8FA"
                  :background    "#24292E"
                  :dimBackground "#2D3237"
                  :red           "#F97583"
                  :green         "#85E89D"
                  :blue          "#73E3FF"
                  :purple        "#B392F0"
                  :yellow        "#FFEA7F"
                  :grey          "#959DA5"}}])


(defn ->ed-color
  "The colors in the editor theme file do not start with #."
  [color]
  (->>
    (color/hex-format color)
    (str/lower-case)))

(defn ->ide-color
  "The colors in the IDE theme file start with a # and are upper case."
  [color]
  (->>
    (color/hex-format color)
    (str/upper-case)
    (str "#")))

(defn inverted
  ""
  [[k color] mixed]
  [(str "inverted" (str/capitalize (name k)))
   (if mixed
     (color/invert color)
     color)])

(defn colors
  "Map values in the map to editor theme colors."
  [{:keys [colors mixed] :as theme}]
  (->>
    (into {} (map #(inverted % mixed) colors))
    (merge colors
           {:mid (color/mid (:background colors) (:foreground colors))})))

(defn theme-metadata
  "Get the theme metadata."
  [theme]
  (dissoc theme :colors))

(defn mustachioed
  ""
  [s]
  (str "{{" (name s) "}}"))

(defn colorize
  ""
  [colorizer colors]
  (if colorizer
    (colorizer colors)
    colors))

(defn theme-subs
  "Substitutions for the IDE theme file."
  [{:keys [color-render colorizer]} theme]
  (merge
    (theme-metadata theme)
    (map-values color-render
                (colorize colorizer (colors theme)))))

(defn render-mustached
  ""
  [text subs]
  (reduce
    (fn [text [m s]] (str/replace text (mustachioed (name m)) (str s)))
    text
    subs))

(defn read-template
  ""
  [{:keys [template]}]
  (slurp (io/resource template)))

(defn output-filename
  ""
  [{:keys [output-filename]} theme]
  (render-mustached output-filename theme))

(defn render-theme
  ""
  [template theme]
  (render-mustached
    (read-template template)
    (theme-subs template theme)))

(defn invert-theme
  ""
  [{:keys [theme description dark colors]}]
  {:theme       theme
   :description description
   :dark        (not dark)
   :colors      (map-values color/invert colors)})

(defn invert-colors
  ""
  [{:keys [foreground dimForeground background dimBackground mid] :as colors}]
  (assoc
    (map-values color/invert colors)
    :foreground background
    :background foreground
    :mid mid
    :dimForeground dimBackground
    :dimBackground dimForeground))

(defn templates
  "The template files"
  []
  {:editor {:template        "Theme.xml.mo"
            :output-filename "{{theme}}/resources/{{theme}}.xml"
            :color-render    ->ed-color}
   :ide    {:template        "Theme.theme.json.mo"
            :output-filename "{{theme}}/resources/{{theme}}.theme.json"
            :color-render    ->ide-color}
   :plugin {:template        "META-INF/plugin.xml.mo"
            :output-filename "{{theme}}/resources/META-INF/plugin.xml"
            :color-render    identity}})

(defn dark
  ""
  [{:keys [editor ide plugin] :as template} {:keys [dark] :as theme}]
  (let [theme-name (str (:theme theme) " Dark")
        theme-uuid (uuid/v5 uuid/+namespace-oid+ theme-name)]
    (if dark
      [{:editor (assoc editor :colorizer identity)
        :ide    (assoc ide :colorizer identity)
        :plugin plugin}
       (assoc theme :dark true :theme theme-name :uuid theme-uuid)]
      [{:editor (assoc editor :colorizer invert-colors)
        :ide    (assoc ide :colorizer invert-colors)
        :plugin plugin}
       (assoc theme :dark true :theme theme-name :uuid theme-uuid)])))

(defn dark-mixed
  ""
  [{:keys [editor ide plugin] :as template} {:keys [dark] :as theme}]
  (let [theme-name (str (:theme theme) " Dark Mixed")
        theme-uuid (uuid/v5 uuid/+namespace-oid+ theme-name)]
    (if dark
      [{:editor (assoc editor :colorizer identity)
        :ide    (assoc ide :colorizer invert-colors)
        :plugin plugin}
       (assoc theme :dark false :theme theme-name :uuid theme-uuid :mixed true)]
      [{:editor (assoc editor :colorizer invert-colors)
        :ide    (assoc ide :colorizer identity)
        :plugin plugin}
       (assoc theme :dark false :theme theme-name :uuid theme-uuid :mixed true)])))

(defn light
  ""
  [{:keys [editor ide plugin] :as template} {:keys [dark] :as theme}]
  (let [theme-name (str (:theme theme) " Light")
        theme-uuid (uuid/v5 uuid/+namespace-oid+ theme-name)]
    (if dark
      [{:editor (assoc editor :colorizer invert-colors)
        :ide    (assoc ide :colorizer invert-colors)
        :plugin plugin}
       (assoc theme :dark false :theme theme-name :uuid theme-uuid)]
      [{:editor (assoc editor :colorizer identity)
        :ide    (assoc ide :colorizer identity)
        :plugin plugin}
       (assoc theme :dark false :theme theme-name :uuid theme-uuid)])))

(defn light-mixed
  "hi "
  [{:keys [editor ide plugin] :as template} {:keys [dark] :as theme}]
  (let [theme-name (str (:theme theme) " Light Mixed")
        theme-uuid (uuid/v5 uuid/+namespace-oid+ theme-name)]
    (if dark
      [{:editor (assoc editor :colorizer invert-colors)
        :ide    (assoc ide :colorizer identity)
        :plugin plugin}
       (assoc theme :dark true :theme theme-name :uuid theme-uuid :mixed true)]
      [{:editor (assoc editor :colorizer identity)
        :ide    (assoc ide :colorizer invert-colors)
        :plugin plugin}
       (assoc theme :dark true :theme theme-name :uuid theme-uuid :mixed true)])))

(def variants [dark dark-mixed light light-mixed])

(defn -main []
  (doseq [theme themes]
    (doseq [variant variants]
      (let [[template theme] (variant (templates) theme)]
        (doseq [[_ template] template]
          (let [f (output-filename template theme)]
            (printf "%s\n" f)
            (io/make-parents f)
            (spit f (render-theme template theme))))))))
