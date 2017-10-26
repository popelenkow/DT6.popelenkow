(ns clojure3.core-test
  (:require [clojure.test :refer :all]
            [clojure3.core :refer :all]))

(deftest a-test
  (testing "FIXME, I fail."
    (is
      (>
        (* 7 Step Step)
        (Math/abs
          (-
            (* 7. 7. 1/2)
            ((IntegrateFun (fn [x] x)) 7)))))))

(deftest b-test
  (testing "FIXME, I fail."
    (is
      (>
        (* 1000.1 Step Step)
        (Math/abs
          (-
            (* 1000.1 1000.1 1000.1 1/3)
            ((IntegrateFun (fn [x] (* x x))) 1000.1)))))))

(deftest c-test
  (testing "FIXME, I fail."
    (is
      (>
        (* 0.001 Step Step)
        (Math/abs
          (-
            (* 0.001 0.001 1/2)
            ((IntegrateFun (fn [x] x)) 0.001)))))))