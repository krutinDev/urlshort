(ns urlshort.server.core
  (:gen-class)
  (:require
   [compojure.core :refer [defroutes GET POST PUT DELETE]]
   [compojure.route :as route]
   [ring.adapter.jetty :refer [run-jetty]]
   [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
   [ring.middleware.params :refer [wrap-params]]
   [clojure.string :as str]
   [urlshort.server.service :as svc]))

;; helpers
(defn ok [body] {:status 200 :body body})
(defn created [body] {:status 201 :body body})
(defn bad [msg] {:status 400 :body {:error msg}})
(defn not-found [] {:status 404 :body {:error "Not found"}})

(defroutes routes
  ;; Каноничные REST-роуты
  (POST "/normal-url" req
    (let [url (get-in req [:body "url"])]
      (if (and url (not (str/blank? url)))
        (created {:slug (svc/create! url)})
        (bad "Provide JSON {\"url\":\"...\"}"))))

  (GET "/short-url/:slug" [slug]
    (if-let [row (svc/find-by-slug slug)]
      (ok {:url (:long_url row)})
      (not-found)))

  (PUT "/short-url/:slug" [slug :as req]
    (let [url (get-in req [:body "url"])]
      (cond
        (or (nil? url) (str/blank? url)) (bad "Provide JSON {\"url\":\"...\"}")
        (svc/update! slug url)           (ok {:ok true})
        :else                            (not-found))))

  (DELETE "/short-url/:slug" [slug]
    (if (svc/delete! slug)
      (ok {:ok true})
      (not-found)))

  ;; Суместимые alias-роуты под формулировку ТЗ
  ;; GET /short-url?slug=...
  (GET "/short-url" req
    (let [slug (or (get-in req [:params "slug"])
                   (get-in req [:body "slug"]))] ;; на всякий случай
      (if (and slug (not (str/blank? slug)))
        (if-let [row (svc/find-by-slug slug)]
          (ok {:url (:long_url row)})
          (not-found))
        (bad "Provide ?slug=... or body {\"slug\":\"...\"}"))))

  ;; PUT /short-url/normal-url  с телом {"slug":"...", "url":"..."}
  (PUT "/short-url/normal-url" req
    (let [slug (or (get-in req [:params "slug"])
                   (get-in req [:body "slug"]))
          url  (get-in req [:body "url"])]
      (cond
        (or (nil? slug) (str/blank? slug)) (bad "Provide slug")
        (or (nil? url)  (str/blank? url))  (bad "Provide url")
        (svc/update! slug url)             (ok {:ok true})
        :else                              (not-found))))

  ;; DELETE /short-url?slug=...
  (DELETE "/short-url" req
    (let [slug (or (get-in req [:params "slug"])
                   (get-in req [:body "slug"]))]
      (cond
        (or (nil? slug) (str/blank? slug)) (bad "Provide slug")
        (svc/delete! slug)                 (ok {:ok true})
        :else                              (not-found))))

  (route/not-found {:status 404 :body {:error "Not found"}}))

(def app
  (-> routes
      (wrap-json-body {:keywords? false})
      wrap-json-response
      wrap-params))

(defn -main [& _]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "3000"))]
    (println (str "Starting server on port " port "..."))
    (run-jetty app {:port port :join? true})))