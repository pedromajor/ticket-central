(ns tickets.frontend
  (:require [quiescent.core :as q]
            [quiescent.dom :as d]
            [tickets.websockets :as ws]))

(defonce world (atom {}))

(q/defcomponent Counter
  :keyfn identity
  [val]
  (d/div {:className "css-transition-item blink_div"}
    (d/h2 {:style {:padding "20px"}} (:counter val))
    (d/h4 {:style {:color "#ffffff" :paddingBottom "10px"}} (:attendant val))))

(q/defcomponent Queue
  [queue]
  (d/div {:className "col-xs-4"}
    (d/div {:className "panel panel-default"}
      (d/div {:className "panel-body" :style {:textAlign "center"}}
        (d/h2 {:style {:color "#57637A"}} (:name queue))
        (d/div {:className "css-transition-container"}
          (q/CSSTransitionGroup {:transitionName "my-transition"
                                 :transitionEnterTimeout 6000
                                 :transitionLeaveTimeout 2000}
            (Counter queue)))))))

(q/defcomponent Queues [queues]
  (apply d/div {:className "row"}
    (d/br {})
    (map Queue queues)))

(q/defcomponent Header [clock]
  (d/header {:style {:textAlign "right" :color "#BDC5CA"}}
    (d/h2 nil
      (str
        (:hours clock) ":"
        (:minutes clock) ":"
        (:seconds clock)))))

(q/defcomponent Root [data]
  (d/div {:className "container-fluid"}
    (Header (:clock data))
    (Queues (:queues data))))

(defn render [data]
  (q/render
    (Root data)
    (.getElementById js/document "main")))

(add-watch world ::render
  (fn [_ _ _ data] (render data)))

(defonce *whatever* (render @world))

(defn decimal-parts [n]
  [(quot n 10) (mod n 10)])

(defn get-time []
  (let [clock (js/Date.)]
    {:hours   (apply str (decimal-parts (.getHours clock)))
     :minutes (apply str (decimal-parts (.getMinutes clock)))
     :seconds (apply str (decimal-parts (.getSeconds clock)))}))

(defn update-clock! []
  (swap! world assoc-in [:clock] (get-time)))

(js/setInterval update-clock! 1000)

(defonce run-once
  (ws/init (fn [msg]
             (js/console.log (str (-> msg)))
             (swap! world assoc-in [:queues] (:queues msg)))))

;; @world
