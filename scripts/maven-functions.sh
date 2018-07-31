#!/bin/bash

declare -f debug > /dev/null || source "$(dirname $0)/logging.sh"

#
# Maven
#

is_maven_project() {
    [ -f pom.xml ]
}

maven_command() {
#    is_maven_project || fatal "No maven POM file found!"
    if [ -x ./mvnw ]; then echo "./mvnw";
    else echo "mvn";
    fi
}

get_maven_version() {
    echo $(printf 'VERSION=${project.version}\n0\n' | $(maven_command) help:evaluate | grep '^VERSION=' | sed 's/VERSION=//')
}

set_maven_version() {
    $(maven_command) --batch-mode versions:set versions:commit -DnewVersion="${1}" >/dev/null || fatal "Could not set project version to ${1}!"
}

build_and_test_maven() {
    log "Building and Testing project."
    $(maven_command) --batch-mode clean verify -Dmaven.test.failure.ignore=false
}

build_and_publish_maven_artifacts() {
    log "Building and Testing project."
    $(maven_command) --batch-mode clean verify -Dmaven.test.failure.ignore=false -Dmaven.javadoc.skip=true -Dmaven.source.skip=true
    log "Publishing project artifacts to maven central."
    $(maven_command) --batch-mode --no-snapshot-updates -Prelease deploy -DskipTests
}
