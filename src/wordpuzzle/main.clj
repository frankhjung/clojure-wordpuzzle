(ns wordpuzzle.main
  (:gen-class)
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :refer [join]]
            [wordpuzzle.library :refer [valid-size? valid-letters? get-words]]))

(defn usage "Wordpuzzle usage message"
  [options-summary]
  (join \newline
        ["NAME"
         ""
         "  Solve word puzzles like those at nineletterword.tompaton.com"
         ""
         "SYNOPSIS"
         ""
         "  wordpuzzle.main [-h|--help]"
         "  wordpuzzle.main [-d|--dictionary PATH] [-s|--size INT] <-l|--letters STRING>"
         ""
         "DESCRIPTION"
         ""
         "  List all valid words using provided letters.  Each word must contain"
         "  the mandatory letter which is the first character in the letters list."
         ""
         "OPTIONS"
         ""
         options-summary
         ""
         "LICENSE"
         ""
         "  Copyright © 2022 Frank H Jung, GPLv3.0"]))

(def cli-options "Process command line arguments"
  [["-h" "--help" "This help text"]
   ["-d" "--dictionary" "Alternate word dictionary"
    :default "resources/dictionary"
    :required "STRING"]
   ["-s" "--size INT" "Minimum word size of 1 to 9 letters"
    :required "INT"
    :default 4
    :parse-fn #(Integer/parseInt %)
    :validate [#(valid-size? %) "Must be a value from 1 to 9"]]
   ["-l" "--letters" "[REQUIRED] 9 lowercase letters to make words"
    :required "STRING"
    :validate [#(valid-letters? %) "Must be 9 lowercase letters"]]])

; Required options
(def required-opts "Letters is required" #{:letters})

(defn missing-required? "Check if any required options are missing"
  [opts]
  (not-every? opts required-opts))

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
      ; show usage if arguments provided
      (> (count arguments) 0) {:exit-message (usage summary), :ok? true}
      ; check letters option provided
      (missing-required? options) {:exit-message (usage summary), :ok? false}
      ; else - get words using options provided
      :else
      {:options options})))

(defn exit "Print message and return with status"
  [status msg]
  (println msg)
  (System/exit status))

(defn -main "Main - process arguments and show matching words"
  [& args]
  (let [{:keys [options exit-message ok?]} (validate-args args)]
    (if exit-message
      (exit (if ok? 0 1) exit-message)
      ; else get and show words
      (let [{:keys [letters size dictionary]} options
            words (get-words letters size dictionary)]
        (dorun (map println words))))))
