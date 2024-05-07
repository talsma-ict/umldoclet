# Maven

This directory contains metadata specific to the maven build for this project.


## Installing or upgrading the Maven and wrapper version

```
mvn wrapper:wrapper
```

The latest version can be found on https://github.com/apache/maven-wrapper

## Copyright license header template

The license header is located at `.mvn/license/header.txt` and is used
by the license plugin in the main `pom.xml` for this project.

When you get build errors that there is no license header in a file that requires
it, you can locally remedy this by running the following command that adds the
license header to all applicable source files:

```bash
./mvnw license:format
```
