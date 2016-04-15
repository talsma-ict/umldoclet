# UML Doclet Usage

## Usage

This page describes how to use the UML Doclet together with the Oracle JavaDoc tool to generate standard HTML documentation for your project, with [PlantUML](http://plantuml.com) source files for each Java class and package.

### Maven

Since Maven is the build tool used by the the UML Doclet project itself, it will be described first.  
  
First tell Maven to generate JavaDoc for your project by declaring the 'maven-javadoc-plugin' to use the UML Doclet:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-javadoc-plugin</artifactId>
            <version>2.10.3</version>
            <executions>
                <execution>
                    <id>attach-javadocs</id>
                    <goals>
                        <goal>jar</goal>
                    </goals>
                    <configuration>
                        <doclet>nl.talsmasoftware.umldoclet.UMLDoclet</doclet>
                        <docletArtifact>
                            <groupId>nl.talsmasoftware</groupId>
                            <artifactId>umldoclet</artifactId>
                            <version>1.0.2-SNAPSHOT</version>
                        </docletArtifact>
                        <additionalParam>
                            ...
                        </additionalParam>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

There are many ways to tune the detail of the UML diagrams.  
These can be provided as additional parameters within the `additionalParam` tag, (each parameter may be on a new line within the XML).  
The additional parameters for this doclet are described below.

### Gradle

In gradle, the doclet and its dependency need to be declared. From there on, the configuration is the same as your regular JavaDoc configuration.

```groovy
apply plugin: 'java'

configurations {
    umlDoclet
}

dependencies {
    umlDoclet "nl.talsmasoftware:umldoclet:1.0.2-SNAPSHOT"
}

javadoc {
    source = sourceSets.main.allJava
    options.docletPath = configurations.umlDoclet.files.asType(List)
    options.doclet = "nl.talsmasoftware.umldoclet.UMLDoclet"
    options.addStringOption "additionalParamName", "additionalParamValue"
}
```

Replace `additionalParamName` and `additionalParamValue` with the name and value of each additional parameter you need.  
_Note:_ The initial dash `-` of additional parameters will automatically be added by the Gradle javadoc task and should therefore be omitted from the configuration.  
  
The additional parameters for this doclet are described below.

### Ant

In ant, the javadoc task needs to be told to use the UML Doclet in a similar way.

```xml
<javadoc destdir="target/javadoc" sourcepath="src">
    <doclet name="nl.talsmasoftware.umldoclet.UMLDoclet" pathref="umlDoclet.classpath"> 
        <param name="additionalParamName" value="additionalParamValue" />
    </doclet>
</javadoc>
```

Make sure a path reference is defined for `umlDoclet.classpath` pointing to `umldoclet-{VERSION}.jar`. It may be a good idea to use [Ivy](http://ant.apache.org/ivy) in this case.   
Replace `additionalParamName` and `additionalParamValue` with the name and value of each additional parameter you need.  
  
The additional parameters for this doclet are described below.

### Commandline

Probably not many people run JavaDoc regularly from the commandline, but in case you do, make sure to provide the options `-doclet nl.talsmasoftware.umldoclet.UMLDoclet` and `-docletpath {PATH_TO_JAR}`, where _{PATH_TO_JAR}_ is the location of the `umldoclet-{VERSION}.jar`.  
The latest version of the jar file can be found on http://repo.maven.apache.org/maven2/nl/talsmasoftware/umldoclet/  
  
For more details on commandline javadoc, please see the [official documentation from Oracle](http://docs.oracle.com/javase/1.5.0/docs/tooldocs/windows/javadoc.html "Oracle documentation").

### Additional parameters

The UML Doclet supports many additional parameters to be configured.  
However, please know that all attempts have been made to keep the defaults chosen in such a manner, that the options rarely need to be overridden.

| Parameter name                    | Possible values   | Default value | Description |
| --------------------------------- | ----------------- | ------------- | ----------- |
| -umlLogLevel                      | `ALL`, `TRACE`, `DEBUG`, `INFO`, `WARN`, `ERROR`, `FATAL` | `INFO` | The log level defined in common level definitions |
| -umlIndentation                   | _integer_         | `-1`          | The indentation level to use for the generated UML. A negative value uses whatever the default indentation is and zero obviously will not indent the UML. |
| -umlBasePath                      | filesystem path   | `<null>`      | The base path where the UML diagrams should be generated, by default the UML Doclet generates the diagrams in the same location as the regular HTML documentation. |
| -umlFileExtension                 | file extention    | `".puml"`     | The file extension to use for generated UML diagrams. |
| -umlFileEncoding                  | encoding name     | `"UTF-8"`     | The file encoding to use for generated UML diagrams. Note however, before the default value is applied, the standard doclet option `-docEncoding` is inspected to obtain a value. |
| -umlSkipStandardDoclet            | _boolean_         | `false`       | When set to `true` this option prevents the UML Doclet from delegating to the Standard doclet that generates the HTML documentation. |
| -umlIncludePrivateFields          | _boolean_         | `false`       | Whether `private` fields will be included in the rendered diagrams. |
| -umlIncludePackagePrivateFields   | _boolean_         | `false`       | Whether `package-private` fields will be included in the rendered diagrams. |
| -umlIncludeProtectedFields        | _boolean_         | `true`        | Whether `protected` fields will be included in the rendered diagrams. |
| -umlIncludePublicFields           | _boolean_         | `true`        | Whether `public` fields will be included in the rendered diagrams. |
| -umlIncludeDeprecatedFields       | _boolean_         | `false`       | Whether `deprecated` fields will be included in the rendered diagrams. |
| -umlIncludeFieldTypes             | _boolean_         | `true`        | Whether the field types will be included in the rendered diagrams. |
| -umlIncludeMethodParamNames       | _boolean_         | `false`       | Whether the names of method parameters will be included in the rendered diagrams. |
| -umlIncludeMethodParamTypes       | _boolean_         | `true`        | Whether the types of method parameters will be included in the rendered diagrams. |
| -umlIncludeMethodReturntypes      | _boolean_         | `true`        | Whether the returntypes of methods will be included in the rendered diagrams. |
| -umlIncludeConstructors           | _boolean_         | `true`        | Whether the class constructors will be included in the rendered diagrams. |
| -umlIncludeDefaultConstructors    | _boolean_         | `false`       | (_only applicable when `-umlIncludeConstructors` is `true`_) Whether the class constructors will still be included in the rendered diagrams if the only constructor is the default constructor without parameters. |
| -umlIncludePrivateMethods         | _boolean_         | `false`       | Whether `private` methods will be included in the rendered diagrams. |
| -umlIncludePackagePrivateMethods  | _boolean_         | `false`       | Whether `package-private` methods will be included in the rendered diagrams. |
| -umlIncludeProtectedMethods       | _boolean_         | `true`        | Whether `protected` methods will be included in the rendered diagrams. |
| -umlIncludePublicMethods          | _boolean_         | `true`        | Whether `public` methods will be included in the rendered diagrams. |
| -umlIncludeDeprecatedMethods      | _boolean_         | `false`       | Whether `deprecated` methods will be included in the rendered diagrams. |
| -umlIncludeAbstractSuperclassMethods | _boolean_      | `true`        | Whether `abstract` methods will be included in superclasses, even when declared outside the rendered package. |
| -umlIncludePrivateClasses         | _boolean_         | `false`       | Whether `private` classes will be included in the rendered package diagrams. |
| -umlIncludePackagePrivateClasses  | _boolean_         | `false`       | Whether `package-private` classes will be included in the rendered package diagrams. |
| -umlIncludeProtectedClasses       | _boolean_         | `true`        | Whether `protected` classes will be included in the rendered package diagrams. |
| -umlIncludeDeprecatedClasses      | _boolean_         | `false`       | Whether `deprecated` classes will be included in the rendered package diagrams. |
| -umlIncludePrivateInnerClasses    | _boolean_         | `false`       | Whether `private` inner-classes will be included in the rendered diagrams. |
| -umlIncludePackagePrivateInnerClasses | _boolean_     | `false`       | Whether `package-private` inner-classes will be included in the rendered diagrams. |
| -umlIncludeProtectedInnerClasses  | _boolean_         | `false`       | Whether `protected` inner-classes will be included in the rendered diagrams. |
| -umlExcludedReferences            | list of classes   | `java.lang.Object`, `java.lang.Enum` | List of classes that will not be rendered as an external reference in the diagrams. Please mind that this value must be enclosed in quotes or there should be no spaces in this setting for the JavaDoc tool to be able to parse this as a single value. |
| -umlIncludeOverridesFromExcludedReferences | _boolean_ | `false`      | Whether inherited methods from classes defined in `-umlExcludedReferences` should be rendered in the diagrams. |
| -umlCommand                       | _commands_        | _none_        | This setting allows custom `commands` to be added to the diagrams. Commands containing whitespaces must be provided within double-quotes. Multiple commands can be specified by specifying the `-umlCommand` parameter repeatedly. An example value could be a `-umlCommand "hide class circle"` parameter and a `-umlCommand "hide empty members"` to hide class circles and empty members (be sure to include the quotes). For more information, please see (http://plantuml.com/classes.html). |
| Since _version 1.0.1_:            ||||
| -umlAlwaysUseQualifiedClassnames  | _boolean_         | `false`       | Whether the new simplified classnames within packages (namespaces) must be disabled. Simplified names should have no effect on the rendered diagrams, but instead only result in more easily readable `.puml` sources. This setting was introduced to be able to turn off the changes from [enhancement 15](https://github.com/talsma-ict/umldoclet/issues/15). |
