(ns afnd.core
  (:require [clojure.string :as s]))

(defn filterv-not-empty
  [data]
  (filterv (fn [x]
             (not-empty x)) data))

(defn read-data-afnd
  []
  (let [data (read-line)]
    (filterv-not-empty (s/split data #",|\s"))))

(defn read-alphabet
  []
  (println "Digite o alfabeto")
  (let [alphabet (read-data-afnd)]
    alphabet))

(defn read-states
  []
  (println "Digite os estados do autômato")
  (read-data-afnd))

(defn read-initial-state
  []
  (println "Digite o estado inicial")
  (keyword (first (read-data-afnd))))

(defn read-final-states
  []
  (println "Digite os estados finais")
  (mapv #(keyword %) (read-data-afnd)))

(defn read-string-chain
  []
  (println "Digite a string")
  (let [chain (read-line)]
    (s/split chain #"")))

(defn mount-rule
  [rule]
  (let [rule (s/split rule #"[\||\s|\-\>]")]
    (filterv (fn [x]
              (not-empty x)) rule)))

(defn read-rules-grammar
  [rules]
  (println "Digite as funções de transição")
  (loop [rules rules]
    (let [rule (read-line)]
      (if-not (empty? rule)
        (do
          (let [real-rule (mount-rule rule)]
            (recur (conj rules real-rule))))
        rules))))

(defn define-function-transition
  [fn-transition]
  (let [fn-part (s/split fn-transition #"")]
    {(keyword (first fn-part)) (last fn-part)}))

(defn mount-part-afnd
  [rule]
  (let [state (first rule)
        fn-transictions (mapv define-function-transition (rest rule))]
    {(keyword state) fn-transictions}))

(defn create-afnd
  [afnd rules-grammar]
  (let [rule-grammar (first rules-grammar)]
    (if (not-empty rule-grammar)
      (recur (conj afnd (mount-part-afnd rule-grammar)) (rest rules-grammar))
      afnd)))

(defn read-automaton-data
  []
  {:alphabet (read-alphabet)
   :states (read-states)
   :initial-state (read-initial-state)
   :final-states (read-final-states)
   :rules-grammar (read-rules-grammar [])
   :string (read-string-chain)})

(defn recognized?
  [result]
  (let [r (first result)]
    (if (true? r)
      r
      (if (vector? r)
        (recur r)))))


(defn identifier-chain
  [chain afnd state final-states]
  (let [label (first chain)
        fn-transactions (state afnd)
        states (filterv #(not (nil? %)) (map #(-> ((keyword label) %)
                                                  keyword) fn-transactions))]
    (if (empty? states)
      false
      (mapv (fn [state]
            (if (and (empty? (rest chain)) (.contains final-states state))
              ;(println "\n\nString reconhecida")
              true
              (identifier-chain (rest chain) afnd state final-states))) states))))

;   {:S [{:a A} {:a B} {:b B}], :A [{:a A} {:b B}], :B [{:a B}]}
;  (identifier-chain ["a" "a" "b" "b" "b"] {:S [{:a "A"}, {:a "B"} {:b "B"}], :A [{:a "A"}, {:b "B"}], :B [{:b "B"}]} :S)

(defn main
  []
  (let [automaton-data (read-automaton-data)
        afnd (create-afnd {} (:rules-grammar automaton-data))
        string (:string automaton-data)
        initial-state (:initial-state automaton-data)
        final-states (:final-states automaton-data)
        res (recognized? (identifier-chain string afnd initial-state final-states))]
    (println res)))