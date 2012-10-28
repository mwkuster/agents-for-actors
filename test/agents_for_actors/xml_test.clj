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
     (= (xml-ancestors (nth locs 6)) [nil :l :sp :body :text :TEI]))
    (is
     (= (xpointer (nth locs 6)) [{:position 1 :tag nil} 
                                 {:position 6 :tag :l} 
                                 {:position 2 :tag :sp}
                                 {:position 1 :tag :body}
                                 {:position 1 :tag :text}
                                 {:position 1 :tag :TEI}]))
    )))

        
        