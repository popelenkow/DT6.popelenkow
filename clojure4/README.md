# clojure4

A Clojure library designed to boolean algebra.

## Doc

### Elments
* constant - constructor boolean constant.
* variable - constructor boolean variable.
* element - constructor boolean constant or variable.

### Operator base
* operator? - check type of expression.
* operator-val - get type of operator.
* operator-args - get arguments of operator.
* operator-cacl - calculate values without depth-searching. Calculated only variables and constants.

### Expressions
* expression-calc - calculate values with depth-searching. Recursively executes operator-cacl.

### Setter
* expression-set-variable - substituting value of variable in expression.
* expression-set-variables - substitution values of variables in expression.

### Searcher
* expression-search-variables - returns all used variables in the expression.

### Truth table
* truth-table - contructor truth table. First element is name, second is list variables, thid is truth table. The table consists of a complete enumeration of the values with result.

### CDNF
* cdnf - constructor complete disjunctive normal form. Argument of function is truth table.

### Operators
Implementation negation, implication, conjunction, disjunction.

## Examples

* (negation (negation (implication (variable :x) (variable :x))))

=> (:user/negation (:user/negation (:user/implication (:user/variable :x) (:user/variable :x))))

* (expression-calc (negation (negation (implication (variable :x) (variable :x)))))

=> (:user/constant true)

* (expression-calc (conjunction (constant true) (variable :y) (variable :x) (variable :x)))


=> (:user/conjunction (:user/variable :y) (:user/variable :x))

* (truth-table (variable :x))

name ______________ variables ____________ res1 _________________ constants1 ______________ res2 __________________ constants2

=> (:user/truth-table ((:user/variable :x)) (((:user/constant true) ((:user/constant true))) ((:user/constant false) ((:user/constant false)))))

* (cdnf (truth-table (negation (implication (variable :x) (variable :y)))))

=> (:user/disjunction (:user/conjunction (:user/variable :x) (:user/negation (:user/variable :y))))

* (expression-calc (cdnf (truth-table (negation (implication (variable :x) (variable :y))))))

=> (:user/conjunction (:user/variable :x) (:user/negation (:user/variable :y)))