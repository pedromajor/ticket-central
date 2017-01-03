(ns tickets.web
  (:require [chord.http-kit :refer [with-channel]]
            [clojure.core.async :refer [go]]
            [clojure.java.io :as io]
            [com.stuartsierra.component :as component]
            [compojure.core :refer [GET routes]]
            [compojure.route :refer [resources]]
            [org.httpkit.server :refer [on-close on-receive run-server]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.response :refer [response]]
            [tickets.clients :refer [add]]))

(defn ws-requests-handler [clients req]
  (with-channel req ws-channel
    (go
      (println "new ws requeset from:" (:remote-addr req))
      (add clients ws-channel))))

(comment
  (defn- info-handler [req]
    (response {:open-sockets (tickets.clients/->count)})))

(defn app-routes [clients]
  (routes
    (GET "/ws"    req (ws-requests-handler clients req))
    ;; (GET "/info"  [] info-handler)
    (GET "/"      [] (slurp (io/resource "public/index.html")))
    (resources "/")))

(defn- start-server [port clients]
  (let [srv (run-server
              (wrap-json-response (app-routes clients))
              {:port port})]
    (println "server started on localhost:" port)
    srv))

(defn- stop-server [server-shut-fn]
  (when server-shut-fn
    (server-shut-fn)
    (println "http-server halted")))

(defrecord WebServer [port clients]
    component/Lifecycle
    (start [this]
      (assoc this :http-server (start-server port clients)))
    (stop [this]
      (stop-server (:http-server this))
      (assoc this :http-server nil)))

(defn web-server [port]
  (component/using (map->WebServer {:port port})
                   [:clients]))
