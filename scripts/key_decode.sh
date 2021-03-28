#!/usr/bin/env bash

set -e

echo "$GPG_KEY_CONTENTS" | base64 -d > "$SIGNING_SECRET_KEY_RING_FILE"