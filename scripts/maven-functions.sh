#!/usr/bin/env bash

declare -f debug > /dev/null || source "$(dirname "$0")/logging.sh"

#
# Maven
#

is_maven_project() {
    [ -f pom.xml ]
}

maven_command() {
#    is_maven_project || fatal "No maven POM file found!"
    if [ -x ./mvnw ]; then echo "./mvnw ${MAVEN_CLI_OPTS:-}";
    else echo "mvn ${MAVEN_CLI_OPTS:-}";
    fi
}

get_maven_version() {
#    Task help:evaluate without expression won't work in --batch-mode unfortunately..
#    echo $(printf 'VERSION=${project.version}\n0\n' | $(maven_command) help:evaluate | grep '^VERSION=' | sed 's/VERSION=//')
    $(maven_command) help:evaluate -Dexpression=project.version | grep '^[0-9]'
}

set_maven_version() {
    local plugin="org.codehaus.mojo:versions-maven-plugin:2.7"
    $(maven_command) --batch-mode "${plugin}:set" "${plugin}:commit" -DnewVersion="${1}" -DprocessAllModules=true >/dev/null || fatal "Could not set project version to ${1}!"
}

build_and_test_maven() {
    log "Building and Testing project."
    $(maven_command) --batch-mode clean verify -Dmaven.test.failure.ignore=false
}

build_and_publish_maven_artifacts() {
    log "Building and Testing project."
    $(maven_command) --batch-mode clean verify -Dmaven.test.failure.ignore=false -Dmaven.javadoc.skip="${MAVEN_JAVADOC_SKIP:-true}" -Dmaven.source.skip="${MAVEN_SOURCE_SKIP:-true}"
    log "Publishing project artifacts to maven central."
    $(maven_command) --batch-mode --no-snapshot-updates -Prelease deploy -DskipTests
}
