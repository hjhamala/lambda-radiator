(ns radiator.ui.pipeline
  (:require [radiator.ui.images :as img]
            [radiator.source.common :as common]))

(defn pipelines-status-img
  [pipelines]
  (cond
    (common/pipelines-failed? pipelines) img/alarm-30px
    (common/pipelines-in-progress? pipelines) img/codepipeline-ongoing
    :else img/codepipeline-succeed))

(defn first-line-of-commit
  [pipeline]
  (when-let [message (:message pipeline)]
    (-> message
        clojure.string/split-lines
        first)))

(defn add-author-if-present
  [pipeline]
  (when-let [author (:author pipeline)]
    (str ": (" (:author pipeline) ")")))

(defn commit-info
  [pipeline]
  [:div.container-column [:p.small [:strong (:name pipeline)] (add-author-if-present pipeline)]
   (if-let [first-line (first-line-of-commit pipeline)]
     [:p.small first-line])])

(defn stage-background
  [stage]
  (condp = (:status stage)
    :success "green-background"
    :in-progress "yellow-background"
    :failed "red-background"
    {}))

(defn stages
  [stages]
  [:div.flex
   (for [stage stages]
     [:p.small.border.stage-element {:class (stage-background stage)} (:name stage)])])

(defn pipelines-header
  [extra-class pipelines]
  [:div.only-t-b-padding {:class extra-class}
   [:div.t-b-5px-padding {:align "center"} (pipelines-status-img pipelines) [:span.header.text-center "Pipelines"]]])

(defn pipelines-count
  [pipelines]
  [:div.t-b-5px-padding img/ok-30px [:span (str "All " (count pipelines) " OK")]])

(defn pipeline-with-stages
  [pipeline image]
  [:div
   [:div.flex image (commit-info pipeline)]
   (stages (:stages pipeline))])

(defn list-not-succeeded-pipelines
  [pipelines]
  (for [pipeline (common/filter-succeeded-pipelines pipelines)]
    [:div.t-b-5px-padding
     (cond
       (= (:pipeline-status pipeline) :success) (pipeline-with-stages pipeline img/codepipeline-succeed)
       (= (:pipeline-status pipeline) :in-progress) (pipeline-with-stages pipeline img/codepipeline-ongoing)
       (= (:pipeline-status pipeline) :unknown) [:div.flex img/codepipeline-ongoing [:p.small [:strong "unknown: "(:name pipeline)]]]
       :else (pipeline-with-stages pipeline img/alarm-30px))]))

(defn pipelines-box
  [combined-pipelines]
  (println combined-pipelines)
  [:div.light-grey-background
   (if (common/pipelines-failed? combined-pipelines)
     (pipelines-header "warning-background" combined-pipelines)
     (pipelines-header "ok-background" combined-pipelines))
   (if (common/all-pipelines-succeeded? combined-pipelines)
     (pipelines-count combined-pipelines)
     (list-not-succeeded-pipelines combined-pipelines))])