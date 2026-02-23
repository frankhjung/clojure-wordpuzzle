(ns wordpuzzle.core
  (:require [clojure.java.io :as io]
            [clojure.string :refer [includes?]]))

(defn valid-size? "Minimum word size is 4 letters"
  [size] (>= size 4))

(defn valid-letters? "Must be 7+ lowercase letters"
  [letters] (boolean (re-matches #"[a-z]{7,}" letters)))

(defn spelling-bee?
  "Return true when `word` can be constructed from the provided `letters`,
   with letters able to be repeated."
  [letters word]
  (let [avail (set letters)]
    (every? #(contains? avail %) word)))

(defn nine-letters?
  "Return true when `word` can be constructed from the provided `letters`.
  The algorithm computes character frequencies so the result handles repeated
  characters correctly and avoids the explicit recursion used previously."
  [letters word]
  (let [avail (frequencies letters)]
    (every? (fn [[ch cnt]]
              (<= cnt (get avail ch 0)))
            (frequencies word))))

(defn get-words "Get list of valid words from the dictionary"
  [letters size dictionary repeats?]
  (with-open [reader (io/reader dictionary)]
    (let [max-len (if repeats? Integer/MAX_VALUE (count letters))
          mandatory-letter (str (first letters))
          valid-word? (if repeats?
                        #(spelling-bee? letters %)
                        #(nine-letters? letters %))]
      (->> (line-seq reader) ; read all words from dictionary
           (filter #(<= size (count %))) ; minimum size
           (filter #(<= (count %) max-len)) ; never longer than available letters
           (filter #(includes? % mandatory-letter)) ; has mandatory letter
           (filter valid-word?) ; is a valid word
           (set)))))
