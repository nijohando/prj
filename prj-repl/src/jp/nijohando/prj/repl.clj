(ns jp.nijohando.prj.repl
  (:require [clojure.tools.nrepl.server :refer [start-server stop-server]]
            [cider.nrepl :refer [cider-nrepl-handler]]))

(def ^:private nrepl-server-running? (ref false))
(def ^:private nrepl-server-agent (agent nil))

(defn- find-available-port
  []
  (with-open [s (java.net.ServerSocket. 0)]
    (.getLocalPort s)))

(defn start-repl
  ([]
   (start-repl nil))
  ([init-ns]
   (let [args (when init-ns
                [:init #(do (in-ns init-ns)
                            (clojure.core/use 'clojure.core)
                            (use init-ns))])]
     (apply clojure.main/repl args))))

(defn start-nrepl-server
  []
  (dosync
   (when-not @nrepl-server-running?
     (ref-set nrepl-server-running? true)
     (send-off nrepl-server-agent
               (fn [_]
                 (let [port (find-available-port)
                       server (start-server :port port :handler cider-nrepl-handler)]
                   (spit ".nrepl-port" port)
                   server))))))

(defn stop-nrepl-server
  []
  (dosync
   (when @nrepl-server-running?
     (ref-set nrepl-server-running? false)
     (send-off nrepl-server-agent
               (fn [server]
                 (stop-server server))))))
