# afnd
Implementaçã́o de um simulador de autômato finito não-determinístico 
apresentado na disciplina de Linguagens Formais e Autômatos do curso de 
Ciência da computação.
 
A entrada para o autômato é a definição de um autômato e uma string, a saída
do simulador deve ser se aceita a string ou não.

## Usage

Primeiramente deve-se ter instalado o Clojure e o Leiningen [Clojure](https://clojure.org/guides/getting_started) [Leiningen](https://leiningen.org/#install).


No diretório raiz do projeto executar o comando  
```
lein run
```

##### As entradas para o simulador (Exemplos)
O alfabeto em letras minúsculas ou números, separados por vírgula ou espaço. Ex.  
```
a b
```

Os estados do autômato, separados por vírgula ou espaço. Ex.  
```
S A B
```

O estado inicial do autômato. Ex.  
```
S
```

O(s) estado(s) final(ais) do autômato, separados por vírgula ou espaço. Ex.  
```
A B
```

As funções de transições, seguindo o seguinte padrão. Ex.
```
S -> aA aB bB
A -> aA bB
B -> bB
```
Ou
```
S -> aA|aB|bB
A -> aA|bB
B -> bB
```

A string para ser avaliada. Ex.
```
aaba
```

## Código

No arquivo core.clj estão as funções do simulador.  

Primeiro faz um require do namespace de string. ``linha 1``
```clojure
(ns afnd.core
  (:require [clojure.string :as s]))
```

A função ``filterv-not-empty`` filtra o vetor recebido por parâmetro 
removendo items vazios. ``linha 4``
```clojure
(defn filterv-not-empty
  [data]
  (filterv (fn [x]
             (not-empty x)) data))
```

A função ``read-data-afnd`` é uma função geral que faz a leitura do teclado, 
cria um vetor do conteúdo lido, usando vírgula ou espaço como delimitador, 
e chama a função de filtro ``filterv-not-empty``. ``linha 9``  
```clojure
(defn read-data-afnd
  []
  (let [data (read-line)]
    (filterv-not-empty (s/split data #",|\s"))))
```

As funções ``read-alphabet``, ``read-states``, ``read-initial-state`` e 
``read-final-states``, fazem a leitura dos respectivos conteúdos, alfabeto, 
estados, estado inicial, e estado final. ``linha 14``
```clojure
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
```

A função ``read-string-chain`` faz a leitura da string para ser avaliada, 
e retorna um vetor com cada caracter sendo um item do vetor. ``linha 35``  
```clojure
(defn read-string-chain
  []
  (println "Digite a string (Ex. aabbb)")
  (let [chain (read-line)]
    (s/split chain #"")))
```

A função ``mount-rule`` faz um parse de cada regra das funções de transição 
e retorna um vetor, por exemplo a regra ``S -> aA|bA`` retorna ``["S" "aA" "bA"]``. ``linha 41``  
```clojure
(defn mount-rule
  [rule]
  (let [rule (s/split rule #"[\||\s|\-\>]")]
    (filterv (fn [x]
              (not-empty x)) rule)))
```

A função ``read-rules-grammar`` faz a leitura das funções de transição, 
usando a função ``mount-rule`` retorna um vetor com todas as funções separadas, ``linha 47``  
Ex.  
```
S -> aA| aB| bB  
A -> aB|bB  
B -> bB  
```
Retornaria  
```clojure
[["S" "aA" "aB" "bB"]
 ["A" "aB" "bB"]
 ["B" "bB"]]
```
```clojure
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
```

A função ``mount-part-afnd``, recebe cada função de transição separado 
e tranforma em um ``map`` com o label sendo um keyword e o estado 
corespondente o seu valor. ``linha 63``  
Ex. função de transição ``aA`` retornaria  
```clojure
{:a "A"}
```
```clojure
(defn mount-part-afnd
  [rule]
  (let [state (first rule)
        fn-transictions (mapv define-function-transition (rest rule))]
    {(keyword state) fn-transictions}))
```

A função ``create-afnd`` é responsável por criar um grafo a partir do vetor das  
funções de transição usando um ``map``, com o estado sendo um nó do grafo 
e a função de transição sendo os destinos. ``linha 69``  
Ex. as funções de transição  
```clojure
[["S" "aA" "aB" "bB"]
 ["A" "aB" "bB"]
 ["B" "bB"]]
```
Retornaria  
```clojure
{:S [{:a "A"} {:a "B"} {:b "B"}]
 :A [{:a "B"} {:b "B"}]
 :B [{:b "B"}]}
```

A função ``read-automaton-data`` cria um ``map`` com os de entrada do simulador 
recebido do teclado. ``linha 76``  
```clojure
(defn read-automaton-data
  []
  {:alphabet (read-alphabet)
   :states (read-states)
   :initial-state (read-initial-state)
   :final-states (read-final-states)
   :rules-grammar (read-rules-grammar [])
   :string (read-string-chain)})
```

A função ``identifier-chain`` é responsável por percorrer o grafo gerado 
pela função ``create-afnd`` identificando os caracteres da string, caso 
chegue no final da string em um estado final, a string é aceita, se a string não 
for aceita não retorna nada. ``linha 89``  
Primeiramente recupera o primeiro caracter da string e tranforma em um keyword, 
depois recupera os estados destinos a partir do estado atual, e após recupera os 
estados destinos a apartir do estado atual pelo label atual, verifica se tem algum 
caminho de destino, caso tenha, verifica se é o fim da string e se está em um estado 
final, caso isso ocorra, mostra a mesnsagem de "String aceira", senão faz o mesmo 
precesso recursivamente percorrendo o grafo até o fim da string, se a string não for 
aceita, não retorna nada.
```clojure
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
```

A função ``main`` chama as funções de leitura dos dados, criação do automato 
e chama função ``identifier-chain`` para iniciar o processo de identificação 
dos caracteres. ``linha 101``  
```clojure
(defn main
  []
  (let [automaton-data (read-automaton-data)
        afnd (create-afnd {} (:rules-grammar automaton-data))
        string (:string automaton-data)
        initial-state (:initial-state automaton-data)
        final-states (:final-states automaton-data)]
        (identifier-chain string afnd initial-state final-states)))
```













