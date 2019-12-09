[![Build Status][ci-img]][ci]
[![Maven Version][maven-img]][maven]
[![Javadoc][javadoc-img]][javadoc]

# UMLDoclet

[![Codacy Badge][codacy-img]][codacy]
[![Coverage Status][coveralls-img]][coveralls]

Doclet for the JavaDoc tool that generates UML diagrams from the code.  

Released versions can be found in the [maven central repository][maven]
or on [github](https://github.com/talsma-ict/umldoclet/releases).  

This doclet uses JavaDoc metadata available to automatically generate
the following [UML diagrams][plantuml] and add them to your documentation:

- Class diagrams
- Package diagrams
- A package dependency diagram

The doclet will warn about (and optionally fail on) cyclic package dependencies.

## Requirements

To use the doclet together with the JavaDoc tool, the following is required.

- [Javadoc version 9][doclet] or higher.  
  For versions of javadoc from earlier JDKs, please use the [latest `1.x` version][v1.x] of this doclet.
  If you compile your java 8 javadocs with a more recent JDK (Javadoc version 9 or higher), please use the 2.x version.
- This doclet jar. An apache-licensed version of plantuml is already included in the jar.
- An installed version of [graphviz](http://plantuml.com/graphviz-dot), at least until [pure java visualization](https://github.com/talsma-ict/umldoclet/issues/51) works.
  Graphviz needs to be compiled with [libexpat] support.

## Usage

- Please see the separate [Usage page][Usage]
  on how to use the UML doclet in your own Java projects.

## Examples

The javadoc of the UMLDoclet itself is probably a decent example of what the
default settings provide for you:

- [Main javadoc page](https://javadoc.io/doc/nl.talsmasoftware/umldoclet)
- [Simple package example: _nl.talsmasoftware.umldoclet.rendering_](https://javadoc.io/page/nl.talsmasoftware/umldoclet/latest/nl/talsmasoftware/umldoclet/rendering/package-summary.html)
- [Complex package example: _nl.talsmasoftware.umldoclet.uml_](https://javadoc.io/page/nl.talsmasoftware/umldoclet/latest/nl/talsmasoftware/umldoclet/uml/package-summary.html)
- [Class example: _nl.talsmasoftware.umldoclet.rendering.indent.Indentation_](https://javadoc.io/page/nl.talsmasoftware/umldoclet/latest/nl/talsmasoftware/umldoclet/rendering/indent/Indentation.html)

## Feedback

- Please search the [open issues](https://github.com/talsma-ict/umldoclet/issues)
  before you [file a new issue](https://github.com/talsma-ict/umldoclet/issues/new).
- If possible, please provide a working example when sending in bugs.
  This will make fixing them that much easier!
  
## Contributing

- See [the contribute page](https://github.com/talsma-ict/umldoclet/contribute) for this repository.

## Thanks

- First of all, thank _you_ for using this doclet, I hope it may be of benefit to your project.
- And of course most importantly a big thanks to the developers of the excellent [PlantUML project][plantuml]
  withouth whom this doclet wouldn't exist!

## License

- [Apache 2.0 license](../LICENSE)

  <!-- Definitions -->
  [ci-img]: https://travis-ci.org/talsma-ict/umldoclet.svg?branch=develop
  [ci]: https://travis-ci.org/talsma-ict/umldoclet
  [coveralls-img]: https://coveralls.io/repos/github/talsma-ict/umldoclet/badge.svg
  [coveralls]: https://coveralls.io/github/talsma-ict/umldoclet
  [codacy]: https://www.codacy.com/app/talsma-ict/umldoclet?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=talsma-ict/umldoclet&amp;utm_campaign=Badge_Grade
  [codacy-img]: https://api.codacy.com/project/badge/Grade/b191c058492e466cb7044c1d53123d9a
  [maven-img]: https://img.shields.io/maven-metadata/v/http/central.maven.org/maven2/nl/talsmasoftware/umldoclet/maven-metadata.xml.svg
  [maven]: http://mvnrepository.com/artifact/nl.talsmasoftware/umldoclet
  [javadoc-img]: https://www.javadoc.io/badge/nl.talsmasoftware/umldoclet.svg
  [javadoc]: https://www.javadoc.io/doc/nl.talsmasoftware/umldoclet
  
  [usage]: usage.md
  [v1.x]: https://github.com/talsma-ict/umldoclet/tree/develop-v1
  [plantuml]: http://plantuml.com
  [doclet]: https://docs.oracle.com/javase/9/docs/api/jdk/javadoc/doclet/Doclet.html
  [libexpat]: https://github.com/libexpat/libexpat/releases
