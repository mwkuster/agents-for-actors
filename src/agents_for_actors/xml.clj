(ns agents-for-actors.xml
  (:require [clojure.xml :as xml]
            [clojure.zip :as z]
            [clojure.set :as set]))

(defn ancestors 
  "Return the tag symbols for the ancestors of the current node-location"
  ([node-loc]
     (ancestors node-loc []))
  ([node-loc ancestor-seq]
     (if (nil? node-loc)
       ancestor-seq
       (recur (z/up node-loc) 
              (conj ancestor-seq (:tag (z/node node-loc)))))))

(defn extract-text-nodes [xml-zipper filter-tags]
 "Do a depth-first traversal of the zipper, representing an XML tree
structure, extracting a sequence of zip locations representing text nodes. The location allows to reconstruct the exact location of this text node in the original XML document"
  (let
      [filter-set (set filter-tags)
       traversal
       (fn [loc result-list]
         (if (z/end? loc)
           result-list
           (cond
            (> (count (set/intersection (set (ancestors loc)) filter-set)) 0) (recur (z/next loc)  result-list) ;we are in a text node that is a descendant of a tag that is being filtered
            (z/branch? loc) (recur (z/next loc)  result-list) ;a branch node cannot contain text
            :else (recur (z/next loc) (conj result-list  loc)))))]
    (traversal xml-zipper [])))


(defn read-xml [filename]
  "Read an XML file and optionally filter the contents of certain tags
from the document (e.g. comments, stage directions, metadata. Returns
a sequence of strings (= element contents) minus the text in the
filter-tags"
  (let
      [xml-zip (z/xml-zip (xml/parse filename))]
    xml-zip))
    
    