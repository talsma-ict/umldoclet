set -eu -o pipefail

: ${DEBUG:="no"}
if [[ "${DEBUG}" =~ ^yes|true$ ]]; then set -x; fi

must_restart=false

# Nice trick to explain errors from an action:
#   output=$( some-action 2>&1) || {
#       if [[ "${output}" =~ "A specific technical message" ]]; then
#           echo "Provide friendly explanation and how to solve it."
#       else echo "${output}"; fi
#       exit 1
#   }

check_for_updates() {
    do_check_for_updates || echo "[ERROR] Problem checking for updates"
}

do_check_for_updates() {
    local rootdir="$(dirname $(dirname $(dirname $0)))"
    git --work-tree "$rootdir" --git-dir "$rootdir/.git" remote update >/dev/null
    local LOCAL=$(git --work-tree "$rootdir" --git-dir "$rootdir/.git" rev-parse @)
    local REMOTE=$(git --work-tree "$rootdir" --git-dir "$rootdir/.git" rev-parse @{u})
    if [[ $LOCAL != $REMOTE ]]; then
        echo
        echo "There are updates for this script, downloading them..."
        # It may be possible that there are local commits or diffs, but we do not check for those yet.
        git --work-tree "$rootdir" --git-dir "$rootdir/.git" pull
        must_restart=true
    fi
}

show_usage() {
    echo "Usage:"
    echo
    echo "  first-command [options] <arguments>"
    echo "  second-command [options]"
    echo
    echo "options:"
    echo
    echo "  -h  show this help page"
    echo "  -d  enable debug logging (environment variable DEBUG=yes will also work)"
    echo "  -n  skip auto-update check"
}

# Common initialization code.
init() {
    echo "Initializing..."
}

first_command() {
    if [ -z "${1-}" ]; then
        echo
        echo "Usage: first-command <argument>"
        echo
        exit 1
    fi

    local arg="$1"
    echo "Processing first command with argument $1..."
}

second_command() {
    echo "Processing second command..."
}

do_run() {
    init

    case $(basename $0) in
        "first-command")
            first_command "$@"
            ;;
        "second-command")
            feature_finish "$@"
            ;;
        *)
            echo "Unknown command: $(basename $0)"
            exit 1
            ;;
    esac
}

run() {
    local checkForUpdates=true
    while getopts "hdn" opt; do
        case $opt in
            h|\?)
                show_usage
                exit 0
                ;;
            d)
                set -x
                export DEBUG=true
                ;;
            n)
                checkForUpdates=false
                ;;
        esac
    done

    [[ $checkForUpdates = true ]] && check_for_updates

    if [[ $must_restart = true ]]; then
        echo
        echo "Scripts were updated and will be reloaded..."
        echo
        source "$(dirname $0)/auto-update.sh"
        run "-n" "$@"
        exit 0
    fi

    shift $((OPTIND-1))
    do_run "$@"
}