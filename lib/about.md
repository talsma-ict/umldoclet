# About the _lib_ directory

_TLDR: Due to license incompatibilities we have to bundle an alternative jar._

## Licensing issues

Since the advanced dependency management was introduced and Maven and its
central repository helped manage dependencies I haven't had the need to 
include any binary dependencies into my java project anymore.  

Until today. There are [several reasons](#48) to bundle the plantuml 
library with the doclet itself. Normally this can easily be accomplished
for any dependency with the [maven-shade-plugin](https://maven.apache.org/plugins/maven-shade-plugin/).
Unfortunately for me however, the version of plantuml on maven central has
a [GPL](http://www.gnu.org/copyleft/gpl.html) license which is a so-called
_copyleft_ license meaning it will require the umldoclet 
to become `GPL` licensed as well.

I want the doclet to remain open-source, but I feel the `GPL` to be too
restrictive: it 'infects' everything that uses it as well.
That's why the umldoclet is published under the 
[Apache License, version 2.0](https://www.apache.org/licenses/LICENSE-2.0.html).
That this license is less restrictive can also be seen from the following quote:

>Apache 2 software can therefore be included in GPLv3 projects, 
>because the GPLv3 license accepts our software into GPLv3 works.
>However, GPLv3 software cannot be included in Apache projects.

Source: https://www.apache.org/licenses/GPL-compatibility.html

## Solution?

Fortunately for us, the author of plantuml, Arnoud Roques also publishes
an [apache licensed version](http://plantuml.com/download#asl) of plantuml
[here](https://sourceforge.net/projects/plantuml/files/).
Just not on maven-central. Therefore the dependency has to be obtained from
sourceforce, preferably still in cooperation with the rest of the maven build.
I thought about automatically downloading, but sourceforce doesn't seem to
like that very much. Fortunately, maven still allows us to just add a
dependency directly into the project.

This is the reason the `lib` directory was introduced.
