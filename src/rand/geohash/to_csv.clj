(ns rand.geohash.to-csv
  "Convert sclj point files into CSV."
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [rand.geohash.util :as u]))

(defn -main
  "Compute CSV file for a points sclj file."
  [in out]
  (let [in (if (= in "-") System/in in)
        out (if (= out "-") System/out out)
        fields [:date :djia :e-or-w :latf :lonf]]
    (with-open [r (io/reader in)]
      (with-open [w (io/writer out :append false)]
        (binding [*out* w]
          (println (str/join \, (map name fields)))
          (doseq [point (u/read-all r)]
            (println (str/join \, ((apply juxt fields) point)))))))))
