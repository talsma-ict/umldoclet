# UMLDoclet
Doclet for the JavaDoc tool that generates UML diagrams from the code.

## Requirements:

- JDK version 1.7 or newer.
- Availability of tools.jar containing the Sun JavaDoc API.

## To do:

_(before 1.0.0)_

- Mark abstract methods and optionally include abstract methods in 'foreign' packages?
- [Handle inner classes properly.](https://github.com/talsma-ict/umldoclet/issues/1)
- [Handle deprecation properly.](https://github.com/talsma-ict/umldoclet/issues/2)
- [Handle generics properly.](https://github.com/talsma-ict/umldoclet/issues/3)
- Add dependency diagrams showing all dependencies on package level.
- Additional doclet options to fine-tune the uml content (use / respect Standard doclet options as defaults?)

_(eventually)_

- Include Plantuml rendering of .puml files to .png images if Graphviz is detected.
- Add custom tag support to Standard doclet to support @classdiagram javadoc
