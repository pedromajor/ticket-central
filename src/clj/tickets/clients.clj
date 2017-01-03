(ns tickets.clients
  (:require [clojure.core.async :refer [>! >!! close! go]]
            [com.stuartsierra.component :as component]
            [tickets.queues-db :as queues]))

(declare notify-all)

(defrecord Clients [db channels]
  component/Lifecycle
  (start [this]
    (queues/add-observer db (partial notify-all channels) ::web)
    this)
  (stop [this] this))

(defn clients []
  (component/using
    (map->Clients {:channels (atom #{})})
    [:db]))

(defn channels [clients]
  (-> clients :channels deref))

(defn add
  "register a new client websocket channel, and send world"
  [clients ch]
  (let [{:keys [db channels]} clients]
    (swap! channels conj ch)
    (println "reg. done!")
    (go
      (>! ch (queues/all db)))))

(defn notify-all [a-chans queues]
  (println "notifying " (count @a-chans) " channels")
  (println "msg:" queues)
  (go
    (doseq [ch @a-chans]
      (>! ch queues))))
