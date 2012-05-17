;; #Matomic Schema
;; Functions that simplify the programmatic declaration of Datomic schema entities.
;;
;; Example code:  
;; `(-> (defattr :test :db.type/string)`  
;;   `(with-doc "A test attribute")`  
;;   `(with-unique-index :db.unique/value)`  
;;   `(with-fulltext))`  
;;
(ns matomic.schema)

(defmacro defentity
  "Macro that returns a rudimentary Datomic schema map.
   Each Datomic schema entity that you wish to store in Datomic
   should at least have a unique identity and unique id 
   (in the db partition). And that's what this macro returns."
  [ident]
  `{:db/id (datomic.api/tempid :db.part/db)
    :db/ident ~ident})

;; Datomic allows you to define your own partions where your application data resides.
(defn defpart
  "Returns a map that represents a Datomic schema definition of a partition.
   Required arguments:  
    - a unique partition identifier  "
  [ident] 
  (assoc 
    (defentity ident) 
    :db.install/_partition :db.part/db))

(defn defattr 
  "Returns a map that represents a Datomic schema definition of an attribute.
   Required arguments:  
    - unique attribute identifier  
    - valueType  
    - cardinality (either one or many)  "
  ([ident valueType cardinality]
   (assoc
    (defentity ident)
    :db/valueType valueType
    :db/cardinality cardinality
    :db.install/_attribute :db.part/db))
  ([ident valueType]
   (defattr ident valueType :db.cardinality/one)))

(defn defattrs [seq]
  (vec (map #(assoc % :db.install/_attribute :db.part/db) seq)))

(defn with-doc
  "Associates a :db/doc attribute with m and returns the new map.
   This function can be used for defining attributes and partitions."
  [m doc]
  (assoc m :db/doc doc))
(defn with-unique
  "Associates a :db/unique attribute with m and returns the new map.
   The unique argument should be :db.unique/ident or :db.unique/value"
  [m unique]
  (assoc m :db/unique unique))
(defn with-index
  "Associates a :db/index attribute with value true with m and returns the new map."
  [m]
  (assoc m :db/index true))
(defn with-unique-index
  "Composition of with-unique and with-index."
  [m unique]
  (with-index (with-unique m unique)))
(defn with-fulltext
  "Associates :db/fulltext attribute with value true with m and returns the new map."
  [m]
  (assoc m :db/fulltext true))

