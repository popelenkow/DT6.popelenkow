(ns clojure2.core)

(def Step (double 1/1000))


(defn- GetCountStep
  [x]
  (double (int 
            (/
              (double x)
              Step))))

(defn- IFun
  [fun]
  (fn [i]
    (*
      Step
      (double 1/2)
      (+
        (fun (* Step i))
        (fun (* Step (dec i)))))))

(defn- ISum [ifun, count]
  (loop [i count
         acc (double 0)]
    (if (pos? i)
      (recur (dec i) (+ acc (ifun i)))
      acc)))
  
(defn IntegrateFun [fun]
  (fn [x]
    (let [count (GetCountStep x)
          ifun  (IFun fun)]
      (ISum ifun count))))



(def IFun-memo
  (memoize
    (fn [fun]
        (memoize (IFun fun)))))

(defn IntegrateFun-memo
  [fun]
  (fn [x]
    (let [count (GetCountStep x)
          ifun  (IFun-memo fun)]
      (ISum ifun count))))

(defn foo [x]
  (Thread/sleep 100)
  x)
  

(defn -main [& args]
  (time ((IntegrateFun-memo foo) 0.01)))
