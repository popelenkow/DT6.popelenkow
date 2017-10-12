(ns clojure1.core)

(defn multiply [first acc]
  (for [ch first
        str acc
				:when (not (= (.charAt ch 0) (.charAt str 0)))]
				(.concat ch str)))

(defn superMultiply [N arr]
  "N is length, arr is dictionary"
  (loop [i N
         first arr
         acc arr]
    (if (pos? (dec i))
      (recur (dec i) first (multiply first acc))
      acc)))
  
(defn -main [& args]
  (println
    (superMultiply 3 ["^" "\u0040" "x"])))
