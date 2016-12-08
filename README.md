# UMLDoclet
Doclet for the JavaDoc tool that generates UML diagrams from the code.  
The current version is: `1.0.9-SNAPSHOT`.  
Released versions can be found in the [maven central repository](http://repo.maven.apache.org/maven2/nl/talsmasoftware/umldoclet/).  

This doclet uses the analyzed information from the JavaDoc tool.
It automatically generates UML diagrams from your code as separate 
`.puml` diagrams of your classes and packages in the 
[PlantUML format](http://plantuml.com/).

## Requirements:
To use the doclet together with the JavaDoc tool, the following is required.

- JDK version 1.7 or newer.
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
