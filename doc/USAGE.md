# UML Doclet Usage

## Usage

### Maven

Since Maven is the build tool used by the the ${project.artifactId} project itself, it will be described first.

1. First of all, tell Maven to generate JavaDoc for your project
   by declaring the 'maven-javadoc-plugin' to use the UML Doclet:  
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
                        <doclet>${project.groupId}.${project.artifactId}.${project.artifactId}</doclet>
                        <docletArtifact>
                            <groupId>${project.groupId}</groupId>
                            <artifactId>${project.artifactId}</artifactId>
                            <version>${project.version}</version>
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

This creates standard HTML documentation for your project,
with [PlantUML](http://plantuml.com) source files for each Java class and package.

There are many ways to tune the detail of the UML diagrams.  
These can be provided as additional parameters within the `additionalParam` tag,
(each parameter may be on a new line within the XML).

### Additional parameters

_TODO_
