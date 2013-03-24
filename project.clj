(defproject matomic "1.0.0-SNAPSHOT"

  :description "My project to explore Datomic"

  :dependencies 
  [[org.clojure/clojure "1.5.1"]
   [com.datomic/datomic-free "0.8.3848"
    :exclusions [org.slf4j/slf4j-nop org.slf4j/slf4j-log4j12]]
   [ch.qos.logback/logback-classic "1.0.10"]
   [midje "1.5.1"]]

  :profiles
  {:dev {:dependencies [[midje "1.5.1"]]
         :plugins [[lein-midje "3.0.1"]]}}
)
