#!/usr/bin/env bash

set -e

sudo echo "$GPG_KEY_CONTENTS" | base64 -d > "$SIGNING_SECRET_KEY_RING_FILE"