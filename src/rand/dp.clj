(ns rand.dp
  (:require [clojure.string :as str]))

(defn find-nexts
  [^String haystack ^String needle]
  (loop [ret []
         index 0]
    (let [next (.indexOf haystack needle index)
          pick (+ next (.length needle))]
      (if (or (neg? next) (<= (.length haystack) pick))
        ret
        (recur (conj ret (.charAt haystack pick)) (inc next))))))

(defn choose-weighted
  "Random weighted choice from a collection of weights. Returns index."
  [probs]
  {:pre [(seq probs)]}
  ;; http://stackoverflow.com/questions/14464011/idiomatic-clojure-for-picking-between-random-weighted-choices
  (let [threshold (* (rand) (apply + probs))]
    (count (take-while #(<= % threshold) (reductions + probs)))))

(defn dissociate*
  [sources window]
  (lazy-seq
   (let [weighted (for [[source prob] sources
                        :let [candidates (find-nexts source window)
                              cnt (count candidates)
                              cprob (if (zero? cnt) 0 (/ prob cnt))]
                        chr candidates]
                    [chr cprob])]
     (when (seq weighted)
       (let [index (choose-weighted (map second weighted))
             picked (first (nth weighted index))]
         (cons picked
               (dissociate* sources (str (.substring window 1) picked))))))))

(defn dissociate
  "Take a map of strings to probabilities and produce a lazy seq
of characters.

Does not work as expected with non-BMP characters."
  [sources window-size]
  (when (seq sources)
    (let [from (ffirst sources)
          init (.substring from 0 (min window-size (.length from)))]
      (concat init (dissociate* sources init)))))

(defn -main
  [window-size limit & sources]
  (let [window-size (Long/parseLong window-size)
        limit (Long/parseLong limit)
        sources (for [[f p] (partition 2 sources)]
                  [(slurp f) (Double/parseDouble p)])]
    (println (apply str (take limit (dissociate sources window-size))))))
