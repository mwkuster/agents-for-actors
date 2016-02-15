(defproject agents-for-actors "0.8.0-SNAPSHOT"
  :description "Agents for actors, a Digital Humanities framework for distributed, workflow-driven microservices for text analysis and visualization"
  :url "https://github.com/mwkuster/agents-for-actors"
  :license {:name "GNU Lesser Public License"
            :url "http://www.gnu.org/copyleft/lesser.html"
            :comments "Author: Marc Wilhelm Küster"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.logging "0.2.3"]
                 [clojurewerkz/neocons "3.1.0"]
                 [cheshire "5.5.0"]
                 [incanter "1.3.0"]]
  :main agents-for-actors.core
  :jvm-opts ["-Xmx7g"])

