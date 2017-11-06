[![Build Status][ci-img]][ci]
[![Released Version][maven-img]][maven]

# UMLDoclet
Doclet for the JavaDoc tool that generates UML diagrams from the code.  
Released versions can be found in the [maven central repository][maven].  

This doclet uses the analyzed information from the JavaDoc tool.
It automatically generates UML diagrams from your code as separate 
`.puml` diagrams of your classes and packages in the 
[PlantUML format](http://plantuml.com/).

## Requirements:
To use the doclet together with the JavaDoc tool, the following is required.

- JDK version 1.7 or 1.8 (JDK 9 no longer has a `tools.jar` and 
  will require [version 2.x](https://github.com/talsma-ict/umldoclet/pull/46) 
  of the UML Doclet).
- Availability of tools.jar containing the Sun JavaDoc API.
- The doclet jar or a build system that knows how to get it (see [usage](USAGE.md)).

## Usage

- Please refer to [the usage page](USAGE.md) on how to use this doclet.

## Example

Please take a look at the rendered .SVG image of this doclet's 
[rendering package](example/rendering-package.svg).  
Or, look at the [the generated plantuml source file](example/rendering-package.puml).

## Feedback

- Please search the [open issues](https://github.com/talsma-ict/umldoclet/issues)
  before you [file a new issue](https://github.com/talsma-ict/umldoclet/issues/new).
- If possible, please provide a working example when sending in bugs.
  This will make fixing them that much easier!
  
  
_Thanks for using the doclet, I hope it may be of benefit to your project!_  
_Also a big thanks go to de developers of the excellent [PlantUML project](http://plantuml.com/)._

  [ci-img]: https://img.shields.io/travis/talsma-ict/umldoclet/master.svg
  [ci]: https://travis-ci.org/talsma-ict/umldoclet
  [maven-img]: https://img.shields.io/maven-central/v/nl.talsmasoftware/umldoclet.svg
  [maven]: http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22nl.talsmasoftware%22%20AND%20a%3A%22umldoclet%22
