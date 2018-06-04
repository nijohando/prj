(ns jp.nijohando.prj.cljs.repl-figwheel
  (:require [figwheel-sidecar.repl-api :as ra]
            [jp.nijohando.prj.core :refer [work-dir]]
            [jp.nijohando.prj.cljs.nodejs :as prj-cljs-nodejs]))

(defn run-figwheel
  [conf opts]
  (ra/start-figwheel!
   {:figwheel-options opts
    :build-ids        [:figwheel]
    :all-builds       [(merge conf {:id :figwheel
                                    :figwheel true})]}))

(defn start-repl
  [conf]
   (let [jsfile-path (get-in conf [:compiler :output-to])]
     (run-figwheel conf {:server-logfile (str work-dir "/figwheel_server.log")})
     (let [p (prj-cljs-nodejs/run-script jsfile-path)]
       (ra/cljs-repl)
       (ra/stop-figwheel!)
       @(:stop p))))

