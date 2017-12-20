(ns config-test
  (:require [clojure.test :refer :all]
            [radiator.config :as config]
            [clojure.spec.alpha :as spec]))

(deftest config
  (testing "Configuration is valid"
    (is (spec/valid? ::config/projects config/projects))))
