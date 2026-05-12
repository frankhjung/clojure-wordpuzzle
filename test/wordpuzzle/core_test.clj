(ns wordpuzzle.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [clojure.java.io :as io]
            [wordpuzzle.core :refer [valid-letters?
                                     valid-size?
                                     nine-letters?
                                     spelling-bee?
                                     find-valid-words]]))

(defn get-words-helper "Test helper to simulate the old get-words behavior"
  [letters size dictionary repeats?]
  (with-open [reader (io/reader dictionary)]
    (find-valid-words (line-seq reader) letters (str (first letters)) size repeats?)))

(deftest test-valid-letters
  (testing "letters valid (≥7 chars)"
    (is (valid-letters? "abcdefghi"))
    (is (valid-letters? "abcdefg")))
  (testing "letters too short"
    (is (not (valid-letters? "abcdef"))))
  (testing "letters contain numbers"
    (is (not (valid-letters? "abcd3fghi"))))
  (testing "letters contain capital letters"
    (is (not (valid-letters? "abcdEfghi")))))

(deftest test-valid-size
  (testing "size 4 in range"
    (is (valid-size? 4)))
  (testing "size in range"
    (is (every? true? (map #(valid-size? %) (range 4 15)))))
  (testing "size too low"
    (is (every? false? (map #(valid-size? %) (range -1 4))))))

(deftest test-nine-letters
  (testing "word contains valid letters"
    (is (nine-letters? "foobar" "barfoo")))
  (testing "word contains valid subset"
    (is (nine-letters? "foobar" "for")))
  (testing "invalid word"
    (is (not (nine-letters? "foobar" "bartez"))))
  (testing "invalid letter frequency"
    (is (not (nine-letters? "foobar" "baafor")))))

(deftest test-spelling-bee
  (testing "word contains valid letters with repeats"
    (is (spelling-bee? "foobar" "foobarrr")))
  (testing "word can reuse a letter that appears once in letters"
    (is (spelling-bee? "bar" "barrrr")))
  (testing "invalid word because of invalid letter"
    (is (not (spelling-bee? "foobar" "bartez")))))

(deftest test-find-valid-words
  (testing "identifies valid words from a sequence"
    (let [dictionary ["apple" "apply" "pale" "plea" "leal" "app"]
          letters "apple"
          mandatory "a"
          size 4
          expected #{"apple" "pale" "plea"}]
      (is (= expected (find-valid-words dictionary letters mandatory size false)))))
  (testing "handles repeats correctly"
    (let [dictionary ["apple" "apply" "pale" "plea" "leal" "app" "peel"]
          letters "aple"
          mandatory "a"
          size 4
          expected #{"apple" "leal" "pale" "plea"}]
      ; "peel" has 'e' twice but no 'a' (mandatory)
      ; "leal" has 'l' twice, and "aple" has 'l'. In Spelling Bee (repeats=true), this is OK.
      ; "apply" has 'y' which is not in 'aple'
      (is (= expected (find-valid-words dictionary letters mandatory size true))))))

(def words7norepeats (set ["discover" "divorce" "divorces" "sidecar" "varicose" "viscera"]))

(def words9repeats (set ["crisscrossed" "crisscrosses" "rediscovered"]))

(deftest test-get-words
  (testing "returns expected words without repeats"
    (let [expected words7norepeats
          actual (get-words-helper "cadevrsoi" 7 "resources/dictionary" false)]
      (is (= expected actual))))
  (testing "returns expected words with repeats"
    (let [expected words9repeats
          actual (get-words-helper "cadevrsoi" 12 "resources/dictionary" true)]
      (is (= expected actual))))
  (testing "words longer than letters are ignored if repeats=false"
    (let [letters "abcd"
          results (get-words-helper letters 4 "resources/dictionary" false)]
      (is (every? #(<= (count %) (count letters)) results)))))
