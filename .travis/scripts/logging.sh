#!/bin/bash

#
# Script containing (very) basic logging and debugging features
#

SCRIPTNAME=$(basename ${0%.*})

log() {
    local message="[${2:-INFO}] [${SCRIPTNAME}] ${1:-}"
    [ "${2:-}" = "DEBUG" ] || echo "${message}" 1>&2
}

debug() {
    log "${1:-}" "${2:-DEBUG}"
}

warn() {
    log "${1:-}" "${2:-WARNING}"
}

fatal() {
    log "${1:-}" "${2:-ERROR}"
    exit 1
}
