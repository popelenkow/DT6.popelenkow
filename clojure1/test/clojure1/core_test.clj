(ns clojure1.core-test
  (:require [clojure.test :refer :all]
            [clojure1.core :refer :all]))

(deftest a-test
  (testing "FIXME, I fail."
    (is (=
          (superMultiply 4 ["a" "b" "c"])
          (list
           "abab"
					 "abac"
					 "abca"
					 "abcb"
					 "acab"
					 "acac"
					 "acba"
					 "acbc"
					 "baba"
					 "babc"
					 "baca"
					 "bacb"
					 "bcab"
					 "bcac"
					 "bcba"
					 "bcbc"
					 "caba"
					 "cabc"
					 "caca"
					 "cacb"
					 "cbab"
					 "cbac"
					 "cbca"
					 "cbcb")))))

(deftest b-test
  (testing "FIXME, I fail."
    (is (=
          (superMultiply 1 ["3" "1" "2"])
          (list
           "3"
					 "1"
					 "2")))))
