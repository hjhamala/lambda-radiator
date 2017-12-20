(ns radiator.source.gitlab
  (:require [clojure.data.json :as json]
            [radiator.ui.pipeline :as pipeline]
            [clj-http.client :as client]))

(defn- transform-pipeline
  [{:keys [status name]}]
  (condp = status
    "success"  {:name name :pipeline-status :success}
    "failed"   {:name name :pipeline-status :failed}
    "running"  {:name name :pipeline-status :in-progress}
    "pending"  {:name name :pipeline-status :in-progress}
    "canceled" {:name name :pipeline-status :success}
    "skipped"  {:name name :pipeline-status :success}
    {:name name :pipeline-status :unknown}))

(defn transform-pipelines
  [pipelines]
  (map transform-pipeline pipelines))

(defn get-pipeline
  [uri api-key]
  (try
    (let [result (client/get uri
                             {:socket-timeout 1000 :conn-timeout 1000
                              :headers        {"Private-Token" api-key}})]
      (first (json/read-str (:body result))))
    (catch Exception e {:pipeline-status "unknown"})))

(defn pipeline-status
  [{:keys [api-key name uri]}]
  {:name   name
   :status (:status (get-pipeline uri api-key))})

