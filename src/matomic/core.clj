;; This namespace contains functions to simplify programmatic declaration
;; of Datomic entities.
(ns matomic.core
    (use [datomic.api :only [tempid]]))

(defmacro bind-partition
  "Marco that returns a function that binds a parition
   identified by partition-ident to the Datomic tempid function."
  [partition-ident]
  `(partial tempid ~partition-ident))

(defn assoc-when
  "If the map m contains the key in the key set s
   then the value is replaced with the return value
   of applying f to the value."
  [m ks f & args]
  (letfn [(get-val [k] (get m k))
          (map-val [k] [k (apply f (get-val k) args)])]
         (reduce 
           (partial apply assoc) m
           (map map-val (filter get-val ks)))))

(defn with-partition-fn
  "Returns a function that will replace all values in a map
   with a call to the Datomic tempid function (in the context 
   of the given partition) for the given keys."
  [partition keys] 
  (fn [m] 
      (assoc-when m keys (bind-partition partition))))

(defn with-partition
  "Applies the with-partition-fn to all maps and returns the results
   in a vector. The returned structure can be used as input for a 
   Datomic transaction."
  [partition keys maps]
  (into [] (map (with-partition-fn partition keys) maps)))

