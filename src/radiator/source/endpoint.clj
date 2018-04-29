(ns radiator.source.endpoint
  (:require [clj-http.client :as client])
  (:use [slingshot.slingshot :only [throw+ try+]]))

(defn check-status
  [{:keys [name uri] :as all}]
  (println all)
  (try+
    (let [result (client/get uri
                             {:socket-timeout 10000 :conn-timeout 2000})]
      {:name   name
       :status :ok})
    (catch Object {:keys [status]}
      {:name   name
       :status :failed
       :code   status})))