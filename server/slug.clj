(ns urlshort.server.slug
  (:import (java.security SecureRandom)))

(def base62-chars
  (vec (concat (map char (range 48 58))   ;; 0-9
               (map char (range 65 91))   ;; A-Z
               (map char (range 97 123))))) ;; a-z

(defn rand-slug
  "Случайный base62 slug длины n (по умолчанию 6)"
  ([] (rand-slug 6))
  ([n]
   (let [rnd (SecureRandom.)]
     (->> (repeatedly n #(.nextInt rnd (count base62-chars)))
          (map base62-chars)
          (apply str)))))