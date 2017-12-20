(ns radiator.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [clj-http.client :as client]
            [radiator.ui.page :as page]
            [ring.adapter.jetty :as ring]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defroutes app-routes
  (GET "/" [] (page/generate))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))

(defn start
  []
  (ring/run-jetty #'app {:port 8080 :join? false}))

(defn -main
  [& args]
  (start))




