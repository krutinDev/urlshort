(ns urlshort.server.db
  (:require [korma.db :refer [defdb postgres]]
            [korma.core :refer [defentity]]))

(defn getenv [k default]
  (or (System/getenv k) default))

(defdb db
  (postgres {:db       (getenv "DB_NAME" "urlshort_db")
             :user     (getenv "DB_USER" "dbuser")
             :password (getenv "DB_PASS" "dbpass")
             :host     (getenv "DB_HOST" "localhost")
             :port     (Integer/parseInt (getenv "DB_PORT" "5432"))}))

(defentity urls)