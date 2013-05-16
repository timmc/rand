(ns rand.expand
  "See the full macroexpansion path of a form."
  (:require clojure.walk))

(defn strip-ns
  "Strip off those pesky namespaces from all symbols."
  [form]
  (clojure.walk/postwalk #(if (symbol? %) (symbol (name %)) %) form))

(defn expand-once-fn
  []
  (let [expanded? (atom false)]
    (fn [node]
      (if (and (not @expanded?)
               (or (list? node) (seq? node)))
        (let [exp (macroexpand-1 node)]
          (when (not= exp node)
            (reset! expanded? true))
          exp)
        node))))

(defn expand-once
  [form]
  (clojure.walk/prewalk (expand-once-fn) form))

(defmacro expansions
  [n expr]
  `(take ~n (map strip-ns (iterate expand-once ~(list 'quote expr)))))

(comment
  (dorun (map println (expansions 5 (->> a b (->> c d))))))
