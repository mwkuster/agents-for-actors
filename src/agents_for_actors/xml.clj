(ns agents-for-actors.xml
  (:require [clojure.xml :as xml]
            [clojure.zip :as z]
            [clojure.string :as s]
            [clojure.set :as set]))

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
                        [tag (:tag (z/node node-loc)),
                         same-tag? 
                         (fn [tst-tag] 
                           (= (:tag tst-tag) tag))]
                      {:position 
                       ;Caution: z/lefts returns a list of nodes, not a list of locations!
                       (+ 1 (count (filter same-tag? (z/lefts node-loc)))) 
                       :tag tag})
                    )))))

(defn xpointer-tostr [node-loc]
  "Return a string version of an XPointer in compliance with the W3C XPointer recommendation"
  (let
      [xpath (s/join "/" (map  #(if (nil? (:tag %))
                                  (str "text()[" (:position %) "]")
                                  (str (subs (str (:tag %)) 1) "[" (:position %) "]"))
                     (reverse (xpointer node-loc))))]
    (str "xpointer(/" xpath ")")))

(defn parameters-tostr [parameter-map]
  "Take a parameter map and return it as XML"
  (str
   "<parameters>"
   (s/join
    (for
        [k (keys parameter-map)]
      (let
          [key-to-tag (subs (str k) 1)]
        (str "<" key-to-tag ">" (get parameter-map k) "</" key-to-tag ">\n"))))
   "</parameters>"))

(defn xml-ancestors 
  "Return the tag symbols for the xml-ancestors of the current node-location"
  [node-loc]
  (let
      [xptr (xpointer node-loc)]
    (map :tag xptr)))

(defn loc-tostr [node-loc]
  "Output a location node as XML fragment"
  (str "<location src='" (xpointer-tostr node-loc) "'>" (z/node node-loc) "</location>"))

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
    
    