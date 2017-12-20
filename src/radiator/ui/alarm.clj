(ns radiator.ui.alarm
  (:require [radiator.ui.images :as img]))

(defn has-alarms?
  [alarms-list]
  (not-empty (filter #(not= :ok (:alarm-status %)) alarms-list)))

(defn filter-ok
  [alarms-list]
  (filter #(not= :ok (:alarm-status %)) alarms-list))

(defn alarms-box
  [alarms]
  (when alarms
    [:div.light-grey-background
     (if (has-alarms? alarms)
       [:div
        [:div.warning-background
          [:div.t-b-5px-padding { :align "center"}
             img/alarm-30px
           [:span.header.text-center "Alarms"]]]
         (for [alarm (filter-ok alarms)]
           [:div.t-b-5px-padding img/alarm-30px [:span (:name alarm)]])]
       [:div
         [:div.ok-background
          [:div.t-b-5px-padding { :align "center"}
           img/ok-30px
           [:span.header.text-center "Alarms"]]]
         [:div.t-b-5px-padding img/ok-30px [:span (str "All " (count alarms) " OK")]]])]))

