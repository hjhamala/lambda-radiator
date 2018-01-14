(ns radiator.ui.metric
  (:require [radiator.ui.images :as img]))

(defn metrics-box
  [metrics]
  (when (not-empty metrics)
    [:div.light-grey-background
     [:div
      [:div.ok-background.margin-top-5
       [:div.t-b-5px-padding {:align "center"}
        img/statistics-30px
        [:span.header.text-center "Metrics"]]]
      [:table {:style "width:100%"}
       [:tr
        [:th "Name"]
        [:th "Value"]]
       (for [metric metrics]
         [:tr
          [:td (:name metric)]
          [:td (:metric-value metric)]])]]]))