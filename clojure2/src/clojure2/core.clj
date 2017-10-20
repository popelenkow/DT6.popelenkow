(ns clojure2.core)

(def Step (double 1/1000))

(defn- GetCountStepCos
  [x]
  (double (int 
            (/
              (double x)
              Step))))


(defn- ICos
  [i]
  (*
    Step
    (double 1/2)
    (+
      (Math/cos (* Step i))
      (Math/cos (* Step (dec i))))))

(defn- _IntegrateCos
  [count]
  (loop [i count
         acc (double 0)]
    (if (pos? i)
      (recur (dec i) (+ acc (ICos i)))
      acc)))

(def _IntegrateCos-memo
  (memoize _IntegrateCos))
  
(defn IntegrateCos
  [x]
  (_IntegrateCos (GetCountStepCos x)))

(defn IntegrateCos-memo
  [x]
  (let [i (GetCountStepCos x)]
    (_IntegrateCos-memo i)))

(defn -main [& args]
  (println
    (IntegrateCos 1)))
