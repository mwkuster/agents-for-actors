(ns agents-for-actors.visualization
  (:require [agents-for-actors.xml :as x]
            [agents-for-actors.neo4j :as neo4j]
            [agents-for-actors.excel :as excel]
            [agents-for-actors.parameter :as par])
  (:use [clojure.tools.logging :only (info error)]))

(defmulti initialize
  "Initialize the visualization subsystem"
  (fn [src-name target-name]
    (:visualization-framework par/*parameters*)))

(defmethod initialize "None" [src-name target-name]
  nil)

(defmethod initialize "Neo4j" [src-name target-name]
  (neo4j/neo4j-init src-name target-name))

(defmethod initialize "Excel" [src-name target-name]
  (excel/excel-init src-name target-name))

(defmulti visualize 
  "Visualize given connections using a suitable visualization framework"
  (fn [prev-loc src-name location link-type]
    (:visualization-framework par/*parameters*)))

(defmethod visualize "None" [prev-loc src-name location link-type]
  location)

(defmethod visualize "Neo4j" [prev-loc src-name location link-type]
  "Build in neo4j a link of type link-type between a src node,
identified by its xptr, and a target node, identified as a
location. The target node is if necessary created"
  (let
      [src-node (neo4j/get-by-xptr src-name)]
    (info "Handling " (x/xpointer-tostr location))
    (neo4j/load-location src-node location link-type)
    location))

(defmethod visualize "Excel" [prev-loc src-name location link-type]
  "Store in an Excel file a row with all information on links"
  (info "Handling " (x/xpointer-tostr location))
  (excel/add-row src-name location link-type))

(defmulti finalize 
  "Finalize the connection"
  (fn [prev-loc src-name]
    (:visualization-framework par/*parameters*)))

(defmethod finalize "None" [prev-loc src-name]
  nil)

(defmethod finalize "Neo4j" [prev-loc src-name]
  nil)

(defmethod finalize "Excel" [prev-loc src-name]
  (excel/save-file src-name))

