(ns rand.dp_test
  (use clojure.test
       rand.dp
       [org.timmc.handy :only (deterministic)]))

(deftest finding
  (is (= (find-nexts "ababacaba" "aba") [\b \c])))

(deftest choice
  (is (= (choose-weighted [1]) 0))
  (with-redefs [rand (deterministic 0 0.0001 0.2 0.5 0.999999)]
    (are [v] (= (choose-weighted [0.2 0.8]) v)
         0 0 1 1 1))
  (testing "Floating-point error: Sub-unity summation"
    (with-redefs [rand (deterministic 0.999)]
      (= (choose-weighted [0.2 0.7]) 1)))
  (testing "Floating-point error: Super-unity summation"
    (with-redefs [rand (deterministic 0.999)]
      (= (choose-weighted [0.2 0.9]) 1))))
