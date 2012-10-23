(ns agents-for-actors.ngram
  (:require [clojure.string :as cs]
            [clojure.set :as set]))

(def padding "_")

(defn ngrams [^Number n ^String inputs] 
  "Compute a list of ngrams for a given input string"
  (let
      [padd (cs/join (for [i (range 0 (- n 1))] padding))
       in-str (str padd (cs/replace inputs #"\s+" padding) padd)]
    (map cs/join (partition n 1 in-str)))
  )

(defn quadgrams [^String inputs] 
  (ngrams 4 inputs))

(defn dice-coefficient ^Double [seq1 seq2] 
  "Calculate the dice coefficient for two sequences"
  (let
      [set1 (set seq1)
       set2 (set seq2)]
    (* 2.0 (/ (count (set/intersection set1 set2)) (+ (count set1) (count set2))))))