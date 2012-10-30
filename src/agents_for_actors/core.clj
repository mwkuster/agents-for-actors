(ns agents-for-actors.core
  (:require [agents-for-actors.xml :as x]
            [clojure.zip :as z]
            [clojure.string :as s]
            [agents-for-actors.similitude :as sim]
            [agents-for-actors.parameter :as par]
            [agents-for-actors.ngram :as ngram]
            [agents-for-actors.neo4j :as neo4j]
            [agents-for-actors.visualization :as vis]))

(defn -main 
  "Start Agents for Actors with a few sample values. No error handling yet at this level" 
  [conf-file] 
  (par/set-parameters (load-file conf-file))
  (let
      [source-file (:source-file par/*parameters*)
       src (x/read-xml source-file)
       source-filtered (x/extract-text-nodes src (:source-tags-filtered par/*parameters*))
       target-file (:target-file par/*parameters*)
       target (x/read-xml target-file)
       target-filtered (x/extract-text-nodes target (:target-tags-filtered par/*parameters*))
       chunk-size (:chunk-size par/*parameters*)
       ngram-count (:ngram-count par/*parameters*)
       min-confidence (:min-confidence par/*parameters*)
       visualization-agent (agent (z/root src))
       log-agent (agent (:result-file par/*parameters*))
       log (fn[result-file msg] (spit result-file msg :append true) result-file)]
    
    (vis/initialize source-file target-file)
    (doseq
        [loc source-filtered]
        (send visualization-agent vis/visualize source-file loc :part-of))
    (doseq
        [loc target-filtered]
        (send visualization-agent vis/visualize target-file loc :part-of))
    
    
    (send log-agent log "<results>")
    (send log-agent log (x/parameters-tostr par/*parameters*))
    (doall 
     (for [s (flatten ;Caution: map alone would build a list of lists
              (pmap 
               #(sim/find-similarities 
                 (fn [p1 p2] 
                   (ngram/ngram-similarity chunk-size
                                           ngram-count p1 p2))
                 min-confidence
                 %
                 source-filtered)
               target-filtered))
           :when (not (empty? s)) ;explicitly exclude those runs that returned nil
           ]
       (do
         (send visualization-agent vis/visualize 
               (x/xpointer-tostr (:source s))
               (:phrase s) :cites)
         (send log-agent log (str "<result confidence='" (:confidence s) "'><phrase>" (x/loc-tostr (:phrase s)) "</phrase><source>" (x/loc-tostr (:source s)) "</source></result>"))
       )))
    (send log-agent log "</results>")
    (shutdown-agents)))
