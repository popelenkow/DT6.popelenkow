(ns clojure4.core)

(defn boolean? [x]
  (instance? Boolean x))

(defn third [x]
  (second (next x)))

; Constructor
(defn constant  [val]
  {:pre [(boolean? val)]}
  (list ::constant val))

(defn variable [val]
  {:pre [(keyword? val)]}
  (list ::variable val))

(defn element [val]
  {:pre [(or (boolean? val) (keyword? val))]}
  (if (boolean? val)
    (constant val)
    (variable val)))

; Check type
(defn constant? [expr]
  (= ::constant (first expr)))

(defn variable? [expr]
  (= ::variable (first expr)))

(defn element? [expr]
  (or
    (constant? expr)
    (variable? expr)))

; Get value
(defn constant-val [el]
  {:pre [(constant? el)]}
  (second el))

(defn variable-val [el]
  {:pre [(variable? el)]}
  (second el))

(defn element-val [el]
  {:pre [(element? el)]}
  (second el))

; Same elements
(defn same-constants? [el1 el2]
  (and
    (constant? el1)
    (constant? el2)
    (=
      (constant-val el1)
      (constant-val el2))))

(defn same-variables? [el1 el2]
  (and
    (variable? el1)
    (variable? el2)
    (=
      (variable-val el1)
      (variable-val el2))))

(defn same-elements? [el1 el2]
  (and
    (element? el1)
    (=
      (first el1)
      (first el2))
    (=
      (element-val el1)
      (element-val el2))))

; Operators
(defn- dispatch-type [expr]
  (first expr))

(defmulti operator? dispatch-type)

(defmethod operator? :default [expr]
  false)

(defn operator-val [op]
  {:pre [(operator? op)]}
  (first op))

(defn same-operators? [op1 op2]
  (and
    (operator? op1)
    (operator? op2)
    (=
      (operator-val op1)
      (operator-val op2))))

(defn operator-args [expr]
  {:pre [(operator? expr)]}
  (rest expr))

(defmulti operator-calc dispatch-type)

; Expressions
(defn expression? [expr]
  (or
    (element? expr)
    (operator? expr)))

(defn expression-calc [expr]
  {:pre [(expression? expr)]}
  (if (operator? expr)
    (let [args (operator-args expr)]
      (operator-calc (concat
	                     (operator-val expr)
	                     (expression-calc (first args))
	                     (rest args))))
    expr))

(defn expression-calc [expr]
  {:pre [(expression? expr)]}
  (if (operator? expr)
    (operator-calc (cons
                     (operator-val expr)
                     (map expression-calc (operator-args expr))))
    expr))

; Negation
(defn negation [expr]
  (list ::negation expr))

(defn negation? [expr]
  (= ::negation (first expr)))

(defmethod operator? ::negation [expr]
  true)

(defmethod operator-calc ::negation [expr-full]
  (let [expr (second expr-full)]
    (if (constant? expr)
     (constant (not (constant-val expr)))
     expr-full)))


; Implication
(defn implication [expr1, expr2]
  (list ::implication expr1 expr2))

(defn implication? [expr]
  (= ::implication (first expr)))

(defmethod operator? ::implication [expr]
  true)


(defmethod operator-calc ::implication [expr-full]
  (let [expr1 (second expr-full)
        expr2 (third expr-full)]
    (if (same-elements? expr1 expr2)
      true
      (if (constant? expr1)
        (if (constant-val expr1)
          expr2
          true)
        (if (constant? expr2)
          (if (constant-val expr2)
            true
            (negation expr1))
          expr-full)))))


; Conjunction
(defn conjunction [expr & rest]
  (cons ::conjunction (cons expr rest)))

(defn conjunction? [expr]
  (= ::conjunction (first expr)))

(defmethod operator? ::conjunction [expr]
  true)


(defn- collapse-conjunction-constants [exprs]
  (let [consts (filter constant? exprs)
        other-exprs (remove constant? exprs)
        combined-const
        (reduce
          (fn [acc entry]
            (and acc
               (constant-val entry)))
          true consts)]
    (cond
      (= combined-const false)
      (list (constant false))
      :else
      (if (empty? other-exprs)
        (list (constant true))
        other-exprs))))

(defmethod operator-calc ::conjunction [expr-full]
  (let [args (operator-args expr-full)
       normalized-args (collapse-conjunction-constants args)]
   (if (= 1 (count normalized-args))
      (first normalized-args)
      (cons ::conjunction normalized-args))))


; Disjunction
(defn disjunction [expr & rest]
  (cons ::disjunction (cons expr rest)))

(defn disjunction? [expr]
  (= ::disjunction (first expr)))


(defmethod operator? ::disjunction [expr]
  true)


(defn- collapse-disjunction-constants [exprs]
  (let [consts (filter constant? exprs)
        other-exprs (remove constant? exprs)
        combined-const
        (reduce
          (fn [acc entry]
            (or acc
               (constant-val entry)))
          false consts)]
    (cond
      (= combined-const true)
      (list (constant true))
      :else
      (if (empty? other-exprs)
        (list (constant false))
        other-exprs))))

(defmethod operator-calc ::disjunction [expr-full]
  (let [args (operator-args expr-full)
        normalized-args (collapse-disjunction-constants args)]
    (if (= 1 (count normalized-args))
      (first normalized-args)
      (cons ::disjunction normalized-args))))


(defn -main [& args]
  (println
    (negation (negation (implication (variable :x) (variable :x))))))
  
  
  