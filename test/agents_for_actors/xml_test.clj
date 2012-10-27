(ns agents-for-actors.xml-test
  (:require [clojure.zip :as z])
  (:use clojure.test
        agents-for-actors.xml))

(deftest xml-test
  (testing "XML functionality"
    (let
        [xml (read-xml "test/agents_for_actors/transcript.xml")
         locs (extract-text-nodes xml '(:stage  :speaker :teiHeader :front))]
    (is 
     (= (count locs) 24))
    (is
     (= (z/node (nth locs 6)) "I pray to Heaven we fare well,"))
    (is
     (= (ancestors (nth locs 6)) [nil :l :sp :body :text :TEI])))))

        
        