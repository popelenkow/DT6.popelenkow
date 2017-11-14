(ns clojure4.core)

(defn boolean? [x]
  (instance? Boolean x))

(defn third [x]
  (second (next x)))

; Check type
(defn constant? [expr]
  {:post [(boolean? %)]}
  (= ::constant (first expr)))

(defn variable? [expr]
  {:post [(boolean? %)]}
  (= ::variable (first expr)))

(defn element? [expr]
  {:post [(boolean? %)]}
  (or
    (constant? expr)
    (variable? expr)))

; Constructor
(defn constant  [val]
  {:pre  [(boolean? val)]
   :post [(constant? %)]}
  (list ::constant val))

(defn variable [val]
  {:pre  [(keyword? val)]
   :post [(variable? %)]}
  (list ::variable val))

(defn element [val]
  {:pre  [(or (boolean? val) (keyword? val))]
   :post [(element? %)]}
  (if (boolean? val)
    (constant val)
    (variable val)))


; Get value
(defn constant-val [el]
  {:pre  [(constant? el)]
   :post [(boolean? %)]}
  (second el))

(defn variable-val [el]
  {:pre  [(variable? el)]
   :post [(keyword? %)]}
  (second el))

(defn element-val [el]
  {:pre  [(element? el)]
   :post [(or (boolean? %) (keyword? %))]}
  (second el))

; Same elements
(defn same-constants? [el1 el2]
  {:post [(boolean? %)]}
  (and
    (constant? el1)
    (constant? el2)
    (=
      (constant-val el1)
      (constant-val el2))))

(defn same-variables? [el1 el2]
  {:post [(boolean? %)]}
  (and
    (variable? el1)
    (variable? el2)
    (=
      (variable-val el1)
      (variable-val el2))))

