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

(defn pipelines-box
  [combined-pipelines]
  [:div.light-grey-background

   (if (common/pipelines-failed? combined-pipelines)
     [:div.warning-background.only-t-b-padding
      [:div.t-b-5px-padding {:align "center"} (pipelines-status-img combined-pipelines) [:span.header.text-center "Pipelines"]]]
     [:div.ok-background.only-t-b-padding
      [:div.t-b-5px-padding {:align "center"} (pipelines-status-img combined-pipelines) [:span.header.text-center "Pipelines"]]])

   (if (common/all-pipelines-succeeded? combined-pipelines)
     [:div.t-b-5px-padding img/ok-30px [:span (str "All " (count combined-pipelines) " OK")]]
     (for [pipeline (common/filter-succeeded-pipelines combined-pipelines)]
       [:div.t-b-5px-padding
        (cond
          (= (:pipeline-status pipeline) :success) [:div
                                                    [:div.flex img/codepipeline-succeed (commit-info pipeline)]
                                                    (stages (:stages pipeline))]
          (= (:pipeline-status pipeline) :in-progress) [:div
                                                        [:div.flex img/codepipeline-ongoing  (commit-info pipeline)]
                                                        (stages (:stages pipeline))]
          (= (:pipeline-status pipeline) :unknown) [:div.flex img/codepipeline-ongoing [:p.small [:strong "unknown: "(:name pipeline)]]]
          :else [:div
                 [:div.flex img/alarm-30px  (commit-info pipeline)]
                 (stages (:stages pipeline))])]))])