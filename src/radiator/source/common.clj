(ns radiator.source.common
  (:require [radiator.source.aws :as aws]
            [radiator.source.gitlab :as gitlab]
            [radiator.config :as config]
            [clojure.spec.alpha :as s]))

(s/def ::name string?)
(s/def ::pipeline-status #{:success :in-progress :failed :unknown})
(s/def ::pipeline (s/keys :req-un [::name ::pipeline-status]))
(s/def ::alarm-status #{:ok :alarm})
(s/def ::alarm (s/keys :req-un [::name ::alarm-status]))
(s/def ::metric (s/keys :req-un [::name ::metric-value]))
(s/def ::alarm-history (s/keys :req-un [::name ::alarm-status ::timestamp]))

(defn get-status
  [{:keys [aws gitlab-api-key gitlab-pipelines name] :as end-point}]
  {:name name
   :aws-status    (future
                    (when aws
                      (aws/pipeline-and-alarm-statuses aws)))
   :gitlab-status (future
                    (mapv gitlab/pipeline-status gitlab-pipelines))})

(defn get-statuses
  []
  (mapv get-status config/projects))