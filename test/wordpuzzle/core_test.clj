(ns wordpuzzle.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [wordpuzzle.core :refer [get-words
                                     valid-letters?
                                     valid-size?
                                     valid-word?]]))

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

(deftest test-valid-word
  (testing "word contains valid letters"
    (is (valid-word? "foobar" "barfoo")))
  (testing "word contains valid subset"
    (is (valid-word? "foobar" "for")))
  (testing "invalid word"
    (is (not (valid-word? "foobar" "bartez"))))
  (testing "invalid letter frequency"
    (is (not (valid-word? "foobar" "baafor")))))

(deftest test-get-words
  (testing "returns expected words"
    (let [expected #{"varicose" "sidecar" "divorce" "discover" "divorces" "viscera"}
          actual (get-words "cadevrsoi" 7 "resources/dictionary")]
      (is (= expected actual))))
  (testing "words longer than letters are ignored"
    (let [letters "abcd"
          results (get-words letters 1 "resources/dictionary")]
      (is (every? #(<= (count %) (count letters)) results)))))
