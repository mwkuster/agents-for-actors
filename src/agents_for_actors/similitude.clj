(ns agents-for-actors.similitude
  (:require [clojure.string :as cs]
            [clojure.zip :as z]
            [agents-for-actors.ngram :as ngram]))

(defn find-similarities [phrase-loc source-seq similar?]
  "Find similarities for a given phrase (= a location) in a source (=
a sequence of locations) and return a sequence of maps of source and
phrase locations pointing to those similarities. Use a given
predicate (e.g. quadgram coupled with a dice coefficient with cutoff)
for comparision"
  (let
      [res (for [p-loc source-seq :when (similar? (z/node p-loc) (z/node phrase-loc))] {:phrase phrase-loc :source p-loc})]
    (filter #(not (empty? %)) res)))
      

(defn similar-phrase? ^Boolean [^Double cut-off ^Integer word-count ^Integer ngram-count ^String phrase1 ^String phrase2]
  "This specific definition of similarity builds on ngram and
considers all phrases to be similar that have a similarity measured by
dice coefficient > cut-off, using ngram-count grams based on phrases
maximally word-count long"
  ;(println "similar-phrase? called" phrase1 phrase2)
  (let
      [tok-seq1 (ngram/generate-token-sequences phrase1 word-count)
       tok-seq2 (ngram/generate-token-sequences phrase2 word-count)]
    (if 
       (and (not-any? empty? [phrase1 phrase2])
            (some 
             (fn [[t1 t2]]
               (> 
                (ngram/dice-coefficient 
                 (ngram/ngrams ngram-count t1)
                 (ngram/ngrams ngram-count t2))
                cut-off))
             (for [tok1 tok-seq1 tok2 tok-seq2] [tok1 tok2])))
            true
            false)))
        
