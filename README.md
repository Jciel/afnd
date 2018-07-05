# afnd
 Implementation of a finite automaton presented in the discipline Formal Languages ​​and Automata of the course of Computer Science.

 
Implementation of a simulator of finite non-deterministic automata.  
The input to the simulator is the definition of an automaton and an input string for the automaton.  
The output of the simulator shall whether or not the automaton accepts the input string.

## Usage

First you must have Clojure and Leiningen installed [Clojure](https://clojure.org/guides/getting_started) and
[Leiningen](https://leiningen.org/#install).


Enter in the root project directory and run
```
lein run
```

##### Input (just examples :P)
The alphabet of automaton. Ex.  
```
a b
```

The states of automaton. Ex.  
```
S A B
```

The initial state. Ex.  
```
S
```

The final states. Ex.  
```
A B
```

The transition functions. Ex.  
```
S -> aA aB bB
A -> aA bB
B -> bB
```
Or
```
S -> aA|aB|bB
A -> aA|bB
B -> bB
```

The input string. Ex.  
```
aaba
```


The function ``create-afnd`` create one graph in this format.
```clojure
{:S [{:a A} {:a B} {:b B}]
 :A [{:a A} {:b B}]
 :B [{:b B}]}
```

And the function ``identifier-chain`` run the graph reading and identifie the character of input 
string and return ``true`` if recognized the string and ``nill`` if not.