[![Maven Version][maven-img]][maven]
[![Javadoc][javadoc-img]][javadoc]
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=talsma-ict_umldoclet&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=talsma-ict_umldoclet)
[![Coverage Status][coveralls-img]][coveralls]

# UMLDoclet

[Doclet for javadoc][doclet] to automatically add [UML diagrams][plantuml] to Java documentation.  

## Generate UML diagrams

The UML Doclet uses available javadoc metadata to automatically generate
the following UML diagrams and embeds them in your HTML documentation.
The diagrams are embedded as clickable SVG objects by default 
and link to package and class documentation where available.

### Package dependency diagram

<p align="center"><a href="https://www.javadoc.io/doc/nl.talsmasoftware/umldoclet"><img src="https://javadoc.io/page/nl.talsmasoftware/umldoclet/2.2.0/package-dependencies.svg" width="60%" /></a></p>

_The UML Doclet will warn about (and optionally fail on) cyclic package dependencies._

The dependency diagram links to package documentation containing:

### Package diagram

<p align="center"><a href="https://www.javadoc.io/page/nl.talsmasoftware/umldoclet/latest/nl/talsmasoftware/umldoclet/rendering/indent/package-summary.html"><img src="https://javadoc.io/page/nl.talsmasoftware/umldoclet/2.2.0/nl/talsmasoftware/umldoclet/rendering/indent/package.svg" width="60%"/></a></p>

The package diagram links to class documentation containing:

### Class diagram

<p align="center"><a href="https://www.javadoc.io/page/nl.talsmasoftware/umldoclet/latest/nl/talsmasoftware/umldoclet/rendering/indent/Indentation.html"><img src="https://javadoc.io/page/nl.talsmasoftware/umldoclet/2.2.0/nl/talsmasoftware/umldoclet/rendering/indent/Indentation.svg" height="200"/></a></p>

(These examples are from the [latest UML Doclet javadoc][javadoc] page. Please feel free to take a look to get an idea of the final result)

## Requirements

To use the UML Doclet, the following is required.

- [Javadoc version 9][doclet] or higher.  
  For versions of javadoc from earlier JDKs, please use the [latest `1.x` UML Doclet version][v1.x].
  If you compile your java 8 or older javadocs with a more recent JDK (Javadoc version 9 or higher),
  you need to use the 2.x version.
- The UML Doclet, the [usage page][usage] shows how.
  An apache-licensed version of [PlantUML][plantuml] is already included in the umldoclet jar.

## Releases

Released versions can be found in the [maven central repository][maven]
or on [github](https://github.com/talsma-ict/umldoclet/releases).  

## Usage

- Please see the separate [usage page][Usage]
  on how to use the UML Doclet in your own Java projects.

## Feedback

- We welcome [new issues](https://github.com/talsma-ict/umldoclet/issues/new).
  Please search the [current issues](https://github.com/talsma-ict/umldoclet/issues)
  to avoid filing a duplicate.
- If at all possible, please provide an example when sending in bugs.
  This will make fixing them that much easier!
  
## Contributing

- See [the contribute page](https://github.com/talsma-ict/umldoclet/contribute) for this repository.

## Thanks

- First of all a big thanks to [Arnaud Roques](https://github.com/arnaudroques),
  the developer of the excellent [PlantUML project](https://github.com/plantuml/plantuml) 
  without whom this project wouldn't exist!
- And of course thank _you_ for using UML Doclet.
  I hope it may be of benefit to your project!

## License

- [Apache 2.0 license](./LICENSE)

  [coveralls-img]: <https://coveralls.io/repos/github/talsma-ict/umldoclet/badge.svg>
  [coveralls]: <https://coveralls.io/github/talsma-ict/umldoclet>
  [codacy-img]: <https://api.codacy.com/project/badge/Grade/b191c058492e466cb7044c1d53123d9a>
  [codacy]: <https://www.codacy.com/app/talsma-ict/umldoclet?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=talsma-ict/umldoclet&amp;utm_campaign=Badge_Grade>
  [maven-img]: <https://img.shields.io/maven-central/v/nl.talsmasoftware/umldoclet>
  [maven]: <http://mvnrepository.com/artifact/nl.talsmasoftware/umldoclet>
  [javadoc-img]: <https://www.javadoc.io/badge/nl.talsmasoftware/umldoclet.svg>
  [javadoc]: <https://www.javadoc.io/doc/nl.talsmasoftware/umldoclet>
  
  [usage]: <usage.md>
  [v1.x]: <https://github.com/talsma-ict/umldoclet/tree/develop-v1>
  [plantuml]: <http://plantuml.com>
  [doclet]: <https://docs.oracle.com/javase/9/docs/api/jdk/javadoc/doclet/Doclet.html>
  [libexpat]: <https://github.com/libexpat/libexpat/releases>
