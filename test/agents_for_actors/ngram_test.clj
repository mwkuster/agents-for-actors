(ns agents-for-actors.ngram-test
  (:use clojure.test
        agents-for-actors.ngram))

(deftest ngram-test
  (testing "NGram functionality"
    (is 
     (= (ngrams 2 "abcdef") '("_a" "ab" "bc" "cd" "de" "ef" "f_"))
    )
    (is 
     (= (ngrams 2 "abc def") '("_a" "ab" "bc" "c_" "_d" "de" "ef" "f_"))
    )
    (is 
     (= (ngrams 2 "") '("__"))
    )
    (is 
     (= (ngrams 3 "abcdef") '("__a" "_ab" "abc" "bcd" "cde" "def" "ef_" "f__"))
    )
  )
)

(deftest quadgram-test
  (testing "quadgrams"
    (is (= (ngrams 4 "abcdef") (quadgrams "abcdef")))))

(deftest dice-coefficient-test
  (testing "dicecoefficient"
    (let
        [set1 #{"ab" "bc" "cd"}
         set2 #{"ab" "ef"}
         set3 #{"ab"}
         set4 #{}
         set5 #{"hi"}]
      (is (= (dice-coefficient set1 set2) 0.4))
      (is (= (dice-coefficient set1 set3) 0.5))
      (is (= (dice-coefficient set1 set4) 0.0))
      (is (= (dice-coefficient set1 set5) 0.0))
      (is (= (dice-coefficient set2 set3) (/ 2.0 3.0))))))

(deftest generate-token-sequences
  (testing "Generate Token Sequences"
    (is (= (generate-token-sequences '("ab" "cd" "hij") 2 true)
           '("ab cd" "cd hij" "hij")))
    (is (= (generate-token-sequences '("ab" "cd" "hij") 2 false)
           '("ab cd" "hij")))))