(ns agents-for-actors.excel
  (:require [agents-for-actors.xml :as x]
            [clojure.zip :as z]))

(use '(incanter core charts excel))

(def ^:dynamic *excel-sheet* (atom {}))

(defn excel-init [source-name target-name]
  "Setup an Excel file"
  nil)

(defn add-row [src-name location link-type]
  "Add a row to a map that will later be saved to Excel"
  (let 
      [xptr (x/xpointer-tostr location)
       src-xptr (x/xpointer-tostr (:xptr (:data src-name)))]
                                        ; (println {:src-name src-xptr, :location xptr, :link-type link-type})
    (swap! *excel-sheet* conj {:src-name src-xptr, :location xptr, :link-type link-type})))

(defn save-file [source-name]
  "Save the save Excel information to file"
  ;(println @*excel-sheet*)
  (println source-name)
  ;(save-xls (to-dataset (get @*excel-sheet* source-name)) (str source-name ".xls")))
  (save-xls (to-dataset (get @*excel-sheet* source-name)) "/tmp/result.xls"))


