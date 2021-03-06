(ns matomic.test.currency
  (:use 
   [matomic.currency]
   [midje.sweet]))

(facts "new-ccy"
  (new-ccy "EUR" 2) => {:currency/iso-code "EUR" :currency/nr-of-decimals 2}
  (new-ccy "JPY" 0) => {:currency/iso-code "JPY" :currency/nr-of-decimals 0}
)
