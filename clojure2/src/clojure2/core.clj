(ns clojure2.core)

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



(def IFun-memo
  (memoize IFun))


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
  (time ((IntegrateFun-memo foo) 0.01)))
