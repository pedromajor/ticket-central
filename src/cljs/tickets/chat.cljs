(ns tickets.chat
  (:require [chord.client :refer [ws-ch]]
            [cljs.core.async :refer [<! >!]])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(defonce world (atom {:backend-server "127.0.0.1"}))

(defn get-ws-uri [] (str "ws://" (:backend-server @world) ":9009" "/ws/ping"))
(defn get-ws-chat-uri [] (str "ws://" (:backend-server @world) ":9009" "/ws"))

(defn now-stamp []
  (.getTime (js/Date.)))

(defn ping-server []
  (go
    (let [{:keys [ws-channel error]} (<! (ws-ch (get-ws-uri)))]
      (if-not error
        (do
          (>! ws-channel "Hello from client")
          (js/console.log (:message (<! ws-channel))))
        (js/console.log "WebSocket error:" error)))))

(defn chatty []
  (go
    (let [{:keys [ws-channel error]} (<! (ws-ch (get-ws-chat-uri)))]
      (if-not error
        (do
          (go-loop []
            (js/console.log "waiting for msg")
            (let [{:keys [message error] :as msg} (<! ws-channel)]
              (when message
                (js/console.log "RX msg:" (:counter message))
                (recur)))))
        (js/console.log "WebSocket error:" error)))))

;; (ping-server)
;; (chatty)
