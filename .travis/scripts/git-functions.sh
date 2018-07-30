#!/bin/bash

declare -f debug > /dev/null || source "$(dirname $0)/logging.sh"

#
# Script containing some handy git functions because CI (travis) runs on a detached HEAD
#

fix_travis_fetch() {
    debug "Fixing broken 'git fetch' on travis..."
    git config --replace-all remote.origin.fetch +refs/heads/*:refs/remotes/origin/*
}

is_pull_request() {
    git ls-remote origin | grep $(git rev-parse HEAD) | grep "refs/pull/"
    return $?
}

find_release_tag() {
    echo $(git tag -l --points-at HEAD | grep '^release-')
}

get_local_branch() {
    echo "$(git branch | grep '*' | sed 's/[* ]*//')"
}

find_remote_branches() {
    echo $(git ls-remote --heads origin | grep `git rev-parse HEAD` | sed "s/.*refs\/heads\///g")
}

find_remote_branch() {
    local local_branch="$(get_local_branch)"
    local remote_branches=$(find_remote_branches)
    if [[ -n "${local_branch:-}" && "${remote_branches}" = *"${local_branch}" ]]; then echo ${local_branch};
    elif [[ -n "${TRAVIS_BRANCH:-}" && "${remote_branches}" = *"${TRAVIS_BRANCH}" ]]; then echo ${TRAVIS_BRANCH};
    else echo ${remote_branches} | awk '{print $1}';
    fi
}

switch_to_branch() {
    log "Switching to branch ${1}"
    git fetch origin
    git checkout "${1}" || git checkout -B "${1}" "origin/${1}" && git pull
}

create_branch() {
    log "Creating and switching to new local branch ${1}"
    git checkout -b "${1}"
}

validate_merged_with_remote_branch() {
    if ! git branch -a --merged | grep "remotes/.*/${1}" > /dev/null; then
        fatal "FATAL - Git is not up-to-date with remote branch ${1}, please merge first before proceeding."
    fi
}
