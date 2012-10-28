(defproject agents-for-actors "0.1.0-SNAPSHOT"
  :description "Agents for actors, a Digital Humanities framework for distributed, workflow-driven microservices for text analysis and visualization"
  :url "https://github.com/mwkuster/agents-for-actors"
  :license {:name "GNU Lesser Public License"
            :url "http://www.gnu.org/copyleft/lesser.html"
            :comments "Author: Marc Wilhelm Küster"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [clojurewerkz/neocons "1.1.0-beta1"]]
  :main agents-for-actors.core
  :jvm-opts ["-Xmx1g"])
