(ns radiator.ui.page
  (:require [hiccup.core :as hiccup]
            [radiator.ui.pipeline :as pipeline]
            [radiator.ui.metric :as metric]
            [radiator.ui.alarm :as alarm]
            [radiator.source.aws :as aws]
            [radiator.source.common :as common]
            [radiator.source.gitlab :as gitlab]
            [radiator.config :as config]
            [garden.selectors :refer [nth-child]]
            [garden.core :refer [css]]))

(defn account-header
  [alarms pipelines name]
  (cond
    (or (alarm/has-alarms? alarms) (pipeline/pipelines-failed? pipelines)) [:h1.t-b-15px-padding.text-center.black-background.red name]
    (pipeline/pipelines-in-progress? pipelines) [:h1.text-center.t-b-15px-padding.black-background.yellow name]
    :else [:h1.text-center.t-b-15px-padding.black-background.white name]))

(defn make-row
  [accounts]
  [:div.container
   (for [{:keys [aws-status gitlab-status name]} accounts]
     (let [alarms (aws/transform-alarms (:alarms @aws-status))
           combined-pipelines (concat (aws/transform-pipelines (:pipelines @aws-status))
                                      (gitlab/transform-pipelines @gitlab-status))
           metrics (aws/transform-metrics (:metrics @aws-status))]
       [:div.item.width-32
        (account-header alarms combined-pipelines name)
        [:div.container-2.min-heightbox.light-grey-background
         [:div.item.border.no-padding
          [:div.full-width
           (alarm/alarms-box alarms)
           (pipeline/pipelines-box combined-pipelines)
           (metric/metrics-box metrics)]]]]))])

(def styles
  [:style
    (css [:body {:margin  "0px"
                 :padding "0px"}])
    (css [:h1 {:font-family    "proxima-nova,sans-serif"
               :text-transform "uppercase"
               :margin         "0px"
               :font-size      "18px"}])
    (css [:.header {:font-family "proxima-nova,sans-serif"
                    :font-size   "16px"}])
    (css [:span {:font-family "proxima-nova,sans-serif"
                 :font-size   "14px"}])
    (css [:p {:font-family "proxima-nova,sans-serif"
              :font-size   "14px"}])
    (css [:.container {:display         "flex"
                       :justify-content "space-around"
                       :padding-left    "10px"
                       :padding-right   "10px"}])
    (css [:.container-2 {:display         "flex"
                         :justify-content "space-around"
                         }])
    (css [:.item {:border-bottom-left-radius  "10px"
                  :border-bottom-right-radius "10px"
                  :padding                    "5px"
                  :flex-grow                  "1"}])
    (css [:.no-padding {:padding "0px"}])
    (css [:.t-b-5px-padding {:padding-top    "5px"
                             :padding-bottom "5px"}])
    (css [:.t-b-15px-padding {:padding-top    "15px"
                              :padding-bottom "15px"}])
    (css [:.min-heightbox {:min-height "200px"}])
    (css [:.width-32 {:width "32%;"}])
    (css [:.header-img {:height "50px"
                        :vertical-align "middle"}])
   (css [:.status-img {:height "30px"
                       :padding-left "5px"
                       :padding-right "5px"
                       :vertical-align "middle"}])
   (css [:.header-site-name {:color "white"
                             :font-size "20px"
                             :text-transform "uppercase"
                             :padding-left "10px"}])
    (css [:.display {:width "100%;"}])
    (css [:.full-width {:width "100%"}])
    (css [:.white {:color "white"}])
    (css [:.red {:color "red"}])
    (css [:.yellow {:color "yellow"}])
    (css [:.warning-background {:padding-top      "5px"
                                :padding-bottom   "5px"
                                :background-color "#ff0000"}])
    (css [:.ok-background {:padding-top      "5px"
                           :padding-bottom   "5px"
                           :background-color "#40bf40"}])
    (css [:.black-background {:background-color "black"}])
    (css [:.light-grey-background {:background-color "#f0f5f5"}])
    (css [:.text-center {:text-align "center"}])
    (css [:.border {:border-color "grey"
                    :border-width "thin"
                    :border-style "solid"}])
    (css [:table {:padding "10px"}])
    (css [:th {:padding    "5px"
               :text-align "left"
               :background "black" :color "white"}])
    (css [:td {:padding "5px"}])
    "tr:nth-child(even) {background: #CCC}"
    "tr:nth-child(odd) {background: #FFF}"])

(def page-header
  [:div.black-background
   [:img.header-img {:src   (:organization config/images)}]
   (when (:cloud-provider config/images)
     [:img.header-img {:src   (:cloud-provider config/images)}])
   [:span.header-site-name config/site-name]])

(defn content
  []
  (let [rows (partition-all 3 (common/get-statuses))]
    (for [row rows]
      (make-row row))))

(defn generate
  []
  (hiccup/html
    [:html
     [:head
      [:meta {:charset    "UTF-8"
              :http-equiv "refresh" :content "10"}]
      [:meta {:http-equiv "Cache-Control" :content= "no-store"}]
      [:title config/title]
      styles]
     [:body
      page-header
      (content)]]))