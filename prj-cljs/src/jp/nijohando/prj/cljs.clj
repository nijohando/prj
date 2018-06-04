(ns jp.nijohando.prj.cljs
  (:require [cljs.build.api]
            [meta-merge.core :refer [meta-merge]]
            [figwheel-sidecar.repl-api :as ra]
            [jp.nijohando.prj.core :refer [work-dir]])
  (:import (java.lang ProcessBuilder$Redirect)))

(defn merge-config
  [configs profiles]
  (->> configs
       ((apply juxt profiles))
       (apply meta-merge)))

(defn build-cljs
  ([conf]
   (build-cljs conf nil))
  ([conf extra-sources]
   (let [sources (comp #(apply cljs.build.api/inputs %)
                       #(concat % extra-sources)
                       :source-paths)]
     (->> conf
          ((juxt sources :compiler))
          (apply cljs.build.api/build)))))
