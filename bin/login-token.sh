#!/bin/bash
HOST=${HOST:-"http://localhost:9000"}
curl -v -H 'Content-Type: application/json' \
    "${HOST}/login" \
    -d '{"userAuthToken": "'${1}'"}'
