(ns rand.geohash.points
  "Calculate points from DJIA dates."
  (:require [clojure.java.io :as io]
            [org.timmc.geohash :as gh]
            [rand.geohash.util :as u])
  (:import (org.joda.time LocalDate)))

(defn -main
  "Compute fractional hashes for all historical east or west graticules. Args:

- `in`: Read slurp-djia output from this file (or - for stdin)
- `e-or-w`: Compute hashpoints for east-of-30W (`east`) or west-of-30W (`west`)
- `out`: Write maps of :date. :djia, :e-or-w, :latf, :lonf

Example invocation:

`$ lein run -m rand.geohash.points cached/djia.sclj east cached/points-east.sclj`

Example output:

    {:date \"1928-10-01\", :djia \"239.43\", :e-or-w \"east\", :latf \"0.267062\", :lonf \"0.767406\"}
    {:date \"1928-10-02\", :djia \"240.01\", :e-or-w \"east\", :latf \"0.066001\", :lonf \"0.178189\"}
    {:date \"1928-10-03\", :djia \"238.14\", :e-or-w \"east\", :latf \"0.445791\", :lonf \"0.269008\"}`"
  [in e-or-w out]
  (let [in (if (= in "-") System/in in)
        out (if (= out "-") System/out out)
        djia-dates (with-open [r (io/reader in)]
                     (doall (u/read-all r)))
        [lat lon] (case e-or-w
                    "east" [42.0 +71.0]
                    "west" [42.0 -71.0])
        dow-by-date (into {} (for [[date djia] djia-dates]
                               [(u/read-date date) djia]))
        today (org.joda.time.LocalDate.)
        hash-dates (iterate #(.plusDays % 1) u/earliest-djia)]
    ;; There is a DJIA for every date (both east and west) from the earliest
    ;; DJIA opening to the most recent closing. Because the 30W rule started
    ;; recently, old dates in West graticules don't use the previous day's
    ;; DJIA.
    (with-open [w (io/writer out :append false)]
      (binding [*out* w]
        (doseq [then hash-dates
                :let [djia (dow-by-date (gh/dow-date lat lon then))]
                :while djia ;; Today might not have a DJIA yet
                :let [[latf lonf] (gh/fractional then djia)]]
          (prn {:date (u/prn-date then)
                :djia djia
                :e-or-w e-or-w
                :latf (format "%08f" latf)
                :lonf (format "%08f" lonf)}))))))
