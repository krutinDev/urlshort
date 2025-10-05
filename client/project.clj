(defproject urlshort-client "0.1.0-SNAPSHOT"
  :description "CLI client for URL shortener"
  :min-lein-version "2.9.8"
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [clj-http "3.12.3"]
                 [cheshire "5.11.0"]]
  :main urlshort.client.core
  :profiles {:uberjar {:aot :all}}
  :uberjar-name "urlshort-client.jar")