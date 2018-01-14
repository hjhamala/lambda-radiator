(ns radiator.source.aws
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]
            [clj-time.format :as f]))

(defn- round [s n]
  (.setScale (bigdec n) s java.math.RoundingMode/HALF_EVEN))

;; Pipelines

(defn transform-pipeline
  [{:keys [currentStatus name commitAuthor commitMessage] :as all}]
  (condp = currentStatus
    "Succeeded" {:name   name
                 :pipeline-status :success
                 :author commitAuthor
                 :message commitMessage}
    "Failed"    {:name   name
                 :pipeline-status :failed
                 :author commitAuthor
                 :message commitMessage}
    "InProgress" {:name   name
                  :pipeline-status :in-progress
                  :author commitAuthor
                  :message commitMessage}
    {:name name
     :pipeline-status :unknown
     :author commitAuthor
     :message commitMessage}))

(defn transform-pipelines
  [pipelines]
  (map transform-pipeline pipelines))

;; Alarms

(defn transform-alarm
  [{:keys [AlarmName StateValue]}]
  (condp = StateValue
    "OK"  {:name   AlarmName
           :alarm-status :ok}
    {:name   AlarmName
     :alarm-status :alarm}))

(defn transform-alarms
  [alarms]
  (map transform-alarm alarms))

;; Metrics

(defn transform-metric
  [{:keys [name unit result]}]
  {:name name
   :metric-value (condp = unit
            "Percent" (str (round 3 result) " %")
            result)})

(defn transform-metrics
  [metrics]
  (map transform-metric metrics))

(defn pipeline-and-alarm-statuses
  [{:keys [uri api-key]}]
  (try
    (json/read-str (:body (client/get uri
                                      {:socket-timeout 10000 :conn-timeout 2000
                                       :headers {"x-api-key" api-key}})) :key-fn keyword)
    (catch Exception e
      (do
        (clojure.stacktrace/print-stack-trace e)
        nil))))

;; Alarms history

(def aws-timestamp-format
  (f/formatter "YYYY-MM-dd'T'HH:mm:ss.SSSSSSZ"))

(defn transform-history
  [{:keys [AlarmName State Timestamp]}]
  {:name AlarmName
   :alarm-status State
   :timestamp (f/parse aws-timestamp-format Timestamp)})

(defn filter-only-newest
  [mapped-history]
  (->> (group-by :name mapped-history)
        (map (fn[[k v]]{:name k
                        :timestamp (:timestamp (last (sort-by :timestamp v)))}))))

(defn transform-alarm-histories
  [histories]
  (filter-only-newest (map transform-history histories)))



