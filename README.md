# PrimoCore

The core of Primo - the Word Document parser/preprocessor.

PrimoCore should serve as the interface between the Apache POI and  
the specific classes of Word documents (e.g. cases, official gazettes, etc.)

PrimoCore should only be concerned with the general and the abstract. 
It must not know about the specific parsing needs of a particular document class.

PrimoCore should expose interfaces and abstract classes that parsers  
for specific document classes can implement.

The Parsers that depend on PrimoCore should not need to import or know about  
Apache POI. This dependency is simply an implementation detail that could  
be replaced at any given time.