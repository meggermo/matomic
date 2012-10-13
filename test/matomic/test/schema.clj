(ns matomic.test.schema
  (:use
    [matomic.schema]
    [midje.sweet]))

(fact 
  "defentity"
  (defentity :test) => (contains {:db/ident :test}))

(fact
  "defpart"
  (defpart :test) => (contains {:db/ident :test}))

(facts 
  "defattr"
  (defattr :test nil) 
  => (contains {:db/ident :test
                :db/valueType nil
                :db/cardinality :db.cardinality/one})
  (defattr :test nil nil)
  => (contains {:db/ident :test
                :db/valueType nil
                :db/cardinality nil}))

(facts
  "with"
  (let [a (defattr "test" :db.valueType/string)] 
    (-> a 
      (with-doc "test")) => (contains {:db/doc "test"})
    (-> a 
      (with-unique "test")) => (contains {:db/unique "test"})
    (-> a 
      (with-index)) => (contains {:db/index true})
    (-> a 
      (with-unique-index "test")) => (contains {:db/unique "test" :db/index true})
    (-> a 
      (with-fulltext)) => (contains {:db/fulltext true})
    (-> a
      (with-doc "test")
      (with-unique-index "test")
      (with-fulltext)) => (contains {:db/doc "test" :db/unique "test" :db/index true :db/fulltext true})
    )
  )