(defn same-elements? [el1 el2]
  {:post [(boolean? %)]}
  (and
    (element? el1)
    (element? el2)
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
  {:post [(boolean? %)]}
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
  
; Args
(defn args? [args]
  {:post [(boolean? %)]}
  (if (empty? args)
      true
      (some true?
          (map (resolve 'expression?) args))))

  
; Expressions
(defn expression? [expr]
  {:post [(boolean? %)]}
  (or
    (element? expr)
    (operator? expr)))

(defn- args-calc-seq [arg-first args-rest]
  {:pre [((resolve 'expression?) arg-first)]}
  (lazy-seq
    (cons
      ((resolve 'expression-calc) arg-first)
      (if (pos? (count args-rest))
          (args-calc-seq (first args-rest) (rest args-rest))
          nil))))

(defn- args-calc [args]
  {:pre [(args? args)]}
  (take
    (count args)
    (args-calc-seq (first args) (rest args))))

(defn expression-calc [expr]
  {:pre  [(expression? expr)]
   :post [(expression? %)]}
  (if (operator? expr)
      (let [args (operator-args expr)]
           (operator-calc
             (cons
               (operator-val expr)
                 ((resolve 'args-calc) args))))
     expr))

; Setter

(defn- args-set-seq [arg-first args-rest vrb cns]
  {:pre [((resolve 'expression?) arg-first)]}
  (lazy-seq
    (cons
      ((resolve 'expression-set-variable) arg-first vrb cns)
      (if (pos? (count args-rest))
          (args-set-seq (first args-rest) (rest args-rest) vrb cns)
          nil))))
        
(defn- args-set [args vrb cns]
  {:pre [(args? args)]}
  (take
    (count args)
    (args-set-seq (first args) (rest args) vrb cns)))

(defn expression-set-variable [expr vrb cns]
  {:pre  [(and (expression? expr) (variable? vrb) (constant? cns))]
   :post [(expression? %)]}
 (if (operator? expr)
     (let [args (operator-args expr)]
          (cons
            (operator-val expr)
            ((resolve 'args-set) args vrb cns)))
      (if (same-elements? vrb expr)
          cns
          expr)))

(defn expression-set-variables [expr vrbs cnss]
  {:pre  [(expression? expr)]
   :post [(expression? %)]}
 (loop [acc expr
        vrbs-rest vrbs
        cnss-rest cnss]
      (if (empty? vrbs-rest)
          acc
          (recur
            (expression-set-variable acc (first vrbs-rest) (first cnss-rest))
            (rest vrbs-rest)
            (rest cnss-rest)))))
        
; Searcher
(defn- args-search [args]
  {:pre [(args? args)]}
  (loop [args-rest args
         acc ()]
       (if (empty? args-rest)
           acc
           (recur (rest args-rest) (concat acc ((resolve 'expression-search) (first args-rest)))))))

(defn- expression-search [expr]
   {:pre  [(expression? expr)]
   :post  [(args? %)]}
  (cond
    (operator? expr) (args-search (operator-args expr))
    (variable? expr) (list expr)
    :else            ()))

(defn- collapse-only-variables [args]
    {:pre [(args? args)]}
  (let [arg (first args)
        args-rest (remove (fn [x] (same-variables? arg x)) (rest args))]
      (if (empty? args-rest)
          (list arg)
          (cons arg (collapse-only-variables args-rest)))))


(defn collapse-variables [args]
    {:pre [(args? args)]}
  (let [varibles (filter variable? args)
        other-args (remove variable? args)
        combined-variables (collapse-only-variables varibles)]
      (if (empty? varibles)
          args
          (concat combined-variables other-args))))
  
(defn expression-search-variables [expr]
   {:pre  [(expression? expr)]
   :post  [(args? %)]}
  (collapse-variables (expression-search expr)))
 
 
; Truth table 
(defn truth-table? [expr]
  {:post [(boolean? %)]}
  (= ::truth-table (first expr)))


(defn brute-force-search [lenght]
  (if (not (pos? lenght))
      ()
      (loop [acc (list (list (constant false)) (list (constant true)))
             it (dec lenght)]
           (if
             (zero? it) acc
             (recur
               (for [x acc 
                     y (list (list (constant false)) (list (constant true)))]
                   (concat x y))
                 (dec it))))))
         
(defn truth-table [expr]
  {:pre  [(expression? expr)]
   :post [(truth-table? %)]}
 (let [variables (expression-search-variables expr)]
      (list
        ::truth-table
        variables
        (loop [acc ()
               brute-rest (brute-force-search (count variables))]
              (if (empty? brute-rest)
                  acc
                  (recur
                    (cons
                      (list
                        (let [exp (expression-set-variables expr variables (first brute-rest))
                              res (expression-calc exp)]
                            res)
                        (first brute-rest))
                      acc)
                      (rest brute-rest)))))))

; CDNF
(defn cdnf-elem [vrbs cnss]
  {:pre  [(args? vrbs)]
   :post [(or (empty? %) (expression? %))]}
 (apply
   (resolve 'conjunction)
   (loop [acc ()
          vrbs-rest vrbs
          cnss-rest cnss]
        (if (empty? vrbs-rest)
            acc
            (recur
              (concat
                acc
                (list
                  (let [vrb (first vrbs-rest)
                        cns (first cnss-rest)]
                      (if (constant-val cns)
                          vrb
                          (negation vrb)))))
              (rest vrbs-rest)
              (rest cnss-rest))))))
   
(defn cdnf [table]
  {:pre  [(truth-table? table)]
   :post [(expression? %)]}
 (let [variables (second table)]
      (apply
        (resolve 'disjunction)
        (loop [acc ()
               brute-rest (third table)]
             (if (empty? brute-rest)
                 acc
                 (let [brute (first brute-rest)
                       res (first brute)
                       cnss (second brute)]
                     (recur
                       (if (constant-val res)
                           (concat
                             acc
                             (list
                               (cdnf-elem variables cnss)))
                            acc)
                       (rest brute-rest))))))))
       
            

; Negation
(defn negation [expr]
  (list ::negation expr))

(defn negation? [expr]
  {:post [(boolean? %)]}
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
  {:post [(boolean? %)]}
  (= ::implication (first expr)))

(defmethod operator? ::implication [expr]
  true)


(defmethod operator-calc ::implication [expr-full]
  (let [expr1 (second expr-full)
        expr2 (third expr-full)]
    (if (same-elements? expr1 expr2)
      (constant true)
      (if (constant? expr1)
        (if (constant-val expr1)
          expr2
          (constant true))
        (if (constant? expr2)
          (if (constant-val expr2)
            (constant true)
            (negation expr1))
          expr-full)))))


; Conjunction
(defn conjunction [expr & rest]
  (cons ::conjunction (cons expr rest)))

(defn conjunction? [expr]
  {:post [(boolean? %)]}
  (= ::conjunction (first expr)))

(defmethod operator? ::conjunction [expr]
  true)


(defn- collapse-conjunction-constants [args]
  (let [consts (filter constant? args)
        other-args (remove constant? args)
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
      (if (empty? other-args)
        (list (constant true))
        other-args))))

(defmethod operator-calc ::conjunction [expr-full]
  (let [args (operator-args expr-full)
       normalized-variables (collapse-variables args)
       normalized-args (collapse-conjunction-constants normalized-variables)]
   (if (= 1 (count normalized-args))
       (first normalized-args)
       (cons ::conjunction normalized-args))))


; Disjunction
(defn disjunction [expr & rest]
  (cons ::disjunction (cons expr rest)))

(defn disjunction? [expr]
  {:post [(boolean? %)]}
  (= ::disjunction (first expr)))


(defmethod operator? ::disjunction [expr]
  true)


(defn- collapse-disjunction-constants [args]
  (let [consts (filter constant? args)
        other-args (remove constant? args)
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
      (if (empty? other-args)
        (list (constant false))
        other-args))))

(defmethod operator-calc ::disjunction [expr-full]
  (let [args (operator-args expr-full)
        normalized-args (collapse-disjunction-constants (collapse-variables args))]
    (if (= 1 (count normalized-args))
      (first normalized-args)
      (cons ::disjunction normalized-args))))
    

(expression-calc (cdnf (truth-table
  (implication (variable :y) (variable :x)))))

(defn -main [& args]
  (println
    (negation (negation (implication (variable :x) (variable :x))))))
  
  
  