(ns urlshort.client.core
  (:gen-class)
  (:require [clj-http.client :as http]
            [cheshire.core :as json]))

(def base (atom (or (System/getenv "API_BASE") "http://localhost:3000")))

(defn prompt [text]
  (print text) (flush) (read-line))

(defn configure []
  (let [n (prompt (str "Текущий API: " @base " | Новый (Enter — оставить): "))]
    (when (seq n) (reset! base n))
    (println "API:" @base)))

(defn post-json [url body]
  (http/post url {:headers {"Content-Type" "application/json"}
                  :body (json/generate-string body)
                  :as :json
                  :throw-exceptions false}))

(defn put-json [url body]
  (http/put url {:headers {"Content-Type" "application/json"}
                 :body (json/generate-string body)
                 :as :json
                 :throw-exceptions false}))

(defn get-json [url]
  (http/get url {:as :json :throw-exceptions false}))

(defn delete-json [url]
  (http/delete url {:as :json :throw-exceptions false}))

(defn create []
  (let [long (prompt "Введите длинный URL: ")]
    (when (seq long)
      (let [resp (post-json (str @base "/normal-url") {:url long})]
        (println "HTTP" (:status resp) "=>" (:body resp))))))

(defn show []
  (let [slug (prompt "Введите slug: ")]
    (when (seq slug)
      (let [resp (get-json (str @base "/short-url/" slug))]
        (println "HTTP" (:status resp) "=>" (:body resp))))))

(defn edit []
  (let [slug (prompt "Введите slug: ")
        new  (prompt "Новый URL: ")]
    (when (and (seq slug) (seq new))
      (let [resp (put-json (str @base "/short-url/" slug) {:url new})]
        (println "HTTP" (:status resp) "=>" (:body resp))))))

(defn del []
  (let [slug (prompt "Введите slug: ")]
    (when (seq slug)
      (let [resp (delete-json (str @base "/short-url/" slug))]
        (println "HTTP" (:status resp) "=>" (:body resp))))))

(defn menu []
  (println)
  (println "1) Создать   2) Показать   3) Изменить   4) Удалить   5) Настроить API   0) Выйти"))

(defn -main [& _]
  (loop []
    (menu)
    (case (prompt "Выбор: ")
      "1" (create)
      "2" (show)
      "3" (edit)
      "4" (del)
      "5" (configure)
      "0" (do (println "Пока!") (System/exit 0))
      (println "Неверный выбор"))
    (recur)))