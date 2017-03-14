(set-env!
 :source-paths    #{"sass" "src/cljs"}
 :resource-paths  #{"resources"}
 :dependencies '[[adzerk/boot-cljs          "1.7.228-2"  :scope "test"]
                 [adzerk/boot-cljs-repl     "0.3.3"      :scope "test"]
                 [adzerk/boot-reload        "0.4.13"      :scope "test"]
                 [pandeiro/boot-http        "0.7.6"      :scope "test"]
                 [com.cemerick/piggieback   "0.2.1"      :scope "test"]
                 [org.clojure/tools.nrepl   "0.2.12"     :scope "test"]
                 [weasel                    "0.7.0"      :scope "test"]
                 [org.clojure/clojurescript "1.9.293"]
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

(deftask build []
  (comp (speak)
        
        (cljs)
        
        (sass)))

(deftask run []
  (comp (serve)
        (watch)
        (cljs-repl)
        
        (cljs-devtools)
        (reload)
        (build)))

(deftask production []
  (task-options! cljs {:optimizations :advanced}
                      sass   {:output-style :compressed})
  identity)

(deftask development []
  (task-options! cljs {:optimizations :none}
                 reload {:on-jsload 'weekly-log.app/init})
  identity)

(deftask dev
  "Simple alias to run application in development mode"
  []
  (comp (development)
        (run)))


