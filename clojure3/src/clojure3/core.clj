(ns clojure3.core)

(def Step (double 1/1000))


(defn- GetCountStep
  [x]
  (double (int 
            (/
              (double x)
              Step))))

(defn- IFun
  [fun, i]
  (*
    Step
    (double 1/2)
    (+
      (fun (* Step i))
      (fun (* Step (dec i))))))

(defn- ISum [ifun, fun, count]
  (loop [i count
         acc (double 0)]
    (if (pos? i)
      (recur (dec i) (+ acc (ifun fun i)))
      acc)))
  
(defn IntegrateFun [fun]
  (fn [x]
    (let [count (GetCountStep x)
          ifun (IFun fun)]
      (ISum ifun count))))


(defn- IFun-seq
  [ifun, fun, i]
    (lazy-seq
      (cons (ifun fun i) (IFun-seq ifun fun (inc i)))))
  
(def IFun-seq-memo
  IFun-seq)

(defn- IFun-memo
[fun, i]
  (let [ifun IFun
        ifun-seq (IFun-seq-memo ifun fun 0)]
    (nth
      ifun-seq
      i)))


(defn IntegrateFun-memo
  [fun]
  (fn [x]
    (let [count (GetCountStep x)
          ifun IFun-memo]
      (ISum ifun fun count))))

(defn foo [x]
  (Thread/sleep 100)
  x)

(defn -main [& args]
  (println
    ((IntegrateFun (fn [x] x)) 1)))
