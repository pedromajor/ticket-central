(ns user
  (:require [com.stuartsierra.component :as component]
            [reloaded.repl :refer [system reset stop start]]
            [tickets.clients :refer [clients]]
            [tickets.queues-db :refer [queues-db]]
            [tickets.web :refer [web-server]]))

(defn new-dev-system []
  (component/system-map
    :web     (web-server 9009)
    :clients (clients)
    :db      (queues-db)))

(reloaded.repl/set-init! #'new-dev-system)
