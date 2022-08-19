#!/bin/bash
set -e

if [[ -z $1 ]]; then
  echo "ERROR: version is not set"
  echo "Usage: bump-cs-version-in-gradle-project.sh <version>"
  exit 1
fi

VERSION=$1
GRADLE_FILE=gradle-project/build.gradle

sed -i'' -e "s/checkstyleVersion =.*/checkstyleVersion = '$VERSION'/g" $GRADLE_FILE

echo "Version updated to $VERSION at $GRADLE_FILE"
