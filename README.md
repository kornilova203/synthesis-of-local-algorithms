# Synthesis of Local Algorithms

## What is a Local Algorithm?
A _Distributed Algorithm_ is an algorithm which runs on a distributed system, that does not have a central coordinator.  
A _Local Algorithm_ is a Distributed algorithm that runs in constant time, independently of the size of the network.

## What kind of problems do these algorithms solve?
This research is focused on LCLs or locally checkable labeling problems in the LOCAL model of computation.  
 Examples of such problems:
 * maximal independent set
 * maximal matching
 * vertex colouring
 * edge colouring

## About Algorithm Synthesis
The idea of the project is to use computer search to find new optimal local algorithms on 2-dimensional torus grids.

The basic structure of synthesized algorithm is the following:
1. Get specific normal form of the grid
2. Find right state for each vertex/edge base on constant size neighbourhood of vertex/edge

The goal of computer search is to find such normal form and radius of a neighbourhood so it is possible to find a mapping from a neighbourhood to right state for each vertex/edge.

## What has been done
Reproduced some results of the paper [LCL problems on grids](https://users.ics.aalto.fi/suomela/doc/grid-lcl.pdf):  
Found algorithm that solves 4-colouring on 2-dimensional torus grids.

