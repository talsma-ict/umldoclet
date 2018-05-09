#!/bin/bash
set -eu -o pipefail
[[ "${DEBUG:-false}" =~ ^yes|true$ ]] && set -x

# Import functions if not already imported
declare -f debug > /dev/null || source "$(dirname $0)/logging.sh"
declare -f is_semantic_version > /dev/null || source "$(dirname $0)/versioning.sh"
declare -f is_pull_request > /dev/null || source "$(dirname $0)/git-functions.sh"
declare -f is_maven_project > /dev/null || source "$(dirname $0)/maven-functions.sh"

#
# Delegation
#

build_and_test() {
    if is_maven_project; then build_and_test_maven;
    else fatal "ERROR: No known project structure to publish artifacts for.";
    fi
}

build_and_publish_artifacts() {
    if is_maven_project; then build_and_publish_maven_artifacts;
    else fatal "ERROR: No known project structure to publish artifacts for.";
    fi
}

#----------------------
# MAIN
#----------------------

[ -n "${VERSION:-}" ] || VERSION=$(get_version)
[ -n "${GIT_BRANCH:-}" ] || GIT_BRANCH=$(find_remote_branch)

if is_pull_request; then
    log "Testing code for pull-request."
    build_and_test
elif is_snapshot_version "${VERSION}" && [[ "${GIT_BRANCH}" = "develop" ]]; then
    log "Publishing '${VERSION}' from branch '${GIT_BRANCH}'."
    build_and_publish_artifacts
elif [[ "${GIT_BRANCH}" = "master" ]]; then
    if [[ "${TRAVIS_BRANCH:-}" != "${TRAVIS_TAG:-}" ]]; then
        log "Travis: Running a test build on '${TRAVIS_BRANCH:-}' to avoid multiple release deployments."
        build_and_test
    elif ! is_snapshot_version "${VERSION}" && git tag -l --points-at HEAD | grep "${VERSION}" >/dev/null ; then
        log "Publishing '${VERSION}' from branch '${GIT_BRANCH}'."
        validate_version "${VERSION}"
        build_and_publish_artifacts
    else
        log "No release tag found on branch '${GIT_BRANCH}' for version '${VERSION}', running a test build."
        build_and_test
    fi
else
    log "Not publishing '${VERSION}' from branch '${GIT_BRANCH}', running a test build."
    build_and_test
fi
