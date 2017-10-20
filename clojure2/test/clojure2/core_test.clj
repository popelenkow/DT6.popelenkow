(ns clojure2.core-test
  (:require [clojure.test :refer :all]
            [clojure2.core :refer :all]))

(deftest a-test
  (testing "FIXME, I fail."
    (is
      (>
        (* 7 Step Step)
        (Math/abs
          (-
            (Math/sin 7)
            (IntegrateCos 7)))))))

(deftest b-test
  (testing "FIXME, I fail."
    (is
      (>
        (* 1000.1 Step Step)
        (Math/abs
          (-
            (Math/sin 1000.1)
            (IntegrateCos 1000.1)))))))

(deftest c-test
  (testing "FIXME, I fail."
    (is
      (>
        (* 0.001 Step Step)
        (Math/abs
          (-
            (Math/sin 0.001)
            (IntegrateCos 0.001)))))))


