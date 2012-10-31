(ns agents-for-actors.neo4j
  (:require [clojurewerkz.neocons.rest :as nr]
            [clojurewerkz.neocons.rest.nodes :as nn]
            [clojurewerkz.neocons.rest.relationships :as nrl]
            [clojurewerkz.neocons.rest.cypher :as cy]
            [agents-for-actors.xml :as x]
            [clojure.zip :as z]))

(def ^:dynamic *connection-string* "http://localhost:7474/db/data/")
(def ^:dynamic *root*)

(defn create-source [source-name]
  (let
      [src-node (nn/create {:name source-name :xptr source-name})]
    (nrl/create *root* src-node :root)
    (nn/add-to-index (:id src-node) "xptr" "xptr" source-name true)
    src-node))

(defn neo4j-init [source-name target-name]
  (nr/connect! *connection-string*)
  (def ^:dynamic *root* (nn/get 0))
  (if (not-any? #(= "xptr" (:name %)) (nn/all-indexes))
    (nn/create-index "xptr" {:unique true}))
  (create-source source-name)
  (create-source target-name))

(defn get-by-xptr [xptr]
  (nn/find-one "xptr" "xptr" xptr))

(defn load-location [src-node loc link-type]
  "Take a location and load it as objects to Neo4j, if it does not yet exist. Link it to its source node"
  (let 
      [xptr (x/xpointer-tostr loc)
       n (get-by-xptr xptr)
       new-node  (if n
                   n
                   (nn/create {:xptr xptr :name (z/node loc)}))]
    (nn/add-to-index (:id new-node) "xptr" "xptr" xptr)
    (nrl/create src-node new-node link-type)))

;; (defn get-node-for-location [loc]
;;   "Retrieve the Neo4j node for a location (if existing) and return it"
;;   (let
;;       [q (str "start n=node(*) where has(n.xptr) and n.xptr=\"" (x/xpointer-tostr loc) "\" return id(n)")
;;        query-res (cy/tquery q)]
;;     ;(println q)
;;     (assert (= (count query-res) 1))
;;     (nn/get (get (first query-res) "id(n)"))))

;; (defn load-link [src-loc tgt-loc]
;;   "Establish a link between a source and a target"
;;   (let
;;       [src-node (get-node-for-location src-loc)
;;        tgt-node (get-node-for-location tgt-loc)]
;;     (nrl/create  tgt-node src-node :cites)))

;; (defn bulk-load [src-loc-seq tgt-loc-seq src-name target-name]
;;   "Bulk load a sequence of source and target sequences into Neo4j,
;; creating also nodes representing the source and target files"
;; ;TODO: since the bulk load is a very expensive operation, check first if the src and target nodes do not exist
;;   (let
;;       [tr-src (create-source target-name)
;;        ff-src (create-source src-name)
;;        visualization-agent (agent *root*)
;;        ll-fn (fn [prev src-node loc]
;;                (load-location src-node loc))]
;;     (doseq 
;;         [loc tgt-loc-seq]
;;         (send visualization-agent ll-fn tr-src loc) )
;;     (doseq 
;;         [loc src-loc-seq]
;;         (send visualization-agent ll-fn ff-src loc))
;;     visualization-agent))
