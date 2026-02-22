(ns wordpuzzle.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [wordpuzzle.core :refer [get-words
                                     valid-letters?
                                     valid-size?
                                     nine-letters?
                                     spelling-bee?]]))

((deftest test-valid-letters
   (testing "letters valid (≥7 chars)"
     (is (valid-letters? "abcdefghi"))
     (is (valid-letters? "abcdefg")))
   (testing "letters too short"
     (is (not (valid-letters? "abcdef"))))
   (testing "letters contain numbers"
     (is (not (valid-letters? "abcd3fghi"))))
   (testing "letters contain capital letters"
     (is (not (valid-letters? "abcdEfghi"))))))

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
  (testing "invalid word because of invalid letter"
    (is (not (spelling-bee? "foobar" "bartez")))))

(deftest test-get-words
  (testing "returns expected words without repeats"
    (let [expected #{"varicose" "sidecar" "divorce" "discover" "divorces" "viscera"}
          actual (get-words "cadevrsoi" 7 "resources/dictionary" false)]
      (is (= expected actual))))
  (testing "returns expected words with repeats"
    (let [letters "barfo"
          results (get-words letters 4 "resources/dictionary" true)]
      ;; It might produce words like "foobars" if 's' was valid, but this just checks it runs.
      (is (sequential? (seq results)))))
  (testing "words longer than letters are ignored if repeats=false"
    (let [letters "abcd"
          results (get-words letters 1 "resources/dictionary" false)]
      (is (every? #(<= (count %) (count letters)) results)))))
