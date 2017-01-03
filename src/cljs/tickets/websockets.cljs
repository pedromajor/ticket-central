(ns tickets.websockets
  (:require [chord.client :refer [ws-ch]]
            [cljs.core.async :refer [<! >!]])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(defn get-ws-uri []
  (str "ws://" (.-host (.-location js/window)) "/ws"))

(defn receive-msgs-loop
  "receiving messages loop"
  [on-receive server-ch]
  (go-loop []
    (let [{:keys [message error]} (<! server-ch)]
      (when message
        (js/console.log (str "RX server msg:" message))
        (on-receive message)
        (recur)))))

(defn init
  "establishes a web-socket channel to the backend"
  [on-receive-callback]
  (js/console.log "trying to open a socket to" (get-ws-uri))
  (go
    (let [{:keys [ws-channel error]} (<! (ws-ch (get-ws-uri)))]
      (if-not error
        (receive-msgs-loop on-receive-callback ws-channel)
        (js/console.log "WebSocket error:" error)))))
