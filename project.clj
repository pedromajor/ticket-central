(defproject ticket-central "0.1.0-SNAPSHOT"
  :description "ticket system demo"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.293" :exclusions [org.apache.ant/ant]]
                 [org.clojure/core.async "0.2.395" :exclusions [org.clojure/tools.reader]]
                 [http-kit "2.2.0"]
                 [com.stuartsierra/component "0.3.2"]
                 [compojure "1.5.1"]
                 [ring/ring-json "0.4.0"]
                 [cljs-ajax "0.5.8"]
                 [quiescent "0.3.2"]
                 [ring "1.5.0"]
                 [jarohen/chord "0.7.0"]
                 [com.cemerick/url "0.1.1"]
                 [ring/ring-json "0.4.0"]]

  :plugins [[lein-cljsbuild "1.1.4"]
            [lein-figwheel "0.5.8"]]

  :main tickets.core
  :source-paths ["src/clj"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :profiles {:dev {:dependencies [[reloaded.repl "0.2.3"]
                                  [figwheel-sidecar "0.5.8"]
                                  [com.cemerick/piggieback "0.2.1"]]
                   :source-paths ["dev"]}}

  :repl-options {:init-ns user
                 :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

  :cljsbuild {:builds
              [{:id "dev"
                :figwheel true
                :source-paths ["src/cljs"]
                :compiler {:main "tickets.frontend"
                           :asset-path "/js/out"
                           :output-to "resources/public/js/app.js"
                           :output-dir "resources/public/js/out"}}
               {:id "prod"
                :source-paths ["src/cljs"]
                :compiler {:output-to "resources/public/js/app.js"
                           :main tickets.frontend
                           :optimizations :advanced
                           :pretty-print false}}]})
