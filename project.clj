(defproject matomic "1.0.0-SNAPSHOT"

  :description "My project to explore Datomic"

  :dependencies [
    [org.clojure/clojure "1.4.0"]
    [com.datomic/datomic "0.1.3142"
       :exclusions [org.slf4j/slf4j-nop 
                    org.slf4j/slf4j-log4j12]]
    [ch.qos.logback/logback-classic "1.0.1"]
  ]
  :dev-dependencies [
    [lein-marginalia "0.7.0"]
  ]
  :plugins [
    [lein-swank "1.4.4"]
  ]
)
