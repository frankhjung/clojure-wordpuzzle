(ns wordpuzzle.main
  (:gen-class)
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :refer [join]]
            [clojure.java.io :as io]
            [wordpuzzle.core :refer [valid-size? valid-letters? find-valid-words]]))

(defn usage "Wordpuzzle usage"
  [options-summary]
  (join \newline
        [""
         "NAME"
         ""
         "  Solve word puzzles like those at nineletterword.tompaton.com and NYT Spelling Bee"
         ""
         "SYNOPSIS"
         ""
         "  wordpuzzle.main [-h|--help]"
         "  wordpuzzle.main [-d|--dictionary PATH] [-s|--size INT] <-l|--letters STRING> [-r|--repeats]"
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
         "  Copyright © 2022-2026 Frank H Jung, MIT License"]))

(def letters-required "Need 7+ lowercase letters")

(def cli-options "Process command line arguments"
  [["-h" "--help" "This help text"]
   ["-v" "--version" "Show version"]
   ["-d" "--dictionary" "Alternate word dictionary"
    :default "resources/dictionary"
    :required "STRING"
    :validate [#(.exists (io/file %)) "Dictionary file not found"]]
   ["-s" "--size INT" "Minimum word size"
    :required "INT"
    :default 4
    :parse-fn #(Integer/parseInt %)
    :validate [#(valid-size? %) "Must be greater than or equal to 4"]]
   ["-l" "--letters STRING" "[REQUIRED] 7+ lowercase letters to make words"
    :required "STRING"
    :validate [#(valid-letters? %) letters-required]]
   ["-r" "--repeats" "Letters can be repeated (e.g., Spelling Bee)"
    :default false]])

; Required options
(def required-opts "Letters is required" #{:letters})

(defn missing-required? "Check if any required options are missing"
  [opts]
  (not-every? opts required-opts))

(defn error-msg "Show option validation errors "
  [errors]
  (join \newline errors))

(defn project-version "Read project version from pom.properties"
  []
  (or
   (when-let [pom (io/resource "META-INF/maven/wordpuzzle/wordpuzzle/pom.properties")]
     (let [props (java.util.Properties.)]
       (with-open [reader (io/reader pom)]
         (.load props reader))
       (.getProperty props "version")))
   "unknown"))

(defn validate-opts "Check command line arguments"
  [args]
  (let [{:keys [arguments errors options summary]} (parse-opts args cli-options)]
    (cond
      ; show help
      (:help options) {:exit-message (usage summary), :ok? true}
      ; show version
      (:version options) {:exit-message (str "wordpuzzle " (project-version)), :ok? true}
      ; show errors
      errors {:exit-message (error-msg errors), :ok? false}
      ; show usage if arguments provided
      (> (count arguments) 0) {:exit-message (usage summary), :ok? true}
      ; check that letters option provided
      (missing-required? options) {:exit-message (str letters-required (usage summary)), :ok? false}
      ; get words using options provided
      :else
      {:options options})))

(defn exit "Print message and return with status"
  [status msg]
  (println msg)
  (System/exit status))

(defn solve "Solve word puzzle"
  [letters size dictionary repeats]
  (with-open [reader (io/reader dictionary)]
    (let [words (find-valid-words (line-seq reader) letters (str (first letters)) size repeats)]
      (dorun (map println words)))))

(defn -main "Main - read options and solve word puzzle"
  [& args]
  (let [{:keys [options exit-message ok?]} (validate-opts args)]
    (if exit-message
      ; exit with message
      (exit (if ok? 0 1) exit-message)
      ; else get and show words
      (let [{:keys [letters size dictionary repeats]} options]
        (solve letters size dictionary repeats)))))
