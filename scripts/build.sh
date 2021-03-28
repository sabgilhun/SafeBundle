#!/usr/bin/env bash

set -e

./gradlew :safebundle-annotation:jar \
  :safebundle-processor:jar \
  :safebundle:assemble