# UMLDoclet
Doclet for the JavaDoc tool that generates UML diagrams from the code.

## Requirements:

- JDK version 1.7 or newer.
- Availability of tools.jar containing the Sun JavaDoc API.

## To do:

_(before 1.0.0)_

- [Fix class visibility.](https://github.com/talsma-ict/umldoclet/issues/4)
- [Ability to influence the style of generated diagrams.](https://github.com/talsma-ict/umldoclet/issues/6)
- [Sanitize and/or group the many settings for this doclet.](https://github.com/talsma-ict/umldoclet/issues/7)
- [Document usage for initial release of this doclet.](https://github.com/talsma-ict/umldoclet/issues/8)

_(eventually)_

- Add dependency diagrams showing all dependencies on package level.
- Include Plantuml rendering of .puml files to .png images if Graphviz is detected.
- Add custom tag support to Standard doclet to support @classdiagram javadoc
