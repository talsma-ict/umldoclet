#!/bin/bash
set -eu -o pipefail
source "$(dirname $0)/options.sh"
[[ "${DEBUG:-false}" =~ ^yes|true$ ]] && set -x

declare -f debug > /dev/null || source "$(dirname $0)/logging.sh"
declare -f is_semantic_version > /dev/null || source "$(dirname $0)/versioning.sh"
declare -f is_pull_request > /dev/null || source "$(dirname $0)/git-functions.sh"

create_release() {
    fix_travis_fetch
    local branch="${1:-}"
    debug "Performing release from branch ${branch}."
    is_release_version "${branch}" || fatal "Not a valid release branch: '${branch}'."
    local release_version="${branch#*/}"
    debug "Detected version '${release_version}'."
    validate_version "${release_version}"
    local major_version=$(major_version_of ${release_version})

    switch_to_branch "${branch}" || create_branch "${branch}"
    log "Releasing version ${release_version} from branch ${branch}."

    local current_version="$(get_version)"
    if [[ "${current_version}" != "${release_version}" ]]; then
        log "Updating version from ${current_version} to ${release_version}."
        set_version "${release_version}"
        git commit -sam "Release: Set project version to ${release_version}"
    else
        log "No need to update project version. It is already '${release_version}'."
    fi

    local tagname="${release_version}"
    log "Tagging published code with '${tagname}'."
    git tag -m "Release version ${release_version}" "${tagname}"

    # Merge to master and delete local release branch
    debug "Checking if we have to merge ${branch} to master"
    switch_to_branch master
    [[ "$(get_local_branch)" = "master" ]] || fatal "Could not switch to master branch."
    local master_version="$(get_version)"
    local merge_to_master="true"
    if [[ ${major_version} -ge $(major_version_of ${master_version}) ]]; then
        log "Merging ${release_version} to master (v${master_version})."
        git merge --no-edit --ff-only "${branch}"
    else
        log "Not merging ${release_version} to master (v${master_version})."
        merge_to_master="false"
    fi

    # Merge to develop and switch to next snapshot
    local nextSnapshot="$(next_snapshot_version ${release_version})"
    local develop_branch="develop"
    if [[ $(switch_to_branch "develop-v${major_version}") ]]; then
        develop_branch="develop-v${major_version}"
    else
        switch_to_branch "${develop_branch}"
    fi
    [[ "$(get_local_branch)" = "${develop_branch}" ]] || fatal "Could not switch to ${develop_branch} branch."
    log "Merging to ${develop_branch} and updating version to '${nextSnapshot}'."
    git merge --no-edit "${branch}"
    set_version ${nextSnapshot}
    git commit -sam "Release: Set next development version to ${nextSnapshot}"

    log "Pushing release to origin and deleting branch '${branch}'."
    git branch -D "${branch}" || warn "Could not delete local release branch '${branch}'."
    git push origin "${tagname}"
    [[ "${merge_to_master}" = "true" ]] && git push origin master
    git push origin "${develop_branch}"
    git push origin --delete "${branch}"
}

#----------------------
# MAIN
#----------------------

[ -n "${GIT_BRANCH:-}" ] || GIT_BRANCH=$(find_remote_branch)

if is_release_version "${GIT_BRANCH}"; then
    log "Creating a new release from branch '${GIT_BRANCH}'."
    create_release "${GIT_BRANCH}"
else
    log "Nothing to release, not on a release branch: '${GIT_BRANCH}'."
fi
