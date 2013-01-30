(ns rand.core)

;;;; Splitting

(defn prefix?
  [sub s]
  (loop [sub (seq sub), s (seq s)]
    (or (empty? sub)
        (and s
             (= (first sub) (first s))
             (recur (rest sub) (rest s))))))

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
                            (rest s)))))))

(defn split-at-subs
  "Split coll `s` at each appearance of `sub`. Result and subsequences are lazy
with at most `(inc (count sub))` lookahead."
  [sub s]
  (let [sentinel (Object.)
        data? #(not (identical? % sentinel))
        ;; splitter expects one of:
        ;; A) a collapsed stream starting with a sentinel,
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
                           (splitter (drop-while data? (rest s)) false)
                           (cons (take-while data? (rest s))
                                 (splitter s true)))))))]
    (splitter (cons sentinel (collapse-subs sub sentinel s)))))

