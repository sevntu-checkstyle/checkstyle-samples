#!/bin/bash
set -e


case $1 in

build-ant-project)
  cd ant-project
  ant checkstyle
  ;;

build-gradle-project)
  cd gradle-project
  ./gradlew clean check --debug --stacktrace
  ;;

build-maven-project)
  cd maven-project
  mvn checkstyle:checkstyle -Pgithub-maven-repo
  mvn -f pom-google-custom-suppression.xml clean checkstyle:checkstyle -Pgithub-maven-repo
  grep "error" target/checkstyle-result.xml
  mvn -f pom-google.xml clean checkstyle:checkstyle -Pgithub-maven-repo
  mvn site
  mvn checkstyle:checkstyle -Pgithub-maven-repo -Dcheckstyle.skip=true
  ;;

build-maven-ant-project)
  cd maven-ant-project
  mvn clean verify
  grep "error" target/checkstyle-result.xml
  mvn verify -Dcheckstyle.ant.skip=true
  ;;

git-diff)
  if [ "$(git status | grep 'Changes not staged\|Untracked files')" ]; then
    printf "Please clean up or update .gitattributes file.\nGit status output:\n"
    printf "Top 300 lines of diff:\n"
    git status
    git diff | head -n 300
    false
  fi
  ;;

*)
  echo "Unexpected argument: $1"
  sleep 5s
  false
  ;;

esac
