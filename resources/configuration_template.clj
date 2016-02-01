{:min-confidence min-confidence, 
 :chunk-size chunk-size,
 :ngram-count ngram-count,
 :source-file source-file,
 :source-tags-filtered  '(:stage :castList :teiHeader :speaker :head :front),
 :target-file target-file,
 :target-file-filtered '(:stage :castList :teiHeader :speaker :front),
 :visualization-framework "Neo4j" ,
  ; set of parameters specific to a given visualization framework
 :visualization-framework-parameters {
  :connection-string "http://YOURLOGIN:YOURPASSWORD@localhost:7474/db/data/"
  }
 }
