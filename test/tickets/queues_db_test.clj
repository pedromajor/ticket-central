(ns tickets.queues-db-test
  (:require [tickets.queues-db :refer :all]
            [clojure.test :refer :all]))

(deftest t-add-remove-queue
  (let [db (queues-db)
        count-qs #(-> db all :queues count)]
    (add db (new-queue "pigs"))
    (is (= 1 (count-qs)))
    (rm db "pigs")
    (is (= 0 (count-qs)))))

(deftest t-push-updates
  (let [db (queues-db)
        updates (atom 0)]
    (add-observer db
                  (fn [_] (swap! updates inc)) :obs1)
    (doall (->> ["pigs" "chickens"]
                (map new-queue)
                (map (partial add db))))
    (is (= 2 @updates))))
