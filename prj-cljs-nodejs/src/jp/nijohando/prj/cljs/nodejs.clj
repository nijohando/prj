(ns jp.nijohando.prj.cljs.nodejs
  (:require [cljs.build.api]
            [jp.nijohando.prj.core :refer [env*]])
  (:import (java.lang ProcessBuilder$Redirect)))

(defn npm-install
  [conf]
  (let [npm-deps (get-in conf [:compiler :npm-deps])]
    (println "Install npm dependencies...")
    (cljs.build.api/install-node-deps! npm-deps conf)
    (println "Done.")))

(defn run-script
  ([jsfile-path]
   (run-script jsfile-path nil))
  ([jsfile-path args]
   (let [node (or (env* :node-bin) "node")
         command (concat [node jsfile-path] args)
         p (-> (ProcessBuilder. command)
               (.redirectOutput ProcessBuilder$Redirect/INHERIT)
               (.redirectError ProcessBuilder$Redirect/INHERIT)
               (.start))]
     {:wait-for (delay (.waitFor p) (.exitValue p))
      :stop (delay (.destroy p))})))

