(ns jp.nijohando.prj.test
  (:require [clojure.test :as t]))

(defn run-tests
  [& test-case-symbols]
  (doseq [s test-case-symbols]
    (require s :reload-all))
  (apply t/run-tests test-case-symbols))
