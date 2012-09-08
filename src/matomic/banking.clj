;; This namespace is my playground to try out functions defined in the matomic.schema namespace
;; as well as playing around with Datomic in general.
(ns matomic.banking
    (:use [datomic.api :only [q db] :as d])
    (:require [matomic.schema :as s])
    (:require [matomic.core :as c]))

;; The database URI. It seems sensible to name the database to the context that it will be used in.
;; In my case, that's the manegement of bank accounts. 
(def uri "datomic:mem://banking")

;; Now create the database from the uri. In a real application you'll have a persistent database so
;; normally the database is created only once. However, an in-memory database always needs to be re-created. 
(d/create-database uri)

;; OK, database is up and running. Now get a connection to the database. Of course, when dealing with a persistent
;; (non in-memory) database logon creadentials will be required to get a connection. I've not discivered documentation
;; on how to do that yet on the Datomic web-site though.
(def conn (d/connect uri))

;; Get the transaction report queue. I'm not using this at the moment, but I would like to explore the possibillities
;; of this queue. Need to read some more about it.
(def queue (d/tx-report-queue conn))

;; Currently my schema definition is in this namespace. This is to see if it is worth the effort to define the schema
;; programatically with my utility functions.
;; The schema consists of the following entities:  
;; - Currency  
;; - Bank  
;; - Company  
;; - Account  
;; The account has references to a currency, a bank and a company,
;; since an account is holding money in one currency and is registered at a bank and
;; is a owned (in my case) by a company.
;;
;; NOTE: Just found out that in the real world it is quite normal to have an account for multiple
;; currencies, so I might need to reconsider my data model ....
(def schema [
 (-> (s/defattr :currency/iso-code :db.type/string)
     (s/with-unique-index :db.unique/value)
     (s/with-doc "The ISO code of the currency"))
 (-> (s/defattr :currency/decimals :db.type/long)
     (s/with-doc "The number of decimals of the currency"))
 (-> (s/defattr :bank/bic :db.type/string)
     (s/with-unique-index :db.unique/value)
     (s/with-doc "The bank identification code, e.g. the SWIFT address"))
 (-> (s/defattr :account/id :db.type/string)
     (s/with-doc "The account identification code"))
 (-> (s/defattr :account/currency :db.type/ref)
     (s/with-doc "The currency of the account's balance"))
 (-> (s/defattr :account/bank :db.type/ref)
     (s/with-doc "The bank where the account is registered"))
 (-> (s/defattr :account-holder/account :db.type/ref :db.cardinality/many)
     (s/with-doc "The accounts owned by the account-holder"))
 (-> (s/defattr :company/name :db.type/string)
     (s/with-unique-index :db.unique/value)
     (s/with-doc "The name of a company"))
 (-> (s/defattr :company/parent :db.type/ref)
     (s/with-doc "The parent of a company"))
   ])
@(d/transact conn schema)

;; Now that the schema is in place we can fill it with some data.
;; First I load the root data for the currencies.
;; Each currency is labeled with an :db/ident to make it possible to refer to a currency
;; via qualified enumerations, e.g. :currency/EUR.
@(d/transact conn (read-string (slurp "resources/currencies.dtm")))
;; And then load the rest of the data.
@(d/transact conn (read-string (slurp "resources/data.dtm")))

;; I'm working on a way to make programatically adding data simpler.
;; I know that data can reside in user defined partitions, so I want
;; to be able to pass it in as a parameter. Furthermore, since attributes
;; of type ref point to an id of another attribute, I want those to be mapped too.
;; That's what the set is for. It contains all keys that have (a negative) id as
;; value. The with-partition function will map the id to #db/id[:db.part/user id].
;; Saves a lot of typing. This is an example of how the with-partition function works.
(def bank-data 
  (c/with-partition :db.part/user #{:db/id :account/bank} 
    [{:bank/bic   "BANK_0" :db/id -1000}
     {:account/id "ACCT_0" :db/id -1001 :account/bank -1000 :account/currency :currency/EUR}
     {:account/id "ACCT_1" :db/id -1002 :account/bank -1000 :account/currency :currency/JPY}]))
@(d/transact conn bank-data)
 
;; This function maps the given sequence of lists (or any other sequeble type) to a map, where the key
;; is the first element and the value is the rest of the list. The assuption therefore is that each
;; element in the seq is a seq.
(defn map-by-first [seq]
  (letfn [(f [m [k & v]]
             (assoc m k (into v (m k))))]
         (reduce f {} seq)))

;; With this function we can now find all accounts per currency and return it as a map
;; of accounts per currency.
(map-by-first 
  (q '[:find ?code ?id :where
       [?a :account/id ?id]
       [?a :account/currency ?c]
       [?c :currency/iso-code ?code]]
     (db conn)))

;; Find all names of parent companies
(vec (q '[:find ?name :where
          [?c :company/name ?name]
          [_  :company/parent ?c]]
        (db conn)))

;; Find all names of child companies
(vec (q '[:find ?name :where
          [?c :company/name ?name]
          [?c :company/parent _]]
        (db conn)))

;; Find all names of parents and their childs
(def parents-and-children
  (map-by-first 
        (q '[:find ?pn ?cn :where 
                [?p :company/name ?pn] 
                [?c :company/name ?cn]
                [?c :company/parent ?p]] 
              (db conn))))
;; and pretty print them.
(doseq [[parent children] parents-and-children]
       (println "Parent: " parent)
       (println "  Children:" children))

