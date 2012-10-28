(ns agents-for-actors.core
  (:require [agents-for-actors.xml :as x]
            [clojure.zip :as z]
            [agents-for-actors.similitude :as sim]))

(defn -main 
  "Start Agents for Actors with a few sample values. No error handling yet at this level" 
  [cut-off word-count ngram-count ] 
  (let
      [first-folio (x/read-xml "/Users/marcwilhelmkuster/sandbox/playground/blackadder/3014.xml")
       first-folio-filtered (x/extract-text-nodes first-folio '(:stage :castList :teiHeader :speaker :head :front))
       transcript (x/read-xml "/Users/marcwilhelmkuster/sandbox/playground/blackadder/transcript.xml")
       transcript-filtered (x/extract-text-nodes transcript '(:stage :castList :teiHeader :speaker :front))]
    (println "cut-off value: " cut-off)
    (println "word-count: " word-count)
    (println "ngram-count: " ngram-count)

    (doall 
     (for [s (map
              #(sim/find-similarities 
                %
                first-folio-filtered 
                (fn [p1 p2] 
                  (sim/similar-phrase? (Double/parseDouble cut-off) 
                                      (Integer/parseInt word-count) 
                                      (Integer/parseInt ngram-count) p1 p2)))
              transcript-filtered)
           :when (not (empty? s)) ;explicitly exclude those runs that returned nil
           ]
       (do
         (println s)
         (println (z/node (:phrase s)) (z/node (:source s))))))))
      ; (println s)))))
    


