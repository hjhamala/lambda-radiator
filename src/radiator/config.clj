
(ns radiator.config
  (:require [clojure.spec.alpha :as spec]
            [clojure.spec.alpha :as s]))

(def title "Radiator")
(def site-name "Super-Radiator")

(def images
  {:ok             "https://upload.wikimedia.org/wikipedia/commons/0/0e/Dialog-apply.svg"
   :alarm          "https://upload.wikimedia.org/wikipedia/commons/0/04/Process-stop.svg"
   :ongoing        "https://upload.wikimedia.org/wikipedia/commons/a/a7/Orologio_verde.svg"
   :succeed        "https://upload.wikimedia.org/wikipedia/commons/0/0e/Dialog-apply.svg"
   :statistics     "https://upload.wikimedia.org/wikipedia/commons/8/89/Gnumeric.svg"
   :organization   "https://upload.wikimedia.org/wikipedia/commons/3/3e/Light_Bulb_Icon.svg"
   :cloud-provider nil})

(s/def ::non-empty-string (s/and string? #(> (count %) 1)))
(s/def ::name ::non-empty-string)
(s/def ::uri #(uri? (java.net.URI. %)))
(s/def ::api-key ::non-empty-string)
(s/def ::endpoint (s/keys :opt-un [::name ::uri]))
(s/def ::endpoints (s/coll-of ::endpoint))
(s/def ::aws (s/keys :req-un [::uri ::api-key]))
(s/def ::gitlab-pipeline (s/keys :req-un [::name ::uri ::api-key]))
(s/def ::gitlab-pipelines (s/coll-of ::gitlab-pipeline))
(s/def ::project (s/keys :opt-un [::aws ::gitlab-pipelines ::endpoints]))
(s/def ::projects (s/coll-of ::project))

(def projects
  [])

(def query-token
  "token-comes-here")