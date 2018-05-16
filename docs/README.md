[![Build Status][ci-img]][ci]
[![Released Version][maven-img]][maven]

# UMLDoclet

Doclet for the JavaDoc tool that generates UML diagrams from the code.  

## Blockers for 2.0

- None discovered, please [file an issue](https://github.com/talsma-ict/umldoclet/issues/new)
  if you found a serious defect.

## Nice to haves

- Java-8 compatible multi-version jar (#52)
- Reasonable configurability

__Note:__ Version `2.0.0` of this [doclet] is a full rewrite as 
a [Javadoc 9 doclet][doclet], reusing only the internal rendering of `.puml` files.  
UMLDoclet v2 requires a JDK 9 compatible version of `javadoc` to run.  
For earlier versions of javadoc, please use the latest `1.x` version of this doclet.

Released versions can be found in the [maven central repository][maven].  

This doclet uses the analyzed information from the JavaDoc tool.
It automatically generates UML diagrams from your code as separate 
`.puml` diagrams of your classes and packages in the 
[PlantUML format][plantuml].

## Requirements:

To use the doclet together with the JavaDoc tool, the following is required.

- JDK version 9 or higher (for older Javadoc versions please use [v1.x]).
- This doclet jar.

## Usage

- See [Usage] (__TODO__)

## License

[Apache 2.0 license](../LICENSE)

## Feedback

- Please search the [open issues](https://github.com/talsma-ict/umldoclet/issues)
  before you [file a new issue](https://github.com/talsma-ict/umldoclet/issues/new).
- If possible, please provide a working example when sending in bugs.
  This will make fixing them that much easier!
  
## Contributing

- See [Contributing] (__TODO__)

## Thanks!

- First of all, thank _you_ for using this doclet, I hope it may be of benefit to your project.
- And of course most importantly a big thanks to the developers of the excellent [PlantUML project][plantuml]
  withouth whom this doclet wouldn't exist!



  [ci-img]: https://img.shields.io/travis/talsma-ict/umldoclet/develop.svg
  [ci]: https://travis-ci.org/talsma-ict/umldoclet
  [maven-img]: https://img.shields.io/maven-central/v/nl.talsmasoftware/umldoclet.svg
  [maven]: http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22nl.talsmasoftware%22%20AND%20a%3A%22umldoclet%22
  [usage]: USAGE.md
  [contributing]: Contributing.md
  [v1.x]: https://github.com/talsma-ict/umldoclet/tree/develop-v1
  [plantuml]: http://plantuml.com
  [doclet]: https://docs.oracle.com/javase/9/docs/api/jdk/javadoc/doclet/Doclet.html
