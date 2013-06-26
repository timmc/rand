(ns rand.geohash.slurp-djia
  "Slurp DJIA data from crox's server."
  (:require [clojure.java.io :as io]
            [rand.geohash.util :as u])
  (:import (org.joda.time LocalDate)))

(defn djia-url
  [^LocalDate when]
  (format "http://geo.crox.net/djia/%04d/%02d/%02d"
          (.getYear when) (.getMonthOfYear when) (.getDayOfMonth when)))

(defn fetch-djia
  [^LocalDate on]
  (slurp (djia-url on)))

(defn djia-seq
  [^LocalDate start-date]
  (lazy-seq
   (try (let [val (fetch-djia start-date)]
          (cons [(u/prn-date start-date) val]
                (djia-seq (.plusDays start-date 1))))
        (catch java.io.IOException ioe
          []))))

(defn slow-seq
  "Make a blocking seq out of another seq. Take millis ms to take rest."
  [sequence millis]
  (let [locked? (atom false)
        produce (fn produce [s]
                  (if (empty? s)
                    []
                    (cons (first s)
                          ;; no catch; let interruptions bubble
                          (lazy-seq (do (Thread/sleep millis)
                                        (produce (rest s)))))))]
    (produce sequence)))

(defn last-persisted-date
  [f]
  (let [last-form (when (.exists f)
                    (with-open [r (io/reader f)]
                      (last (u/read-all r))))]
    (if (nil? last-form)
      u/earliest-djia
      (u/read-date (first last-form)))))

(def ms-between-reqs 500)

(defn dump-to-disk
  "Read all historical DJIA values to disk."
  [file-path]
  (let [f (io/file file-path)
        start-date (.plusDays (last-persisted-date f) 1)
        slow (slow-seq (djia-seq start-date) ms-between-reqs)]
    (with-open [w (io/writer f :append true)]
      (binding [*out* w]
        (doseq [s slow]
          (prn s))))))

(defn -main
  "Read historical DJIA data from crox's server to file specified by
first argument. Action is interruptible and will automatically resume
where it left off when restarted. File contains one top-level form for
each date, as [\"YYYY-MM-dd\" \"123.45\"].

Example invocation:

`$ lein run -m rand.geohash.slurp-djia cached/djia.sclj`

Example output:

`[\"1928-10-01\" \"239.43\"]
[\"1928-10-02\" \"240.01\"]
[\"1928-10-03\" \"238.14\"]
...`"
  [& [file-path]]
  (dump-to-disk file-path))
