(ns wordpuzzle.main
  (:gen-class)
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :refer [join]]
            [wordpuzzle.library :refer [valid-size? valid-letters? get-words]]))

(defn usage "Wordpuzzle usage message"
  [options-summary]
  (join \newline
        ["USAGE wordpuzzle.main [options] letters"
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
         "ARGUMENTS"
         "  letters  9 lowercase letters [REQUIRED]"
         ""
         "Copyright © 2022 Frank H Jung, GPLv3.0"]))

(def cli-options "Process command line arguments"
  [["-d" "--dictionary" "Alternate word dictionary"
    :default "resources/dictionary"
    :required "STRING"]
   ["-s" "--size INT" "Minimum word size of 1 to 9 letters"
    :required "INT"
    :default 4
    :parse-fn #(Integer/parseInt %)
    :validate [#(valid-size? %) "Must be a value from 1 to 9"]]
   ["-h" "--help" "This help text"]])

(defn error-msg "Show option validation errors "
  [errors]
  (str (join \newline errors)))

(defn validate-args "Check command line arguments"
  [args]
  (let [{:keys [arguments errors options summary]} (parse-opts args cli-options)]
    (cond
      ; help
      (:help options) {:exit-message (usage summary), :ok? true}
      ; errors
      errors {:exit-message (error-msg errors), :ok? false}
      ; require letters argument
      (= 0 (count arguments)) {:exit-message (usage summary), :ok? true}
      ; check 9 valid letters provided
      (valid-letters? (first arguments)) {:letters (first arguments), :options options}
      ; else - failed custom validation so exit with usage summary
      :else
      {:exit-message (usage summary), :ok? false})))

(defn exit "Print message and return with status"
  [status msg]
  (println msg)
  (System/exit status))

(defn -main "Main - process arguments and show matching words"
  [& args]
  (let [{:keys [letters options exit-message ok?]} (validate-args args)]
    (if exit-message
      (exit (if ok? 0 1) exit-message)
      ; else get and show words
      (let [{:keys [size dictionary]} options
            words (get-words letters size dictionary)]
        (dorun (map println words))))))
