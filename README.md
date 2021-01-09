# CodexIan

CodexIan establishes a framework for developing modular class interfaces in sclang using arbitrarily defined scriptable components. For instance, a class written in this framework that sequences patterns in a routine might implement only the routine, leaving the patterns themselves to be defined later. As a result, an object of this class can support any number of diverse musical processes without comprising its functionality. In this way, the framework creates a best-of-both-worlds situation, balancing the specificity of a compiled class's interface with the open-ended potential of scripting. 

For more on how to work with CodexIan, consult the HelpSource guide.

## Installation

In SuperCollider, evaluate the following: 

`Quarks.install("https://github.com/ianmacdougald/CodexIan");`

## Changelog 

**January 9 2020** 

Added pseudo-method lookups to CodexStorage dictionary from String instances. 
