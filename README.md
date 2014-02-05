# EECS 391 - Programming Assignment 1
### Authors: Josh Braun (jxb532), Wes Rupert (wkr3)

This is the first programming assignment for Case Western Reserve University's EECS 391 Introduction to Artificial Intelligence course. The project requires CWRU's SEPIA AI engine to run.

To run the assignment parts, execute the following:

* Familiarization with SEPIA
```bat
javac -cp "Sepia.jar" RCAgent.java
java -cp "Sepia.jar;." edu.cwru.sepia.Main2 RCConfig.xml
```

* Pathfinding
```bat
javac -cp "Sepia.jar" SearchAgent.java GraphNode.java WeightedNode.java
java -cp "Sepia.jar;." edu.cwru.sepia.Main2 mazeConfig.xml
```
