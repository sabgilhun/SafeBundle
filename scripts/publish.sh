#!/usr/bin/env bash

set -e

./gradlew :safebundle-annotation:publishReleasePublicationToCentralRepository \
  :safebundle-processor:publishReleasePublicationToCentralRepository \
  :safebundle:publishReleasePublicationToCentralRepository