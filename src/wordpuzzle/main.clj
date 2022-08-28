(ns wordpuzzle.main
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :refer [join]]
            [wordpuzzle.library :refer [valid-size? valid-letters? get-words]])
  (:gen-class))

(defn usage "Wordpuzzle usage message"
  [options-summary]
  (->> ["USAGE wordpuzzle.main [options]"
        ""
        "DESCRIPTION"
        ""
        "Solve word puzzles like those at nineletterword.tompaton.com"
        "List all valid words given a a list of letters.  Each word must"
        "contain the mandatory letter, which is the first in the list."
        ""
        "OPTIONS"
        ""
        options-summary
        ""
        "Copyright © 2022 Frank H Jung, GPLv3.0"]
       (join \newline)))

(def cli-options "Process command line arguments"
  [["-d" "--dictionary" "Alternate word dictionary"
    :default "resources/dictionary"
    :required "STRING"]
   ["-s" "--size INT" "Minimum word size of 1 to 9 letters"
    :required "INT"
    :default 4
    :parse-fn #(Integer/parseInt %)
    :validate [#(valid-size? %) "Must be a value from 1 to 9"]]
   ["-l" "--letters" "Nine letters to make words"
    :required "STRING"
    :validate [#(valid-letters? %) "Must be 9 lowercase letters only"]]
   ["-h" "--help" "This help text"]])

(defn error-msg "Print errors"
  [errors]
  (str "ERROR: "
       (join \newline errors)))

(defn validate-args "Check command line arguments."
  [args]
  (let [{:keys [arguments errors options summary]} (parse-opts args cli-options)]
    (cond
      ; help
      (:help options)
      {:exit-message (usage summary) :ok? true}
      ;; errors
      errors
      {:exit-message (error-msg errors) :ok? false}
      ;; no arguments but have letters so pass options to main
      (and (= 0 (count arguments)) (not-empty (:letters options)))
      {:options options} ; return with user options
      :else ; failed custom validation so exit with usage summary
      {:exit-message (usage summary)})))

(defn exit "Print message and return with status"
  [status msg]
  (println msg)
  (System/exit status))

(defn -main "Main - process arguments and show matching words"
  [& args]
  (let [{:keys [options exit-message ok?]} (validate-args args)]
    (if exit-message
      (exit (if ok? 0 1) exit-message) ; have an error
      ; else get and show words
      (let [{:keys [letters size dictionary]} options
            words (get-words letters size dictionary)]
        (dorun (map println words))))))
