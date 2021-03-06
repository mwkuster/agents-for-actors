agents-for-actors
=================

Agents for Actors (AfA), a Digital Humanities framework for distributed microservices for text analysis and visualization

## Why?
Agents for Actors were born out of the desire to find fuzzy links and allusions between literary texts and their sources. 

## Usage

Agents for Actors can be called directly on the command line, using as only command line parameter the path to a configuration file that is simply a Clojure map (cf. resources/configuration_template.clj for an example):

```clojure
{:min-confidence 0.65, 
 :chunk-size 6,
 :ngram-count 4,
 :source-file "first-folio.xml",
 :source-tags-filtered  '(:stage :castList :teiHeader :speaker :head :front),
 :target-file "transcript.xml",
 :target-tags-filtered '(:stage :castList :teiHeader :speaker :front),
 :visualization-framework "Neo4j",
 ; set of parameters specific to a given visualization framework
 :visualization-framework-parameters {
  :connection-string "http://YOURLOGIN:YOURPASSWORD@localhost:7474/db/data/"
  }
 :result-file "res1.xml"}
```

(this sample assumes that an instance of neo4j is running under http://localhost:7474 with suitable authentication information)


This calls a double NGram algorithm for the comparison of source and target fragments. Since the architecture is pluggable, you can also experiment with any other similarity algorithm and measure.

## Visualization
Per default AfA uses the Open Source Neo4j graph database to visualize a dependency graph:

<img src="https://raw.github.com/mwkuster/agents-for-actors/master/doc/neo4j_screenshot.png" title="Screenshot of Neo4j with citations" align="left" padding="5px" />

Other visualization mechanisms (including none) can be added by adding specialized methods to the multimethods initialize and visualize:
```clojure
(defmulti initialize
  "Initialize the visualization subsystem"
  (fn [src-name target-name]
    (:visualization-framework par/*parameters*)))

(defmulti visualize 
  "Visualize given connections using a suitable visualization framework"
  (fn [prev-loc src-name location link-type]
    (:visualization-framework par/*parameters*)))
```

The current version of AfA has been updated to work with Neo4j version 2.3.2.


## Vagrant and Docker
In order to facilitate the deployment of agents for actors a Vagrant file loads all the necessary dependencies into a Virtualbox.

As a prerequisite you need to install vagrant (https://www.vagrantup.com/) and Virtualbox. For recent distributions of GNU/Linux it is typically part of the package repository. Once installed use 

```
vagrant up
```
to provision and start Virtualbox --- this can take a while and can download quite a few GBs of data, so use a fast line and have a coffee ready...


Inside the Virtualbox (vagrant ssh) you can start up a Docker instance with the latest version of neo4j and launch agents for actors directly:
```
vagrant ssh

cd agents-for-actors
docker-compose up -d neo4j 
lein run [your configuration file]
```

The docker instance of Neo4j is configured to run without authentication - caveat emptor!

## License

AfA is released under the GNU Lesser Public Licence (http://www.gnu.org/copyleft/lesser.html)



