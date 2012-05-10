(ns matomic.core
    (:use [datomic.api :only [q db] :as d])
    (:require [matomic.schema :as s]))

;; The database URI
(def uri "datomic:mem://tweets")
;; create the database
(d/create-database uri)
;; get a connection to the database
(def conn (d/connect uri))
;; get the transaction report queue
(def queue (d/tx-report-queue conn))

(def ecf-schema [
;; Currency
(-> (s/defattr :ecf.currency/iso-code :db.type/string)
    (s/with-doc "The iso code of the currency")
    (s/with-unique-index :db.unique/value))
(-> (s/defattr :ecf.currency/decimals :db.type/long)
    (s/with-doc "The number of decimals of the currency"))
;; Bank
(-> (s/defattr :ecf.bank/bic :db.type/string)
    (s/with-doc "SWIFT code of the bank")
    (s/with-unique-index :db.unique/value))
;; Account
(-> (s/defattr :ecf.account/id :db.type/string)
    (s/with-doc "The account identification known in the external world"))
(-> (s/defattr :ecf.account/bank :db.type/ref)
    (s/with-doc "Reference to the bank that owns this account"))
(-> (s/defattr :ecf.account/currency :db.type/ref)
    (s/with-doc "Reference to the currency of this account"))
(-> (s/defattr :ecf.account/owner :db.type/ref)
    (s/with-doc "The owner of this account"))
;; Company
(-> (s/defattr :ecf.company/name :db.type/string)
    (s/with-doc "The unqiue name of the company")
    (s/with-unique-index :db.unique/value))
(-> (s/defattr :ecf.company/parent :db.type/ref)
    (s/with-doc "The parent company of this company"))])
@(d/transact conn ecf-schema)

(def ecf-currencies [
 {:db/ident :ecf.currency/EUR :ecf.currency/iso-code "EUR" :ecf.currency/decimals 4 :db/id #db/id[:db.part/user]}
 {:db/ident :ecf.currency/USD :ecf.currency/iso-code "USD" :ecf.currency/decimals 4 :db/id #db/id[:db.part/user]}
 {:db/ident :ecf.currency/NOK :ecf.currency/iso-code "NOK" :ecf.currency/decimals 4 :db/id #db/id[:db.part/user]}
 {:db/ident :ecf.currency/JPY :ecf.currency/iso-code "JPY" :ecf.currency/decimals 0 :db/id #db/id[:db.part/user]}
])
@(d/transact conn ecf-currencies)

(def ecf-data [
 {:ecf.bank/bic "BKMGNL20" :db/id #db/id[:db.part/user -2000]}
 {:ecf.bank/bic "BKMGNL21" :db/id #db/id[:db.part/user -2001]}
 {:ecf.bank/bic "BKMGNL22" :db/id #db/id[:db.part/user -2002]}
 {:ecf.bank/bic "BKMGNL23" :db/id #db/id[:db.part/user -2003]}
 {:ecf.bank/bic "BKMGNL24" :db/id #db/id[:db.part/user -2004]}
 {:ecf.company/name "HEEREMA HOLDING" :db/id #db/id[:db.part/user -3000]}
 {:ecf.company/name "HEEREMA EUROPE"  :db/id #db/id[:db.part/user -3001] :ecf.company/parent #db/id[:db.part/user -3000]}
 {:ecf.company/name "HEEREMA ASIA"    :db/id #db/id[:db.part/user -3002] :ecf.company/parent #db/id[:db.part/user -3000]}
 {:ecf.account/id "ACC-4000-EUR-20" :db/id #db/id[:db.part/user -4000] :ecf.account/owner #db/id[:db.part/user -3000]
  :ecf.account/currency :ecf.currency/EUR :ecf.account/bank #db/id[:db.part/user -2000]}
 {:ecf.account/id "ACC-4001-USD-20" :db/id #db/id[:db.part/user -4001] :ecf.account/owner #db/id[:db.part/user -3000]
  :ecf.account/currency :ecf.currency/USD :ecf.account/bank #db/id[:db.part/user -2000]}
 {:ecf.account/id "ACC-4002-NOK-20" :db/id #db/id[:db.part/user -4002] :ecf.account/owner #db/id[:db.part/user -3000]
  :ecf.account/currency :ecf.currency/NOK :ecf.account/bank #db/id[:db.part/user -2000]}
 {:ecf.account/id "ACC-4003-JPY-20" :db/id #db/id[:db.part/user -4003] :ecf.account/owner #db/id[:db.part/user -3000]
  :ecf.account/currency :ecf.currency/JPY :ecf.account/bank #db/id[:db.part/user -2001]}
 {:ecf.account/id "ACC-4010-EUR-21" :db/id #db/id[:db.part/user -4010] :ecf.account/owner #db/id[:db.part/user -3000]
  :ecf.account/currency :ecf.currency/EUR :ecf.account/bank #db/id[:db.part/user -2001]}
 {:ecf.account/id "ACC-4011-USD-21" :db/id #db/id[:db.part/user -4011] :ecf.account/owner #db/id[:db.part/user -3000]
  :ecf.account/currency :ecf.currency/USD :ecf.account/bank #db/id[:db.part/user -2001]}
 {:ecf.account/id "ACC-4012-NOK-21" :db/id #db/id[:db.part/user -4012] :ecf.account/owner #db/id[:db.part/user -3000]
  :ecf.account/currency :ecf.currency/NOK  :ecf.account/bank #db/id[:db.part/user -2001]}
 {:ecf.account/id "ACC-4013-JPY-21" :db/id #db/id[:db.part/user -4013] :ecf.account/owner #db/id[:db.part/user -3000]
  :ecf.account/currency :ecf.currency/JPY :ecf.account/bank #db/id[:db.part/user -2001]}
 {:ecf.account/id "ACC-4020-EUR-21" :db/id #db/id[:db.part/user -4020] :ecf.account/owner #db/id[:db.part/user -3001]
  :ecf.account/currency :ecf.currency/EUR :ecf.account/bank #db/id[:db.part/user -2001]}
 {:ecf.account/id "ACC-4021-USD-21" :db/id #db/id[:db.part/user -4021] :ecf.account/owner #db/id[:db.part/user -3001]
  :ecf.account/currency :ecf.currency/USD :ecf.account/bank #db/id[:db.part/user -2001]}
 {:ecf.account/id "ACC-4022-NOK-21" :db/id #db/id[:db.part/user -4022] :ecf.account/owner #db/id[:db.part/user -3001]
  :ecf.account/currency :ecf.currency/NOK :ecf.account/bank #db/id[:db.part/user -2001]}
 {:ecf.account/id "ACC-4023-JPY-21" :db/id #db/id[:db.part/user -4023] :ecf.account/owner #db/id[:db.part/user -3001]
  :ecf.account/currency :ecf.currency/JPY :ecf.account/bank #db/id[:db.part/user -2001]}
])
@(d/transact conn ecf-data)

(defn map-by-first [seq]
  (letfn [(f [m [k & v]]
             (assoc m k (into v (m k))))]
         (reduce f {} seq)))
(map-by-first (vec (q '[:find ?code ?id :where
        [?a :ecf.account/id ?id]
        [?a :ecf.account/currency ?c]
        [?a :ecf.account/owner ?o]
        [?c :ecf.currency/iso-code ?code]]
   (db conn))))

