(defproject wordpuzzle "0.2.0"
  :description "Clojure solution to letter word puzzles"
  :url "https://gitlab.com/frankhjung1/clojure-wordpuzzle"
  :license {:name "MIT License"
            :url "https://spdx.org/licenses/MIT.html"}
  :dependencies [[org.clojure/clojure "1.12.4"]
                 [org.clojure/tools.cli "1.0.206"]]
  :plugins [[lein-eftest "0.6.0"]
            [lein-ancient "0.7.0"]]
  :main ^:skip-aot wordpuzzle.main
  :target-path "target/%s"
  :profiles {:dev {:aliases {"build" ["do" "check," "eftest," "run"]}
                   :plugins [[lein-cljfmt "0.6.8"]]}
             :cicd {:local-repo ".m2/repository"}
             :uberjar {:aot :all}}
  :clean-targets [:target-path]
  :aliases {"build" ["do" "check," "eftest," "run"]})
