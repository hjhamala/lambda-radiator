(ns radiator.ui.images
  (:require [radiator.config :as config]))

(def ok-30px
  [:img.status-img  {:src (:ok config/images)}])

(def alarm-30px
  [:img.status-img {:src (:alarm config/images)}])

(def codepipeline-ongoing
  [:img.status-img {:src (:ongoing config/images)}])

(def codepipeline-succeed
  [:img.status-img {:src (:succeed config/images)}])

(def codepipeline-succeed-float-left
  [:img.status-img.float-left {:src (:succeed config/images)}])

(def statistics-30px
  [:img.status-img {:src (:statistics config/images)}])

