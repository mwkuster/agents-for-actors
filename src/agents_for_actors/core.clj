(ns agents-for-actors.core
  (:require [agents-for-actors.xml :as x]
            [clojure.zip :as z]
            [clojure.string :as s]
            [agents-for-actors.similitude :as sim]
            [agents-for-actors.ngram :as ngram]
            [agents-for-actors.neo4j :as neo4j]))

(def ^:dynamic *parameters* 
  {:visualization-framework "Neo4j"})

(defn -main 
  "Start Agents for Actors with a few sample values. No error handling yet at this level" 
  [min-confidence chunk-size ngram-count source-file target-file load-to-db] 
  (let
      [src (x/read-xml source-file)
       source-filtered (x/extract-text-nodes src '(:stage :castList :teiHeader :speaker :head :front))
       target (x/read-xml target-file)
       target-filtered (x/extract-text-nodes target '(:stage :castList :teiHeader :speaker :front))
       ag (if (= load-to-db "true")
            (do
              (neo4j/neo4j-init)
              (neo4j/bulk-load source-filtered target-filtered source-file target-file)))]
    (println "<results>")
    (println x/parameters-tostr 
             {:min-confidence min-confidence, 
              :chunk-size chunk-size,
              :ngram-count ngram-count,
              :source-file source-file,
              :target-file target-file})
    (doall 
     (for [s (flatten ;Caution: map alone would build a list of lists
              (pmap 
               #(sim/find-similarities 
                 (fn [p1 p2] 
                   (ngram/ngram-similarity (Integer/parseInt chunk-size) 
                                           (Integer/parseInt ngram-count) p1 p2))
                 (Double/parseDouble min-confidence)
                 %
                 source-filtered)
               target-filtered))
           :when (not (empty? s)) ;explicitly exclude those runs that returned nil
           ]
       (do
         (if (= load-to-db "true")
           (do
             (await ag)
             (neo4j/load-link (:source s) (:phrase s))))
         (println "<result confidence='" (:confidence s) "'><phrase>" (x/loc-tostr (:phrase s)) "</phrase><source>" (x/loc-tostr (:source s)) "</source></result>")
       )))
    (println "</results>")))
    


