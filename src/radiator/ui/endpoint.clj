(ns radiator.ui.endpoint
  (:require [radiator.ui.images :as img]
            [clj-time.format :as f]
            [radiator.source.common :as common]))

(defn endpoints-box
  [endpoints]
  (println "endpoints-box  endpoints" endpoints)
  (when-not (empty? endpoints)
    [:div.light-grey-background
     (if (common/coll-have-key-with-value :status :failed  endpoints)
       [:div
        [:div.warning-background
         [:div.t-b-5px-padding {:align "center"}
          img/alarm-30px
          [:span.header.text-center "Endpoints failed!"]]]
        (for [failed-endpoint (common/filter-when-key-with-value  :status :ok endpoints)]
          [:div.t-b-5px-padding img/alarm-30px [:span (:name failed-endpoint) " " "(" (:code failed-endpoint) ")"]])]
       [:div
        [:div.ok-background
         [:div.t-b-5px-padding {:align "center"}
          img/ok-30px
          [:span.header.text-center "Endpoints"]]]
        [:div.t-b-5px-padding img/ok-30px [:span (str "All " (count endpoints) " OK")]]])]))


