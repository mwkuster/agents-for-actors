(ns agents-for-actors.xml
  (:require [clojure.xml :as xml]
            [clojure.zip :as z]
            [clojure.set :as set]))

(defn xml-ancestors 
  "Return the tag symbols for the xml-ancestors of the current node-location"
  ([node-loc]
     (xml-ancestors node-loc []))
  ([node-loc ancestor-seq]
     (if (nil? node-loc)
       ancestor-seq
       (recur (z/up node-loc) 
              (conj ancestor-seq (:tag (z/node node-loc)))))))

(defn xpointer
  "Find the xpointer corresponding to this node-location. Returns a
sequence of {:position x :tag t} that can be used to build a textual
representation of an XPointer"
   ([node-loc]
     (xpointer node-loc []))
   ([node-loc ancestor-seq]
     (if (nil? node-loc)
       ancestor-seq
       (recur (z/up node-loc) 
              (conj ancestor-seq 
                    (let
                        [tag (:tag (z/node node-loc))
                         same-tag? 
                         (fn [tst-tag] 
                           (= (:tag tst-tag) tag))]
                      {:position 
                       (+ 1 (count (filter same-tag? (z/lefts node-loc)))) 
                       :tag tag})
                    )))))


(defn extract-text-nodes [xml-zipper filter-tags]
 "Do a depth-first traversal of the zipper, representing an XML tree
structure, extracting a sequence of zip locations representing text nodes. The location allows to reconstruct the exact location of this text node in the original XML document. 
To access the text node behind the location, access it via clojure.zip/node"
  (let
      [filter-set (set filter-tags)
       traversal
       (fn [loc result-list]
         (if (z/end? loc)
           result-list
           (cond
                                        ;we are in a text node that is a descendant of a tag that is being filtered
            (> (count (set/intersection (set (xml-ancestors loc)) filter-set)) 0) (recur (z/next loc)  result-list) 
                                        ;a branch node cannot contain text
            (z/branch? loc) (recur (z/next loc)  result-list) 
            ;this is a non-filtered leaf node, we add it to our result-list
            :else (recur (z/next loc) (conj result-list  loc)))))]
    (traversal xml-zipper [])))


(defn read-xml [filename]
  "Read an XML file and optionally filter the contents of certain tags
from the document (e.g. comments, stage directions, metadata. Returns
a sequence of strings (= element contents) minus the text in the
filter-tags"
  (z/xml-zip (xml/parse filename)))
    
    