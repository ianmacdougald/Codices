# CodexIan

CodexIan establishes a framework for developing classes in sclang that contain scriptable modules whose definitions are largely arbitrary. For instance, a class written to sequence patterns in a routine might implement only the routine, leaving the patterns themselves to be defined in external scripts. As a result, an object of this class can support entirely independent musical processes without comprising its functionality. In this way, the framework creates a best-of-both-worlds situation, balancing the specificity of a compiled class's interface with the open-ended potential of scripting. 

For more on how to work with CodexIan, consult the HelpSource guide.

## Installation

In SuperCollider, evaluate the following: 

`Quarks.install("https://github.com/ianmacdougald/CodexIan");`
