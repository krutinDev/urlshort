(ns urlshort.server.service
  (:require [korma.core :as k]
            [urlshort.server.db :refer [urls]]
            [urlshort.server.slug :as slug]))

(defn find-by-slug [s]
  (first (k/select urls (k/where {:slug s}))))

(defn- unique-slug []
  (loop [s (slug/rand-slug 6)]
    (if (nil? (find-by-slug s))
      s
      (recur (slug/rand-slug 6)))))

(defn create! [long-url]
  (let [s (unique-slug)]
    (k/insert urls (k/values {:slug s :long_url long-url}))
    s))

(defn update! [s new-url]
  (pos? (k/update urls
                  (k/set-fields {:long_url new-url})
                  (k/where {:slug s}))))

(defn delete! [s]
  (pos? (k/delete urls (k/where {:slug s}))))