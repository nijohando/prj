(ns jp.nijohando.prj.cljs
  (:require [cljs.build.api]
            [clojure.tools.reader.edn :as edn]
            [meta-merge.core :refer [meta-merge]]))

(defn- load-edn-file
  [path]
  (->> (slurp path)
       (edn/read-string)))

(defn compiler-options
  [& paths]
  (->> paths
       (map #(load-edn-file %))
       (apply meta-merge)))

(defn build-cljs
  ([compiler-options]
   (build-cljs compiler-options nil))
  ([compiler-options extra-sources]
   (let [sources (comp #(apply cljs.build.api/inputs %)
                       #(concat % extra-sources)
                       :source-paths)]
     (->> compiler-options
          ((juxt sources :compiler))
          (apply cljs.build.api/build)))))
