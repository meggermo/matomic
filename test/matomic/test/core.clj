(ns matomic.test.core
  (:use 
   [matomic.core]
   [midje.sweet]))

(def foo "foo")
(def bar "bar")
(def baz "baz")
(def zap "zap")
(def m (zipmap (iterate inc 0) [foo bar baz]))

(tabular 
  (fact "assoc-when"
    (assoc-when m ?keys (constantly zap)) => ?result) 
    ?keys        ?result 
    [0]          {0 zap 1 bar 2 baz} 
    [1]          {0 foo 1 zap 2 baz} 
    [2]          {0 foo 1 bar 2 zap} 
    [0 1]        {0 zap 1 zap 2 baz})

