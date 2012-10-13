(defproject matomic "1.0.0-SNAPSHOT"

  :description "My project to explore Datomic"

  :dependencies 
  [[org.clojure/clojure "1.4.0"]
   [com.datomic/datomic-free "0.8.3488"
    :exclusions [org.slf4j/slf4j-nop org.slf4j/slf4j-log4j12]]
   [ch.qos.logback/logback-classic "1.0.7"]
   [midje "1.4.0"]]

  :profiles
  {:dev {:dependencies
         ;; lein-midje --lazytest requires this dependency
         [[midje "1.4.0"] [com.stuartsierra/lazytest "1.2.3"]]
         :repositories  {"stuart" "http://stuartsierra.com/maven2"}}}
)
