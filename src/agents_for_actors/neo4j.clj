(ns agents-for-actors.neo4j
  (:require [clojurewerkz.neocons.rest :as nr]
            [clojurewerkz.neocons.rest.nodes :as nn]
            [clojurewerkz.neocons.rest.relationships :as nrl]
            [clojurewerkz.neocons.rest.cypher :as cy]
            [agents-for-actors.xml :as x]
            [clojure.zip :as z]))

(def ^:dynamic *root*)
(def ^:dynamic *conn*)

(defn get-by-xptr [xptr]
  (nn/find-one *conn* "xptr" "xptr" xptr))

(defn get-link-by-xptr [link-id]
  (nrl/find-one *conn* "link-by-xptr" "link-text" link-id))

(defn create-source [source-name]
  (let
      [n (get-by-xptr source-name)
       src-node 
       (if n
         n
         (nn/create  *conn* {:name source-name :xptr source-name}))
       link-id (str "root:root->" source-name)]
    (if (not n)
      (nn/add-to-index *conn* (:id src-node) "xptr" "xptr" source-name true))
    (if (not (get-link-by-xptr link-id))
      (let
          [rel (nrl/create *conn* *root* src-node :root {:link-text link-id})]
        (nrl/add-to-index *conn* (:id rel)  "link-by-xptr" "link-text" link-id)))
    src-node))

(defn neo4j-init [source-name target-name framework-params]
  (let
      [conn (nr/connect (:connection-string framework-params))
       root   (nn/create conn {:root "root"})]
    (def ^:dynamic *root* root)
    (def ^:dynamic *conn* conn)
    (if (not-any? #(= "xptr" (:name %)) (nn/all-indexes conn))
      (nn/create-index conn "xptr" {:unique true}))
    (if (not-any? #(= "link-by-xptr" (:name %)) (nrl/all-indexes conn))
      (nrl/create-index conn "link-by-xptr"))
    (create-source source-name)
    (create-source target-name)))


(defn load-location [src-node loc link-type]
  "Take a location and load it as objects to Neo4j, if it does not yet exist. Link it to its source node"
  (let 
      [xptr (x/xpointer-tostr loc)
       src-xptr (:xptr (:data src-node))
       n (get-by-xptr xptr)
       new-node  (if n
                   n
                   (nn/create *conn* {:xptr xptr :name (z/node loc)}))
       link-id (str link-type ":" src-xptr "->" xptr)]
    (if (not n)
      (nn/add-to-index *conn* (:id new-node) "xptr" "xptr" xptr))
    (if (not (get-link-by-xptr  link-id))
      (let
          [new-rel (nrl/create *conn* src-node new-node link-type {:link-text link-id})]
        (nrl/add-to-index *conn* (:id new-rel)  "link-by-xptr" :link-text link-id)))
        ))

(defn finalize []
  "Print out the results found and then close the connection to Neo4j"
  (let
      [res
       (cy/tquery *conn* "MATCH ()-[r:`cites`]->() RETURN r")]
    (println res)
    (def ^:dynamic *conn* nil)))
