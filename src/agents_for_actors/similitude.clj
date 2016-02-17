(ns agents-for-actors.similitude
  (:require [clojure.string :as cs]
            [clojure.zip :as z]))

(defn- part-after-phrase-loc [phrase-loc source-seq]
  "If input and output files are identical, the phrase will be part of the source sequence. To avoid parsing the same lines twice, ignore all lines before the current phrase (optimization). If the last line has been reached, return nil"
  (let
      [remainder (partition-by #(= % phrase-loc) source-seq)]
    (if (= (count remainder) 3)
      (last remainder)
      nil)))


(defn find-similarities
  ([similarity-fn ^Double min-confidence phrase-loc source-seq]
   (find-similarities similarity-fn min-confidence phrase-loc source-seq false))
  ([similarity-fn ^Double min-confidence phrase-loc source-seq input-equals-output?] 
  "Find similarities for a given phrase (= a location) in a source (=
a sequence of locations) and return a sequence of maps of source and
phrase locations pointing to those similarities, including the
respective confidence. Use a given function (e.g. quadgram coupled
with a dice coefficient) that returns a confidence in [0..1] for
comparision. The provided similarity function must return a map with at least the keys :weight, :phrase, :source, :confidence, :t1 (for the best hit in the first phrase) and :t2 (for the best hit in the second phrase). the function returns a list of non-empty such maps with a weight above min-confidence"
  (let
      [source-seq-reduced
       (if input-equals-output?
         (part-after-phrase-loc phrase-loc source-seq)
         source-seq)
       res (for [p-loc source-seq-reduced 
                 :let [best-hit (similarity-fn (z/node p-loc) (z/node phrase-loc))
                       confidence (:weight best-hit)]
                 :when (> confidence min-confidence)]
             (do
               {:phrase phrase-loc :source p-loc :confidence confidence :t1 (:t1 best-hit) :t2 (:t2 best-hit)}))]
       (filter #(not (empty? %)) res))))
        
