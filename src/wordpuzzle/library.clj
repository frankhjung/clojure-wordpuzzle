(ns wordpuzzle.library
  (:require [clojure.java.io :as io]
            [clojure.string :refer [replace-first includes?]]
            [clojure.spec.alpha :refer [int-in-range?]]))

(defn valid-size? "Minimum word size from 1..9 letters"
  [size] (int-in-range? 1 (inc 9) size))

(defn valid-letters? "Must be 9 lowercase letters"
  [letters] (boolean (re-matches #"[a-z]{9}" letters)))

(defn valid-word? "Check if a word contains only valid characters"
  [letters word]
  (let [[x & xs] letters]
    (cond
      (empty? word) true
      (empty? letters) false
      :else (recur xs (replace-first word x "")))))

(defn get-words "Get list of valid words from the dictionary"
  [letters size dictionary]
  (with-open [reader (io/reader dictionary)]
    (->> (line-seq reader) ; read all words from dictionary
         (filter #(<= size (count %))) ; of correct size
         (filter #(includes? % (str (first letters)))) ; has mandatory letter
         (filter #(valid-word? letters %)) ; is a valid lowercase word
         (set))))
