(ns agents-for-actors.neo4j
  (:require [clojurewerkz.neocons.rest :as nr]
            [clojurewerkz.neocons.rest.nodes :as nn]
            [clojurewerkz.neocons.rest.relationships :as nrl]
            [clojurewerkz.neocons.rest.cypher :as cy]
            [agents-for-actors.xml :as x]
            [clojure.zip :as z]))

(def ^:dynamic *connection-string* "http://localhost:7474/db/data/")
(def ^:dynamic *root*)

(defn get-by-xptr [xptr]
  (nn/find-one "xptr" :xptr xptr))

(defn get-link-by-xptr [link-id]
  (nrl/find-one "link-by-xptr" :link-text link-id))

(defn create-source [source-name]
  (let
      [n (get-by-xptr source-name)
       src-node 
       (if n
         n
         (nn/create {:name source-name :xptr source-name}))
       link-id (str "root:root->" source-name)]
    (if (not n)
      (nn/add-to-index (:id src-node) "xptr" :xptr source-name true))
    (if (not (get-link-by-xptr link-id))
      (let
          [rel (nrl/create *root* src-node :root {:link-text link-id})]
        (nrl/add-to-index (:id rel)  "link-by-xptr" :link-text link-id)))
    src-node))

(defn neo4j-init [source-name target-name]
  (nr/connect! *connection-string*)
  (def ^:dynamic *root* (nn/get 0))
  (if (not-any? #(= "xptr" (:name %)) (nn/all-indexes))
    (nn/create-index "xptr" {:unique true}))
  (if (not-any? #(= "link-by-xptr" (:name %)) (nrl/all-indexes))
    (nrl/create-index "link-by-xptr"))
  (create-source source-name)
  (create-source target-name))


(defn load-location [src-node loc link-type]
  "Take a location and load it as objects to Neo4j, if it does not yet exist. Link it to its source node"
  (let 
      [xptr (x/xpointer-tostr loc)
       src-xptr (:xptr (:data src-node))
       n (get-by-xptr xptr)
       new-node  (if n
                   n
                   (nn/create {:xptr xptr :name (z/node loc)}))
       link-id (str link-type ":" src-xptr "->" xptr)]
    (if (not n)
      (nn/add-to-index (:id new-node) "xptr" "xptr" xptr))
    (if (not (get-link-by-xptr link-id))
      (let
          [new-rel (nrl/create src-node new-node link-type {:link-text link-id})]
        (nrl/add-to-index (:id new-rel)  "link-by-xptr" :link-text link-id)))
        ))