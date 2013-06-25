(ns rand.geohash.util
  "Utilities for the utilities."
  (:require [clojure.java.io :as io])
  (:import (org.joda.time LocalDate)))

(def earliest-djia (LocalDate. 1928 10 01))

(defn read-all
  "Produce a lazy seq of forms from a Reader. (Remember to keep the reader
open while realizing the seq!) Must end with complete form (unless empty.)"
  [^java.io.Reader reader]
  (let [reader (java.io.PushbackReader. reader)
        sentinal (Object.)]
    (take-while #(not (identical? % sentinal))
                (repeatedly #(read reader false sentinal)))))

(defn prn-date
  "Date to string."
  [^LocalDate date]
  (.toString date "YYYY-MM-dd"))

(defn read-date
  "String to date"
  [s]
  (LocalDate. s))
