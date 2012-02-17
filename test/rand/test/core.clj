(ns rand.test.core
  (:use [rand.core])
  (:use [clojure.test]))

(deftest prefix
  (is (prefix? [] []))
  (is (prefix? [] [1 2 3]))
  (is (not (prefix? [1 2] [])))
  (is (prefix? [1 2] [1 2]))
  (is (not (prefix? [1 2 3] [1 2 4])))
  (is (prefix? [0 1 2] (range)))
  (is (not (prefix? [0 1 4] (range)))))

(deftest collapse
  (is (= (collapse-subs [1 2] :a nil) nil))
  (is (= (collapse-subs [1 2] :a []) nil))
  (is (thrown? Throwable (collapse-subs [] :a [1 2 3])))
  (is (= (collapse-subs [:a :a] :a [1 :a :a :a]) [1 :a :a]))
  (is (= (collapse-subs [1 2] :a [1 2 0 1 2 1 2 3 1 2]) [:a 0 :a :a 3 :a]))
  (is (= (take 5 (collapse-subs [1 2] :a (range))) [0 :a 3 4 5])))

(deftest splitting
  (is (= (split-at-subs [1 2] [0 1 2 3]) [[0] [3]]))
  (is (= (split-at-subs [1 2] [1 2 0 1 2 1 2 3 1 2]) [[] [0] [] [3] []]))
  (is (= (first (split-at-subs [3 4] (range))) [0 1 2]))
  (is (= (take 5 (second (split-at-subs [3 4] (range)))) [5 6 7 8 9])))
