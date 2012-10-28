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
  "Compute a list of quadgrams"
  (ngrams 4 inputs))

(defn dice-coefficient ^Double [seq1 seq2] 
  "Calculate the dice coefficient for two sequences"
  (let
      [set1 (set seq1)
       set2 (set seq2)]
    (* 2.0 (/ (count (set/intersection set1 set2)) (+ (count set1) (count set2))))))

(defn generate-token-sequences 
  "Take a list of tokens and build a string out of it that is up to
word_count words long. If permutate? is false, the last entry may
contain fewer than word-count elements. Permutate causes the input to
be created word_count times, each time with a different offset, but
all having word-count elements (cf. tests)"
  ([token-seq word-count permutate?]
     (let
         [seq (if (string? token-seq) (filter #(not (empty? %)) (cs/split token-seq #"[,;\.\s]")) token-seq)
          step (if permutate? 1 word-count)
          partition-fn (if permutate? partition partition-all)] 
       (map #(cs/join " " %) (partition-fn word-count step seq))))
  ([token-seq word-count]
     (generate-token-sequences token-seq word-count false)))


(defn ngram-similarity ^Double [^Integer chunk-size ^Integer ngram-count ^String phrase1 ^String phrase2]
  "This specific definition of similarity builds on ngram and
calculates the confidence for the similairty of two phrases, using
ngram-count grams based on phrases chunk-size long. All permutations
are compared"
  ;(println "similar-phrase? called" phrase1 phrase2)
  (let
      [tok-seq1 (generate-token-sequences phrase1 chunk-size true)
       tok-seq2 (generate-token-sequences phrase2 chunk-size true)]
    (if  (not-any? empty? [phrase1 phrase2 tok-seq1 tok-seq2])
      (apply max 
             (map
              (fn [[t1 t2]]
                (dice-coefficient 
                 (ngrams ngram-count t1)
                 (ngrams ngram-count t2)))
              (for [tok1 tok-seq1 tok2 tok-seq2] [tok1 tok2])))
      0.0)))