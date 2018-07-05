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
  (println "Digite o alfabeto (a b)")
  (let [alphabet (read-data-afnd)]
    alphabet))

(defn read-states
  []
  (println "Digite os estados do autômato (Ex. S A B")
  (read-data-afnd))

(defn read-initial-state
  []
  (println "Digite o estado inicial (Ex. S)")
  (keyword (first (read-data-afnd))))

(defn read-final-states
  []
  (println "Digite os estados finais (Ex. B)")
  (mapv #(keyword %) (read-data-afnd)))

(defn read-string-chain
  []
  (println "Digite a string (Ex. aabbb)")
  (let [chain (read-line)]
    (s/split chain #"")))

(defn mount-rule
  [rule]
  (let [rule (s/split rule #"[\||\s|\-\>]")]
    (filterv (fn [x]
              (not-empty x)) rule)))

(defn read-rules-grammar
  [rules]
  (println "Digite as funções de transição (Ex. S -> aA bB ou S->aA|bB")
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

(defn presents-recognition
  []
  (println "\nString aceita!"))

(defn identifier-chain
  [chain afnd state final-states]
  (let [label (keyword (first chain))
        fn-transactions (state afnd)
        states (filterv #(not (nil? %)) (map #(keyword (label %)) fn-transactions))]
    (if (empty? states)
      false
      (mapv (fn [state]
	            (if (and (empty? (rest chain)) (.contains final-states state)) 
	              (presents-recognition)
	              (identifier-chain (rest chain) afnd state final-states))) states))))

(defn main
  []
  (let [automaton-data (read-automaton-data)
        afnd (create-afnd {} (:rules-grammar automaton-data))
        string (:string automaton-data)
        initial-state (:initial-state automaton-data)
        final-states (:final-states automaton-data)]
        (identifier-chain string afnd initial-state final-states)))
