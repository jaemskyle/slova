(set-env!
  :source-paths #{"src"}
  :resource-paths #{"src" "resources"}
  :dependencies '[[adzerk/boot-cljs "2.1.4" :scope "test"]
                  [adzerk/boot-reload "0.5.2" :scope "test"]
                  [pandeiro/boot-http "0.8.3" :scope "test"
                   :exclusions [org.clojure/clojure]]
                  [nightlight "1.9.2"]
                  [org.clojure/clojurescript "1.9.946"]
                  [reagent "0.6.0"]
                  [org.clojure/clojure "1.8.0"] 
                  [org.clojure/core.async "0.3.465"]])

(require
  '[adzerk.boot-cljs :refer [cljs]]
  '[adzerk.boot-reload :refer [reload]]
  '[pandeiro.boot-http :refer [serve]]
  '[nightlight.boot :refer [nightlight sandbox]])

(deftask dev []
  (comp
    (watch)
    (reload :asset-path "nightcoders" :cljs-asset-path ".")
    (sandbox :file "java.policy")
    (cljs :source-map true :optimizations :none :compiler-options {:asset-path "main.out"})
    (target)))

(deftask run []
  (comp
    (serve :dir "target/nightcoders" :port 3000)
    (dev)
    (nightlight :port 4000 :url "http://localhost:3000")))

(deftask build []
  (comp (cljs :optimizations :advanced) (target)))

