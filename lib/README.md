# About the _lib_ directory

_TLDR: Due to license incompatibilities we have to bundle an alternative jar._

## Licensing issues

Since the advanced dependency management was introduced by Maven and its
central repository, there hasn't been a need to include any binary dependencies
into java projects anymore.  

Until today.

There are [good reasons](https://github.com/talsma-ict/umldoclet/issues/48) 
to bundle the plantuml library with the doclet itself.
Normally this can easily be accomplished for any dependency with 
the [maven-shade-plugin](https://maven.apache.org/plugins/maven-shade-plugin/).
Unfortunately however, the version of plantuml 
on [maven central](http://mvnrepository.com/artifact/net.sourceforge.plantuml/plantuml) 
has a [GPL](http://www.gnu.org/copyleft/gpl.html) license which is a so-called
_copyleft_ license. This means inclusion would require the umldoclet 
to become `GPL` licensed as well.

The doclet should remain open-source, but I feel the `GPL` to be too
restrictive: it 'infects' everything that uses it as well.
That is why the this project is published under the 
[Apache License, version 2.0](https://www.apache.org/licenses/LICENSE-2.0.html).
That this license is less restrictive is illustrated by the following quote:

>Apache 2 software can therefore be included in GPLv3 projects, 
>because the GPLv3 license accepts our software into GPLv3 works.
>However, GPLv3 software cannot be included in Apache projects.

Source: https://www.apache.org/licenses/GPL-compatibility.html

## Solution?

Fortunately for us, the author of plantuml, Arnoud Roques also publishes
an [apache licensed version](http://plantuml.com/download#asl) of 
plantuml [here][plantuml-asl-location].
Just not on maven-central. Therefore the dependency has to be obtained from
sourceforge, preferably still in cooperation with the rest of the maven build.
Downloading as part of the build is unfeasible as sourceforge doesn't 
seem to like that very much. Fortunately, maven still allows us to just 
add a dependency directly into the project.

This is the reason the `lib` directory was introduced.

## Why a local maven repo?

Can't you just add the jar to `lib` and add it as a `<scope>system</scope>` dependency
with a `<systemPath>` pointing to it?

>Yes, in fact that's exactly what was the original solution.
>However, the shade plugin (correctly) assumes that a system dependency should
>be actually available on the system and won't shade it, unless it's obtained
>from an actual Maven repository.

That's why the `lib` directory has been made into a mini-repository from which
we 'download' the `plantuml-asl.jar`.  
This way, the jar can be shaded into the umldoclet.

Installing a [freshly downloaded][plantuml-asl-location] `plantuml-asl.jar` 
into the local repo can be achieved by the following command:

```bash
mvn install:install-file \
    -Dfile=plantuml.jar \
    -DgroupId=net.sourceforge.plantuml \
    -DartifactId=plantuml-asl \
    -Dversion=<version> \
    -Dpackaging=jar \
    -DlocalRepositoryPath=lib
```


  [plantuml-asl-location]: https://sourceforge.net/projects/plantuml/files/
