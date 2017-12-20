(ns radiator.source.aws-test
  (:require [clojure.test :refer :all]
            [radiator.source.common :as common]
            [radiator.source.aws :as aws]
            [clojure.spec.alpha :as s]))

(deftest transform-pipeline
  (testing "Succeeded to :success"
    (let [result (aws/transform-pipeline {:currentStatus "Succeeded" :name "test"})]
      (is (= result {:pipeline-status :success :name "test"}))
      (is (s/valid? ::common/pipeline result))))
  (testing "Failed to :failed"
    (let [result (aws/transform-pipeline {:currentStatus "Failed" :name "test"})]
      (is (= result {:pipeline-status :failed :name "test"}))
      (is (s/valid? ::common/pipeline result))))
  (testing "InProgress to :in-progress"
    (let [result (aws/transform-pipeline {:currentStatus "InProgress" :name "test"})]
      (is (= result {:pipeline-status :in-progress :name "test"}))
      (is (s/valid? ::common/pipeline result))))
  (testing "Anything else to :unknown"
    (let [result (aws/transform-pipeline {:currentStatus "Something else" :name "test"})]
      (is (= result {:pipeline-status :unknown :name "test"}))
      (is (s/valid? ::common/pipeline result)))))

(deftest transform-alarm
  (testing "OK to :ok"
    (let [result (aws/transform-alarm {:AlarmName "test" :StateValue "OK"})]
      (is (= result {:alarm-status :ok :name "test"}))
      (is (s/valid? ::common/alarm result))))
  (testing "Anything else to alarm"
    (let [result (aws/transform-alarm {:AlarmName "test" :StateValue "Something else"})]
      (is (= result {:alarm-status :alarm :name "test"}))
      (is (s/valid? ::common/alarm result)))))

(deftest transform-metric
  (testing "Percent to string with three decimals and %"
    (let [result (aws/transform-metric {:name "test" :unit "Percent" :result 6.2787})]
      (is (= result {:name "test" :metric-value "6.279 %"}))
      (is (s/valid? ::common/metric result))))
  (testing "Anything else retains its value"
    (let [result (aws/transform-metric {:name "test" :unit "Something else" :result 6.0})]
      (is (= result {:name "test" :metric-value 6.0}))
      (is (s/valid? ::common/metric result)))))
