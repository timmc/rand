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
  (let [sentinal (Object.)
        data? #(not (identical? % sentinal))
        ;; splitter expects one of:
        ;; A) a collapsed stream starting with a sentinal,
        ;; B) empty seq (() or nil) to indicate there are no more partitions
        splitter (fn splitter
                   ([s] (splitter s false))
                   ;; When emitting a partition, set drop? to true to indicate
                   ;; that before emitting the next one, the last one should
                   ;; be skipped over.
                   ([s drop?]
                      (when (seq s)
                        (lazy-seq
                         (if drop?
                           (splitter (drop-while data? (next s)) false)
                           (cons (take-while data? (next s))
                                 (splitter s true)))))))]
    (splitter (cons sentinal (collapse-subs sub sentinal s)))))

