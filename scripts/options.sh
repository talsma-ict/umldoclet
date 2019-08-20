#!/bin/bash

# Set default values
[ -n "${DEBUG:-}" ] || DEBUG=false
[ -n "${USE_COLOR:-}" ] || if [[ -t 1 ]]; then USE_COLOR=true; else USE_COLOR=false; fi

# Parse commandline arguments
for arg in "$@"; do case $arg in
    -d|--debug) DEBUG=true; shift;;
    -d=*|--debug=*) DEBUG="${arg#*=}"; shift;;
    -c|--color|--colour) USE_COLOR=true; shift;;
    -c=*|--color=*|--colour=*) USE_COLOR="${arg#*=}"; shift;;
#    *) OTHER_ARGUMENTS+=("$1"); shift;;
esac done
