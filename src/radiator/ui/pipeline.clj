(ns radiator.ui.pipeline
  (:require [radiator.ui.images :as img]))

(defn some-list
  [value key list]
  (some #(= value (key %)) list))

(defn pipelines-in-progress?
  [list]
  (some-list :in-progress :pipeline-status list))

(defn all-pipelines-ok?
  [list]
  (every? #(= :success (:pipeline-status %)) list))

(defn pipelines-failed?
  [list]
  (some-list :failed :pipeline-status list))

(defn filter-ok
  [pipelines]
  (filter  #(not= :success (:pipeline-status %)) pipelines))

(defn pipelines-status-img
  [pipelines]
  (cond
    (some #(= :failed (:pipeline-status %)) pipelines) img/alarm-30px
    (some #(= :in-progress (:pipeline-status %)) pipelines) img/codepipeline-ongoing
    :else img/codepipeline-succeed))

(defn pipelines-box
  [combined-pipelines]
  [:div.light-grey-background

   (if (pipelines-failed? combined-pipelines)
     [:div.warning-background.only-t-b-padding
      [:div.t-b-5px-padding {:align "center"} (pipelines-status-img combined-pipelines) [:span.header.text-center "Pipelines"]]]
     [:div.ok-background.only-t-b-padding
      [:div.t-b-5px-padding {:align "center"} (pipelines-status-img combined-pipelines) [:span.header.text-center "Pipelines"]]])

   (if (all-pipelines-ok? combined-pipelines)
     [:div.t-b-5px-padding img/ok-30px [:span (str "All " (count combined-pipelines) " OK")]]
     (for [pipeline (filter-ok combined-pipelines)]
       [:div.t-b-5px-padding
        (cond
          (= (:pipeline-status pipeline) :success) [:div img/codepipeline-succeed [:span (:name pipeline)]]
          (= (:pipeline-status pipeline) :in-progress) [:div img/codepipeline-ongoing [:span (:name pipeline)]]
          (= (:pipeline-status pipeline) :unknown) [:div img/codepipeline-ongoing [:span (str "Unknown: " (:name pipeline))]]
          :else [:div img/alarm-30px [:span (:name pipeline)]])]))])