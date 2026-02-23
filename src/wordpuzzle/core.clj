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
          avail (if repeats? (set letters) (frequencies letters))
          valid-word? (if repeats?
                        ;; with repeats allowed we only need to check that each
                        ;; character in the word appears in the set of letters.
                        (fn [word] (every? #(contains? avail %) word))
                        ;; otherwise compare frequencies the same way as
                        ;; `nine-letters?` does above, but captured in a
                        ;; closure over `avail`.
                        (fn [word] (every? (fn [[ch cnt]]
                                             (<= cnt (get avail ch 0)))
                                           (frequencies word))))]
      (->> (line-seq reader) ; read all words from dictionary
           (filter #(<= size (count %))) ; minimum size
           (filter #(<= (count %) max-len)) ; never longer than available letters
           (filter #(includes? % (str (first letters)))) ; has mandatory letter
           (filter valid-word?) ; is a valid word (arity fixed)
           (set)))))
