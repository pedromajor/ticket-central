(ns tickets.queues-db
  (:require [com.stuartsierra.component :as component]))

(defn new-queue [name]
  {:name name :counter 0 :attendant "-"})

(defprotocol IDB
  "The Queues Store"
  (-add          [this q])
  (-rm           [this qn])
  (-all          [this])
  (-update       [this qn counter attendant])
  (-add-observer [this on-update k]))

(defrecord InMemoryQueues [state]
  IDB
  (-all [this]
    {:queues (-> @state :queues vals)})
  (-add [this q]
    (swap! state assoc-in [:queues (:name q)] q))
  (-rm [this qn]
    (swap! state update-in [:queues] dissoc qn))
  (-update [this qn counter attendant]
    (when (-> @state :queues (get qn))
      (swap! state update-in [:queues qn] merge
             {:counter counter :attendant attendant})))
  (-add-observer [this on-update k]
    (add-watch state k
               (fn [_ _ _ new-state]
                 (on-update {:queues (-> new-state :queues vals)})))))

(defn all [db]
  {:pre [(satisfies? IDB db)]}
  (-all db))

(defn add [db qn]
  {:pre [(satisfies? IDB db)]}
  (-add db qn))

(defn rm [db qn]
  {:pre [(satisfies? IDB db)]}
  (-rm db qn))

(defn update [db qn counter attendant]
  {:pre [(satisfies? IDB db)]}
  (-update db qn counter attendant))

(defn add-observer [db on-update k]
  {:pre [(satisfies? IDB db) (keyword? k)]}
  (-add-observer db on-update k))

(defn queues-db []
  (->InMemoryQueues (atom {})))

(comment
  (let [{:keys [db clients]} reloaded.repl/system]
    (add db (new-queue "Pigs"))
    (add db (new-queue "Chimps"))
    (add db (new-queue "Humans"))
    (all db))
  )

;; (def senha (atom 0))
(comment
  (let* [qns ["Pigs" "Chimps" "Humans"]
         qn (nth qns (rand-int 3))
         db (:db reloaded.repl/system)]
    (update db
            qn
            (swap! senha inc)
            (str "Balcao " (rand-int 10))))
  )
