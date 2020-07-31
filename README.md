# CodexIan 
CodexIan is a library of classes designed to support an approach to music making in sclang that combines the specificity of a compiled class's interface with the open-ended potential of scripting. The basic idea for the library is that a class written using its features only has to describe high-level interactions between swappable components whose definitions are arbitrary. For instance, a class written to sequence patterns in a routine might implement only the routine and then leave the patterns to be loaded from external scripts called modules. As a result, the patterns of this example can be redefined at any point and put to use without the need for recompiling sclang.

### Installation
In SuperCollider, evaluate the following: 

`Quarks.install("https://github.com/ianmacdougald/codexian");`

