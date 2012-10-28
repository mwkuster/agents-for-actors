(ns agents-for-actors.similitude
  (:require [clojure.string :as cs]
            [clojure.zip :as z]))

(defn find-similarities [similarity-fn ^Double min-confidence phrase-loc source-seq]
  "Find similarities for a given phrase (= a location) in a source (=
a sequence of locations) and return a sequence of maps of source and
phrase locations pointing to those similarities, including the
respective confidence. Use a given function (e.g. quadgram coupled
with a dice coefficient) that returns a confidence in [0..1] for
comparision"
  (let
      [res (for [p-loc source-seq 
                 :let [confidence (similarity-fn (z/node p-loc) (z/node phrase-loc))]
                 :when (> confidence min-confidence)]
             (do
               (println confidence)
               {:phrase phrase-loc :source p-loc :confidence confidence}))]
       (filter #(not (empty? %)) res)))
      
        
