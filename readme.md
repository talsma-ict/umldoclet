[![CI build][ci-img]][ci]
[![Maven Version][maven-img]][maven]
[![Javadoc][javadoc-img]][javadoc]

# UMLDoclet

[Doclet for javadoc][doclet] to automatically add [UML diagrams][plantuml] to Java documentation.  

[![Codacy Badge][codacy-img]][codacy]
[![Codebeat badge][codebeat-img]][codebeat]
[![Coverage Status][coveralls-img]][coveralls]

## Generate UML diagrams

The UML Doclet uses available javadoc metadata to automatically generate
the following UML diagrams and embeds them in your HTML documentation.
The diagrams are embedded as clickable SVG objects by default 
and link to package and class documentation where available.

### Package dependency diagram

<p align="center"><a href="https://www.javadoc.io/doc/nl.talsmasoftware/umldoclet"><img src="https://javadoc.io/page/nl.talsmasoftware/umldoclet/2.0.16/package-dependencies.svg" width="60%" /></a></p>

_The UML Doclet will warn about (and optionally fail on) cyclic package dependencies._

The dependency diagram links to package documentation containing:

### Package diagram

<p align="center"><a href="https://www.javadoc.io/page/nl.talsmasoftware/umldoclet/latest/nl/talsmasoftware/umldoclet/rendering/indent/package-summary.html"><img src="https://javadoc.io/page/nl.talsmasoftware/umldoclet/2.0.16/nl/talsmasoftware/umldoclet/rendering/indent/package.svg" width="60%"/></a></p>

The package diagram links to class documentation containing:

### Class diagram

<p align="center"><a href="https://www.javadoc.io/page/nl.talsmasoftware/umldoclet/latest/nl/talsmasoftware/umldoclet/rendering/indent/Indentation.html"><img src="https://javadoc.io/page/nl.talsmasoftware/umldoclet/2.0.16/nl/talsmasoftware/umldoclet/rendering/indent/Indentation.svg" height="200"/></a></p>

(These examples are from the [latest UML Doclet javadoc][javadoc] page. Please feel free to take a look to get an idea of the final result)

## Requirements

To use the UML Doclet, the following is required.

- [Javadoc version 9][doclet] or higher.  
  For versions of javadoc from earlier JDKs, please use the [latest `1.x` UML Doclet version][v1.x].
  If you compile your java 8 or older javadocs with a more recent JDK (Javadoc version 9 or higher),
  you need to use the 2.x version.
- An installed version of [graphviz](http://plantuml.com/graphviz-dot),
  at least until [pure java visualization](https://github.com/talsma-ict/umldoclet/issues/51) works.
  Graphviz needs to be compiled with [libexpat][libexpat] support.
- The UML Doclet, the [usage page][usage] shows how.
  An apache-licensed version of [PlantUML][plantuml] is already included in the umldoclet jar.

## Releases

Released versions can be found in the [maven central repository][maven]
or on [github](https://github.com/talsma-ict/umldoclet/releases).  

## Usage

- Please see the separate [usage page][Usage]
  on how to use the UML Doclet in your own Java projects.

## Feedback

- We welcome [new issues](https://github.com/talsma-ict/umldoclet/issues/new) :+1:.
  Please search the [current issues](https://github.com/talsma-ict/umldoclet/issues) however,
  otherwise you might file a duplicate.
- Please provide an example when sending in bugs if at all possible.
  This will make fixing them that much easier!
  
## Contributing

- See [the contribute page](https://github.com/talsma-ict/umldoclet/contribute) for this repository.

## Thanks

- First of all a big thanks to [Arnaud Roques](https://github.com/arnaudroques),
  the developer of the excellent [PlantUML project](https://github.com/plantuml/plantuml) 
  without whom this project wouldn't exist! :clap:
- And of course thank _you_ for using UML Doclet.
  I hope it may be of benefit to your project!

## License

- [Apache 2.0 license](./LICENSE)

  [ci-img]: https://github.com/talsma-ict/umldoclet/actions/workflows/ci-build.yml/badge.svg
  [ci]: https://github.com/talsma-ict/umldoclet/actions/workflows/ci-build.yml
  [coveralls-img]: <https://coveralls.io/repos/github/talsma-ict/umldoclet/badge.svg>
  [coveralls]: <https://coveralls.io/github/talsma-ict/umldoclet>
  [codacy-img]: <https://api.codacy.com/project/badge/Grade/b191c058492e466cb7044c1d53123d9a>
  [codacy]: <https://www.codacy.com/app/talsma-ict/umldoclet?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=talsma-ict/umldoclet&amp;utm_campaign=Badge_Grade>
  [codebeat-img]: <https://codebeat.co/badges/527487b4-62c8-40a2-8229-8341fec95c7b>
  [codebeat]: <https://codebeat.co/projects/github-com-talsma-ict-umldoclet-develop>
  [maven-img]: <https://img.shields.io/maven-central/v/nl.talsmasoftware/umldoclet>
  [maven]: <http://mvnrepository.com/artifact/nl.talsmasoftware/umldoclet>
  [javadoc-img]: <https://www.javadoc.io/badge/nl.talsmasoftware/umldoclet.svg>
  [javadoc]: <https://www.javadoc.io/doc/nl.talsmasoftware/umldoclet>
  
  [usage]: <usage.md>
  [v1.x]: <https://github.com/talsma-ict/umldoclet/tree/develop-v1>
  [plantuml]: <http://plantuml.com>
  [doclet]: <https://docs.oracle.com/javase/9/docs/api/jdk/javadoc/doclet/Doclet.html>
  [libexpat]: <https://github.com/libexpat/libexpat/releases>
