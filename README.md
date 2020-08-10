# CodexIan

CodexIan is a collection of classes that establishes a framework for making and developing musical projects in sclang. Objects defined under this framework balance the specficity of a compiled class's interface with the open-ended, experimental potential of scripting. As such, a class written using its features only has to describe high-level interactions between scriptable components whose definitions are largely arbitrary. 

For instance, a class written to sequence patterns in a routine might implement only the routine and then leave the patterns to be loaded from external scripts called modules. As a result, the patterns of this example can be redefined at any point and redeployed without the need for recompiling sclang.  

Moreover, the modules themselves can be edited, cloned, and templated at will. As such, every class developed under this framework can endlessly vary its core functions without additional developmental overhead.

## Installation

In SuperCollider, evaluate the following: 

`Quarks.install("https://github.com/ianmacdougald/codexian");`
