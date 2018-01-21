(ns radiator.source.common
  (:require [radiator.source.aws :as aws]
            [radiator.source.gitlab :as gitlab]
            [radiator.config :as config]
            [clojure.spec.alpha :as s]))

(s/def ::name string?)
(s/def ::pipeline-status #{:success :in-progress :failed :unknown})
(s/def ::pipeline (s/keys :req-un [::name ::pipeline-status ::message ::author]))
(s/def ::alarm-status #{:ok :alarm})
(s/def ::alarm (s/keys :req-un [::name ::alarm-status]))
(s/def ::metric (s/keys :req-un [::name ::metric-value]))
(s/def ::alarm-history (s/keys :req-un [::name ::alarm-status ::timestamp]))

(s/fdef has-alarms?
          :args (s/cat :alarm-list (s/coll-of ::alarm))
          :ret (s/coll-of ::alarm))

(s/fdef filter-ok-alarms
        :args (s/cat :alarm-list (s/coll-of ::alarm))
        :ret (s/coll-of ::alarm))

(defn coll-have-key-with-value
  "Returns true if element if collection have key k with value v"
  [k v c]
  (some #(= v (get % k)) c))

(defn all-coll-have-key-with-value
  "Every element in collection have key k with value v"
  [k v c]
  (every? #(= v (get % k)) c))

(defn filter-when-key-with-value
  "Filters element if have key k with value v"
  [k v c]
  (filter  #(not= v (get % k)) c))

(defn pipelines-in-progress?
  [list]
  (coll-have-key-with-value :pipeline-status :in-progress  list))

(defn all-pipelines-succeeded?
  [pipelines]
  (all-coll-have-key-with-value :pipeline-status :success pipelines))

(defn pipelines-failed?
  [list]
  (coll-have-key-with-value :pipeline-status :failed list))

(defn filter-succeeded-pipelines
  [pipelines]
  (filter-when-key-with-value :pipeline-status :success pipelines))

(defn has-alarms?
  "Returns true if a list have elements with :alarm-status :ok"
  [alarms-list]
  (coll-have-key-with-value :alarm-status :alarm alarms-list))

(defn filter-ok-alarms
  [alarms-list]
  (filter-when-key-with-value :alarm-status :ok alarms-list))

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