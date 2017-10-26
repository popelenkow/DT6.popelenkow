(ns clojure4.core)

(defn constant  [val]
  {:pre [(keyword? name)]}
  (list ::var name))

(defn variable [name]
  {:pre [(keyword? name)]}
  (list ::var name))

(defn variable? [expr]
  (=
    (first expr)
    ::var))

(defn variable-name [v]
  (second v))

(defn same-variables? [v1 v2]
  (and
    (variable? v1)
    (variable? v2)
    (=
      (variable-name v1)
      (variable-name v2))))


(defn negation [expr]
  (list ::negation expr))

(defn negation? [expr]
  (= ::negation (first expr)))

(defn implication [expr1, expr2]
  (list ::implication expr1 expr2))

(defn implication? [expr]
  (= ::implication (first expr)))

(defn conjunction [expr & rest]
  (cons ::conjunction (cons expr rest)))

(defn conjunction? [expr]
  (= ::conjunction (first expr)))

(defn disjunction [expr & rest]
  (cons ::disjunction (cons expr rest)))

(defn disjunction? [expr]
  (= ::disjunction (first expr)))






(defn -main [& args]
  (println
    (negation (negation (disjunction (variable :x) (variable :x))))))
  
  
  