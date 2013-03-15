(defn rand-printable []
  (let [cmin (int \!)
        cmax (int \~)]
    (char (+ cmin (rand-int (inc (- cmax cmin)))))))

(comment (println (apply str (repeatedly 100 rand-printable))))
