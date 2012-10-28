(ns agents-for-actors.core
  (:require [agents-for-actors.xml :as x]
            [clojure.zip :as z]
            [clojure.string :as s]
            [agents-for-actors.similitude :as sim]
            [agents-for-actors.ngram :as ngram]))

(defn -main 
  "Start Agents for Actors with a few sample values. No error handling yet at this level" 
  [min-confidence chunk-size ngram-count source-file target-file] 
  (let
      [first-folio (x/read-xml source-file)
       first-folio-filtered (x/extract-text-nodes first-folio '(:stage :castList :teiHeader :speaker :head :front))
       transcript (x/read-xml target-file)
       transcript-filtered (x/extract-text-nodes transcript '(:stage :castList :teiHeader :speaker :front))]
    (println "<results>")
    (println "<min-confidence>" min-confidence "</min-confidence>")
    (println "<chunk-size>" chunk-size "</chunk-size>")
    (println "<ngram-count>" ngram-count "</ngram-count>")
    (println "<source-file>" source-file "</source-file>")
    (println "<target-file>" source-file "</target-file>")
    (doall 
     (for [s (flatten ;Caution: map alone would build a list of lists
              (pmap 
               #(sim/find-similarities 
                 (fn [p1 p2] 
                   (ngram/ngram-similarity (Integer/parseInt chunk-size) 
                                           (Integer/parseInt ngram-count) p1 p2))
                 (Double/parseDouble min-confidence)
                 %
                 first-folio-filtered)
               transcript-filtered))
           :when (not (empty? s)) ;explicitly exclude those runs that returned nil
           ]
       (println "<result confidence='" (:confidence s) "'><phrase>" (x/loc-tostr (:phrase s)) "</phrase><source>" (x/loc-tostr (:source s)) "</source></result>")
       ))
    (println "</results>")))
    


