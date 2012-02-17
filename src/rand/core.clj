(ns rand.core)

(defn prefix?
  [sub s]
  (loop [sub (seq sub), s (seq s)]
    (or (nil? sub)
        (and s
             (= (first sub) (first s))
             (recur (next sub) (next s))))))

(defn collapse-subs
  [match replacement s]
  {:pre [(seq match)]}
  (when-let [s (seq s)]
    (lazy-seq
     (if (prefix? match s)
       (cons replacement
             (collapse-subs match replacement
                            (drop (count match) s)))
       (cons (first s)
             (collapse-subs match replacement
                            (next s)))))))

(defn split-at-subs
  [sub s]
  (let [delimiter (Object.) ;; sentinal between partitions
        data? #(not (identical? % delimiter))
        ;; sentinal indicating the next partition should be dropped
        droppit (Object.)
        ;; splitter expects one of:
        ;; A) a collapsed stream starting with a sentinal,
        ;; B) the empty list to indicate there are no more partitions, or
        ;; C) (cons droppit A-or-B) to indicate that the next partition has
        ;;    been requested (and it is time to walk through and discard the
        ;;    first partition).
        splitter (fn splitter [s]
                   (when (seq s)
                     (lazy-seq
                      (if (identical? droppit (first s))
                        (splitter (drop-while data? (nnext s)))
                        (cons (take-while data? (next s))
                              (splitter (cons droppit s)))))))]
    (splitter (cons delimiter (collapse-subs sub delimiter s)))))

