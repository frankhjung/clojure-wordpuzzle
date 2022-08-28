(ns wordpuzzle.library-test
  (:require [clojure.test :refer [deftest is testing]]
            [wordpuzzle.library :refer [get-words
                                        valid-letters?
                                        valid-size?
                                        valid-word?]]))

((deftest test-valid-letters
   (testing "letters valid"
     (is (valid-letters? "abcdefghi")))
   (testing "letters contain numbers"
     (is (not (valid-letters? "abcd3fghi"))))
   (testing "letters contain capital letters"
     (is (not (valid-letters? "abcdEfghi"))))))

(deftest test-valid-size
  (testing "size 7 in range"
    (is (valid-size? 7)))
  (testing "size in range"
    (is (every? true? (map #(valid-size? %) (range 1 10)))))
  (testing "size too low"
    (is (every? false? (map #(valid-size? %) (range -10 0)))))
  (testing "size too high"
    (is (every? false? (map #(valid-size? %) (range 10 20))))))

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
      (is (= expected actual)))))
