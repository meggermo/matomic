;; This namespace is my playground to try out functions defined in the matomic.schema namespace
;; as well as playing around with Datomic in general.
(ns matomic.core
    (:use [datomic.api :only [q db] :as d])
    (:require [matomic.schema :as s]))

;; The database URI. It seems sensible to name the database to the context that it will be used in.
;; In my case, that's the manegement of bank accounts. 
(def uri "datomic:mem://banking")

;; Now create the database from the uri. In a real application you'll have a persistent database so
;; normally the database is created only once. Howeveer, an in-memory database always needs to be re-created. 
(d/create-database uri)

;; OK, database is up and running. Now get a connection to the database. Of course, when dealing with a persistent
;; (non in-memory) database logon creadentials will be required to get a connection. I've not discivered documentation
;; on hot to do that yes on the Datomic web-site though.
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
;; since an account is holding money in one currency and is registed at a bank and
;; is a owned (in my case) by a company.
(def ecf-schema [
;; Currency
(-> (s/defattr :currency/iso-code :db.type/string)
    (s/with-doc "The iso code of the currency")
    (s/with-unique-index :db.unique/value))
(-> (s/defattr :currency/decimals :db.type/long)
    (s/with-doc "The number of decimals of the currency"))
;; Bank
(-> (s/defattr :bank/bic :db.type/string)
    (s/with-doc "Bank identification code of the bank (SWIFT address)")
    (s/with-unique-index :db.unique/value))
;; Account
(-> (s/defattr :account/id :db.type/string)
    (s/with-doc "The account identification known in the external world"))
(-> (s/defattr :account/bank :db.type/ref)
    (s/with-doc "Reference to the bank that owns this account"))
(-> (s/defattr :account/currency :db.type/ref)
    (s/with-doc "Reference to the currency of this account"))
(-> (s/defattr :account/owner :db.type/ref)
    (s/with-doc "The owner of this account"))
;; Company
(-> (s/defattr :company/name :db.type/string)
    (s/with-doc "The unqiue name of the company")
    (s/with-unique-index :db.unique/value))
(-> (s/defattr :company/parent :db.type/ref)
    (s/with-doc "The parent company of this company"))])
@(d/transact conn ecf-schema)

;; Now that the schema is in place we can fill it with some data.
;; First I load the root data for the currencies.
;; Each currency is labeled with an :db/ident to make it possible to refer to a currency
;; via qualified enumerations, e.g. :currency/EUR.
@(d/transact conn (read-string (slurp "resources/ecf-currencies.dtm")))
;; And then load the rest of the data.
@(d/transact conn (read-string (slurp "resources/ecf-data.dtm")))

;; This function maps the given sequence of lists (or any other sequeble type) to a map, where the key
;; is the first element and the value is the rest of the list. The assuption therefore is that each
;; element in the seq is a seq.
(defn map-by-first [seq]
  (letfn [(f [m [k & v]]
             (assoc m k (into v (m k))))]
         (reduce f {} seq)))

;; With this function we can now find all accounts per currency and return it as a map
;; of accounts per currency.
(map-by-first (vec (q '[:find ?code ?id :where
        [?a :account/id ?id]
        [?a :account/currency ?c]
        [?a :account/owner ?o]
        [?c :currency/iso-code ?code]]
   (db conn))))

