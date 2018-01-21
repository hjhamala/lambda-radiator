(ns radiator.source.common-test
  (:require [clojure.test :refer :all]
            [radiator.source.common :as common]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as stest]))
(stest/instrument `common/has-alarms?)
(stest/instrument `common/filter-ok-alarms)

(deftest coll-have-key-with-value
  (testing "List have match"
    (let [l [{:a 1} {:b 2} {:a 4}]]
      (is (common/coll-have-key-with-value :b 2 l))))
  (testing "no match"))

(deftest all-coll-have-key-with-value
  (testing "All elements have match"
    (is (common/all-coll-have-key-with-value :b 1 [{:b 1} {:b 1} {:b 1}])))
  (testing "All elements have match"
    (is (not (common/all-coll-have-key-with-value :b 1 [{:b 1} {:b 3} {:b 1}])))))

(deftest filter-when-key-with-value
  (testing "Second element is removed"
    (let [source [{:a 1} {:b 2} {:c 2}]]
      (is (= [{:a 1} {:c 2}] (common/filter-when-key-with-value :b 2 source)))))
  (testing "No match so element is returned unaltered"
    (let [source [{:a 1} {:e 2} {:c 2}]]
      (is (= source (common/filter-when-key-with-value :b 2 source))))))

(deftest pipelines-in-progress?
  (testing "Second pipeline is in progress"
    (let [pipelines [{:name "foo" :pipeline-status :success}
                     {:name "bar" :pipeline-status :in-progress}]]
      (is (common/pipelines-in-progress? pipelines))))
  (testing "No pipelines are in progress"
    (let [pipelines [{:name "foo" :pipeline-status :success}
                     {:name "bar" :pipeline-status :success}]]
      (is (not (common/pipelines-in-progress? pipelines))))))

(deftest all-pipelines-succeeded?
  (testing "All succeeded "
    (let [pipelines [{:name "foo" :pipeline-status :success}
                     {:name "bar" :pipeline-status :success}]]
      (is (common/all-pipelines-succeeded? pipelines))))
  (testing "Second is in progress so not succeeded"
    (let [pipelines [{:name "foo" :pipeline-status :success}
                     {:name "bar" :pipeline-status :in-progress}]]
      (is (not (common/all-pipelines-succeeded? pipelines))))))

(deftest pipelines-failed?
  (testing "Second failed"
    (let [pipelines [{:name "foo" :pipeline-status :success}
                     {:name "bar" :pipeline-status :failed}]]
      (is (common/pipelines-failed? pipelines))))
  (testing "No failed"
    (let [pipelines [{:name "foo" :pipeline-status :success}
                     {:name "bar" :pipeline-status :in-progress}]]
      (is (not (common/pipelines-failed? pipelines))))))

(deftest filter-succeeded-pipelines
  (testing "Second failed"
    (let [pipelines [{:name "foo" :pipeline-status :success}
                     {:name "bar" :pipeline-status :failed}]]
      (is (= [{:name "bar" :pipeline-status :failed}] (common/filter-succeeded-pipelines pipelines)))))
  (testing "No succeeded"
    (let [pipelines [{:name "foo" :pipeline-status :failed}
                     {:name "bar" :pipeline-status :in-progress}]]
      (is (= pipelines (common/filter-succeeded-pipelines pipelines))))))

(deftest has-alarms?
  (testing "When list have alarms - result is true"
    (let [alarms [{:alarm-status :alarm
                   :name "foo"}
                  {:alarm-status :ok
                   :name "bar"}]]
      (is (common/has-alarms? alarms))))
  (testing "When list doesnt have alarms - result is false"
    (let [alarms [{:alarm-status :ok
                   :name "foo"}
                  {:alarm-status :ok
                   :name "bar"}]]
      (is (not (common/has-alarms? alarms))))))

(deftest filter-ok-alarms
  (testing "When list have ok values - they are filtered"
    (let [alarms [{:alarm-status :alarm
                   :name "bar"}
                  {:alarm-status :ok
                   :name "foo"}]]
      (is (= [{:alarm-status :alarm
               :name "bar"}] (common/filter-ok-alarms alarms)))))
  (testing "When list have only alarms - it is returned as it is"
    (let [alarms [{:alarm-status :alarm
                   :name "foo"}
                  {:alarm-status :alarm
                   :name "bar"}]]
      (is (= alarms (common/filter-ok-alarms alarms))))))