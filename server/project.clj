(defproject urlshort-server "0.1.0-SNAPSHOT"
  :description "REST API for URL Shortening (Ring + Compojure + Korma)"
  :min-lein-version "2.9.8"
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [ring/ring-core "1.11.0"]
                 [ring/ring-jetty-adapter "1.11.0"]
                 [ring/ring-json "0.5.1"]
                 [compojure "1.7.1"]
                 [korma "0.4.3"]
                 [org.postgresql/postgresql "42.7.3"]]
  :main urlshort.server.core
  :profiles {:uberjar {:aot :all}}
  :uberjar-name "urlshort-server.jar")