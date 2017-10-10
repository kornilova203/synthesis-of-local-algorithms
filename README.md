# Formal Distributed Algorithm

Simulator of distributed algorithms in the port-numbering model.

Basic classes which must be implemented for any algorithm are in [simulator package](java/main/com/github/kornilova_l/formal_da/simulator).

## BMM algorithm

The algorithm finds maximal matching in bipartite graph.

Implementation of BMM algorithm is [here](java/main/com/github/kornilova_l/formal_da/implementation/BMM).

## VC3 algorithm

The algorithm find 3-approximation of minimal vertex cover using BMM as subroutine.

Implementation of VC3 algorithm is [here](java/main/com/github/kornilova_l/formal_da/implementation/VC3).

## Run from command line

```bash
cd java/main
mkdir out
javac -d out -cp ../../.idea/lib/annotations-java8.jar com/github/kornilova_l/formal_da/**/*.java
cd out
```
To run BMM and VC3 you should specify input file. Rules for input data can be found in [BmmAlgorithmRunner](java/main/com/github/kornilova_l/formal_da/implementation/BMM/BmmAlgorithmRunner.java) class and in [V3cAlgorithmRunner](java/main/com/github/kornilova_l/formal_da/implementation/VC3/Vc3AlgorithmRunner.java) class respectively. There are test data in [test_resources](test_resources) dir.

### BMM
```
java com.github.kornilova_l.formal_da.BmmMain "../../../test_resources/BMM/01.txt"
java com.github.kornilova_l.formal_da.BmmMain "../../../test_resources/BMM/02.txt"
```

### VC3
```
java com.github.kornilova_l.formal_da.Vc3Main "../../../test_resources/VC3/01.txt"
java com.github.kornilova_l.formal_da.Vc3Main "../../../test_resources/VC3/02.txt"
```

