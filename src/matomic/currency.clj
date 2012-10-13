(ns matomic.currency)

(defn new-ccy
  [iso-code nr-of-decimals]
  {:currency/iso-code iso-code :currency/nr-of-decimals nr-of-decimals}
  )
