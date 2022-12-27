#!/bin/bash
set -e

if [[ -z $1 ]]; then
  echo "ERROR: version is not set"
  echo "Usage: bump-cs-version-in-maven-project.sh <version>"
  exit 1
fi

VERSION=$1

cd maven-project
mvn -e --no-transfer-progress versions:set-property -Dproperty=checkstyle.version \
  -DnewVersion="$VERSION" -DgenerateBackupPoms=false

echo "Version updated to $VERSION at maven-project/pom.xml"
