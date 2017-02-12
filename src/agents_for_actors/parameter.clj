(ns agents-for-actors.parameter)

(def ^:dynamic *parameters* 
  {:visualization-framework "Neo4j"})

(defn set-parameters [par-map]
  (def ^:dynamic *parameters* par-map))