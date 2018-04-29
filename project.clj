(defproject radiator "0.1.0"
  :description "Simple radiator for different endpoint datas"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [hiccup "1.0.5"]
                 [compojure "1.6.0"]
                 [clj-http "3.7.0"]
                 [uswitch/lambada "0.1.2"]
                 [org.clojure/data.json "0.2.6"]
                 [ring/ring-jetty-adapter "1.6.0"]
                 [ring/ring-defaults "0.2.1"]
                 [garden "1.3.3"]
                 [slingshot "0.12.2"]
                 [clj-time "0.14.2"]]
  :plugins [[lein-ring "0.9.7"]]
  :ring {:handler radiator.handler/app}
  :profiles
    {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                          [ring/ring-mock "0.3.0"]]}
     :uberjar {:aot :all}}
  :main radiator.handler)
