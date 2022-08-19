#!/bin/bash
set -e

if [[ -z $1 ]]; then
  echo "ERROR: version is not set"
  echo "Usage: bump-cs-version-in-ant-project.sh <version>"
  exit 1
fi

FILE=ant-project/ivy.xml
VERSION=$1

xmlstarlet edit --omit-decl -P --pf --inplace -u \
  '//dependencies/dependency[@name="checkstyle"]/@rev' \
  -v "$VERSION" $FILE
echo "Version updated to $VERSION at $FILE"
