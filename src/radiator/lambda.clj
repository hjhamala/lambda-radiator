(ns radiator.lambda
  (:require [uswitch.lambada.core :refer [deflambdafn]]
            [radiator.config :as config]
            [radiator.ui.page :as page]
            [clojure.java.io :as io]
            [clojure.data.json :as json]))

(defn response
  [out result]
  (with-open [w (io/writer out)]
    (json/write result w)))

(deflambdafn fi.sok.superradiator.Handler
             [in out ctx]
             (let [event (json/read (io/reader in))
                   token (get-in event ["queryStringParameters" "token"])]
               (cond
                 (= token config/query-token) (response out {:body       (page/generate)
                                                             :statusCode 200
                                                             :headers    {"Content-Type" "text/html"}})
                 :else (response out {:body       "No token - no show"
                                      :statusCode 401}))))