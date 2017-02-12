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
  (println "Starting up" (new java.util.Date))
  (par/set-parameters (load-file conf-file))
  (let
      [source-file (:source-file par/*parameters*)
       src (x/read-xml source-file)
       source-filtered (x/extract-text-nodes src (:source-tags-filtered par/*parameters*))
       target-file (:target-file par/*parameters*)
       input-equals-output? (= source-file target-file)
       target (if input-equals-output? ;Optimization for the case that input file and output file are identical
                src
                (x/read-xml target-file))
       target-filtered (if input-equals-output?
         source-filtered
         (x/extract-text-nodes target (:target-tags-filtered par/*parameters*)))
       chunk-size (:chunk-size par/*parameters*)
       ngram-count (:ngram-count par/*parameters*)
       min-confidence (:min-confidence par/*parameters*)
       visualization-agent (agent (z/root src))
       result-fn  (str (:result-file par/*parameters*) "." (quot (System/currentTimeMillis) 1000))
       xml-agent (agent result-fn)
       xml (fn[result-file msg] (spit result-file (str msg "\n") :append true) result-file)
       weight-fn ; a weight function is supposed to take two strings and return a metric between 0 (no match) and 1 (identity)
       (if (contains?  par/*parameters* :weight-fn)
         (eval (:weight-fn par/*parameters*))
         (fn [p1 p2]  
           (ngram/ngram-similarity chunk-size
                                   ngram-count p1 p2)))]
    
    (vis/initialize source-file target-file)
    (doseq
        [loc source-filtered]
      (send-off visualization-agent vis/visualize source-file loc :part-of))
    (doseq
        [loc target-filtered]
      (send-off visualization-agent vis/visualize target-file loc :part-of))
    
    (send xml-agent xml "<results>")
    (send xml-agent xml (x/parameters-tostr par/*parameters*))
    (let
        [link-agents (map #(agent %) target-filtered)]
      (doseq
          [ag link-agents]
        (send ag (fn[target-line]
                   (let [links
                         (sim/find-similarities
                            weight-fn
                            min-confidence
                            target-line
                            source-filtered
                            input-equals-output?)]
                     (doseq
                         [s links
                          :when (not (empty? s))]
                       (send visualization-agent vis/visualize 
                                 (x/xpointer-tostr (:phrase s))
                                 (:source s) :cites)
                       (send xml-agent xml (str "<result confidence='" (:confidence s) "'><phrase hit='" (:t2 s) "'>" (x/loc-tostr (:phrase s)) "</phrase><source hit='" (:t1 s) "'>" (x/loc-tostr (:source s)) "</source></result>"))))
                     nil)))
      (doall (map #(await %) link-agents)))
   
    (send xml-agent xml "</results>")
    (send-off visualization-agent vis/finalize source-file)
    (println "Preparing for shutdown" (new java.util.Date))
    (if (agent-error visualization-agent)
      (println "visualization-agent: " (agent-error visualization-agent))
      (await visualization-agent))
    (if (agent-error xml-agent)
      (println "xml-agent: " (agent-error xml-agent))
      (await xml-agent))
    (println "Shutdown")
    (shutdown-agents)))
