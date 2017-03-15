(set-env!
 :source-paths    #{"src/cljs"}
 :resource-paths  #{"resources"}
 :dependencies '[[adzerk/boot-cljs          "2.0.0"  :scope "test"]
                 [adzerk/boot-cljs-repl     "0.3.3"  :scope "test"]
                 [adzerk/boot-reload        "0.4.13" :scope "test"]
                 [pandeiro/boot-http        "0.7.6"  :scope "test"]
                 [com.cemerick/piggieback   "0.2.1"  :scope "test"]
                 [org.clojure/tools.nrepl   "0.2.12" :scope "test"]
                 [weasel                    "0.7.0"  :scope "test"]
                 [org.clojure/clojurescript "1.9.494"]
                 [com.google.javascript/closure-compiler-unshaded "1.0-SNAPSHOT"]
                 ;;[com.google.javascript/closure-compiler-unshaded "v20170218"]
                 [rum "0.10.7"]
                 [deraen/boot-sass  "0.3.0" :scope "test"]
                 [org.slf4j/slf4j-nop  "1.7.21" :scope "test"]
                 [binaryage/devtools "0.9.0" :scope "test"]
                 [powerlaces/boot-cljs-devtools "0.2.0" :scope "test"]])

(require
 '[adzerk.boot-cljs      :refer [cljs]]
 '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
 '[adzerk.boot-reload    :refer [reload]]
 '[pandeiro.boot-http    :refer [serve]]
 '[deraen.boot-sass :refer [sass]]
 '[powerlaces.boot-cljs-devtools :refer [cljs-devtools dirac]])

;; (require 'boot.pod)
;; (boot.pod/add-classpath "/Users/martin/code/closure-compiler/target/closure-compiler-1.0-SNAPSHOT.jar")

(deftask build []
  (comp (speak)
        (cljs)
        (sass)))

(deftask run []
  (comp (serve)
        (watch)
        ;; (cljs-repl)
        (cljs-devtools)
        (reload)
        (build)))


(require '[clojure.java.io :as io]
         '[clojure.pprint :as pprint]
         '[cljs.build.api :as b])

(defn compute-foreign-libs []
  (let [deps (->> (b/node-inputs [{:file "node_deps.js"}])
                  (into [] (map (fn [{:keys [file] :as ijs}]
                                  ;; Hacky as hell
                                  (cond-> ijs
                                    (.contains file "mobiledoc-kit/dist/commonjs/mobiledoc-kit/index.js")
                                    (assoc :provides ["mobiledoc-kit"])

                                    (.contains file "mobiledoc-dom-renderer/dist/commonjs/mobiledoc-dom-renderer/index.js")
                                    (assoc :provides ["mobiledoc-dom-renderer"])

                                    (.contains file "mobiledoc-text-renderer/dist/commonjs/mobiledoc-text-renderer/index.js")
                                    (assoc :provides ["mobiledoc-text-renderer"]))))))]
    (prn (filter :provides deps))
    (pprint/pprint deps (io/writer "deps.edn"))))

(deftask production []
  (task-options! cljs {:optimizations :advanced}
                 sass {:output-style :compressed})
  identity)

(def cljsjs-mobiledoc
  [{:file "node_modules/mobiledoc-kit/dist/global/mobiledoc-kit.js"
    :provides ["cljsjs.mobiledoc-kit"]}
   {:file "node_modules/mobiledoc-dom-renderer/dist/global/mobiledoc-dom-renderer.js"
    :provides ["cljsjs.mobiledoc-dom-renderer"]}
   {:file "node_modules/mobiledoc-text-renderer/dist/global/mobiledoc-text-renderer.js"
    :provides ["cljsjs.mobiledoc-text-renderer"]}])

(deftask development []
  (task-options! cljs {:optimizations :none
                       :compiler-options {:foreign-libs  #_cljsjs-mobiledoc (read-string (slurp "deps.edn"))
                                          :pretty-print true
                                          :closure-warnings {:non-standard-jsdoc :off
                                                             :global-this :off}}}
                 reload {:on-jsload 'weekly-log.app/init})
  identity)

(deftask dev
  "Simple alias to run application in development mode"
  []
  (comp (development)
        (run)))


