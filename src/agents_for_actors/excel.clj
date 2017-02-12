(ns agents-for-actors.excel
  (:require [agents-for-actors.xml :as x]
            [clojure.zip :as z]))

(use '(incanter core charts excel))

(def ^:dynamic *excel-sheet* (atom '()))

(defn excel-init [source-name target-name]
  "Setup an Excel file"
  nil)

(defn add-row [src-name location link-type]
  "Add a row to a map that will later be saved to Excel"
  (let 
      [xptr (x/xpointer-tostr location)
       src-xptr (x/xpointer-tostr (:xptr (:data src-name)))]
    (swap! *excel-sheet* conj {:str-name src-name, :src-xptr src-xptr, :location xptr, :link-type link-type})))

(defn save-file [source-name]
  "Save the save Excel information to file"
  (println "Starting to save the Excel file")
  (save-xls (to-dataset @*excel-sheet*) "/tmp/result.xlsx"))


