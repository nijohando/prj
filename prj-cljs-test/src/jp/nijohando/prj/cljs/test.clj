(ns jp.nijohando.prj.cljs.test
  (:require [jp.nijohando.prj.cljs :as prj-cljs]
            [jp.nijohando.prj.cljs.nodejs :as prj-cljs-nodejs]))

(defn- generate-test-runner-cljs
  [test-case-symbols]
  (let [require-args (for [s test-case-symbols] [s])
        run-tests-args (for [t test-case-symbols] `'~t)]
    [`(~'ns ~'jp.nijohando.prj.cljs.test.runner
       (:require [~'cljs.test :refer-macros [~'run-tests]]
                 ~@require-args))
     `(~'defn ~'-main [& ~'args]
       (~'enable-console-print!)
       (~'run-tests ~@run-tests-args))
     '(defmethod cljs.test/report [:cljs.test/default :end-run-tests] [m]
        (when-not (cljs.test/successful? m)
          ((aget js/process "exit") 1)))
     '(set! *main-cli-fn* -main)]))

(defn run-tests
  [conf test-case-symbols]
  (prj-cljs/build-cljs conf [(generate-test-runner-cljs test-case-symbols)])
  (let [file (get-in conf [:compiler :output-to])
        args (map str test-case-symbols)]
    (-> (prj-cljs-nodejs/run-script file args)
        :wait-for
        deref
        zero?)))
