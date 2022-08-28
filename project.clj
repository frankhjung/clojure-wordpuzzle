;; https://github.com/technomancy/leiningen/blob/stable/sample.project.clj
(defproject wordpuzzle "0.1.0"
  :description "Clojure solution to 9-letter word puzzle"
  :url "https://gitlab.com/frankhjung1/clojure-wordpuzzle"
  :license {:name "GNU General Public License (GPLv3)"
            :url "https://www.gnu.org/licenses/gpl-3.0.txt"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [org.clojure/tools.cli "1.0.206"]]
  :plugins [[lein-eftest "0.5.9"]]
  :main ^:skip-aot wordpuzzle.main
  :target-path "target/%s"
  :profiles {:dev {:aliases {"build" ["do" "check," "eftest," "run"]}}
             :cicd {:local-repo ".m2/repository"}}
  :clean-targets [:target-path]
  :aliases {"build" ["do" "check," "eftest," "run"]})
