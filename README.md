[![Build Status][ci-img]][ci]
[![Released Version][maven-img]][maven]

# UMLDoclet
Doclet for the JavaDoc tool that generates UML diagrams from the code.  
Released versions can be found in the [maven central repository][maven].  

This doclet uses the analyzed information from the JavaDoc tool.
It automatically generates UML diagrams from your code as separate 
`.puml` diagrams of your classes and packages in the 
[PlantUML format][plantuml].

__Note:__ Version `2.0.0` of this [doclet] has been created as a full rewrite of the 
existing `1.x` version, reusing only the internal rendering of `.puml` files.  
For this reason, the doclet will require a JDK 9 compatible version of `JavaDoc` to run.  
For earlier versions of javadoc, please use the `1.x` versions of this doclet.

## Requirements:
To use the doclet together with the JavaDoc tool, the following is required.

- JDK version 9 or higher.
- This doclet jar (see [usage]).

## Usage

- Please refer to [the usage page][usage] on how to use this doclet.

## Feedback

- Please search the [open issues](https://github.com/talsma-ict/umldoclet/issues)
  before you [file a new issue](https://github.com/talsma-ict/umldoclet/issues/new).
- If possible, please provide a working example when sending in bugs.
  This will make fixing them that much easier!
  
## Contributing

- __TODO__

  
_Thanks for using the doclet, I hope it may be of benefit to your project!_  
_Also a big thanks go to de developers of the excellent [PlantUML project][plantuml]._



  [ci-img]: https://img.shields.io/travis/talsma-ict/umldoclet/master.svg
  [ci]: https://travis-ci.org/talsma-ict/umldoclet
  [maven-img]: https://img.shields.io/maven-central/v/nl.talsmasoftware/umldoclet.svg
  [maven]: http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22nl.talsmasoftware%22%20AND%20a%3A%22umldoclet%22
  [usage]: USAGE.md
  [plantuml]: http://plantuml.com
  [doclet]: https://docs.oracle.com/javase/9/docs/api/jdk/javadoc/doclet/Doclet.html
