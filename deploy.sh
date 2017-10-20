#!/bin/bash


API=https://api.bintray.com
PACKAGE_DESCRIPTOR=bintray-package.json

## Define the following variables:
## BT_USER=???
## BT_KEY=???
## BT_ORG=???
## BT_REPO=dekaf
## VERSION=2.0.0.version

if [ -z "${VERSION}" ]; then VERSION="dev"; fi;

echo BinTray User:         ${BT_USER}
echo BinTray Organization: ${BT_ORG}
echo BinTray Repor:        ${BT_REPO}
echo Package:              ${PACKAGE}
echo Version:              ${VERSION}

if [ -z "${BT_USER}" ]; then echo "BinTray User Name is not specified :(";    exit -91; fi
if [ -z "${BT_ORG}"  ]; then echo "BinTray Organization is not specified :("; exit -92; fi
if [ -z "${BT_REPO}" ]; then echo "BinTray Repository is not specified :(";   exit -93; fi
if [ -z "${BT_KEY}"  ]; then echo "BinTray API key is not specified :(";      exit -99; fi

CURL="curl -u${BT_USER}:${BT_KEY} -H Content-Type:application/json -H Accept:application/json"

DIR=dist


upload_file() {
  echo "Uploading file $1..."
  UPLOAD_PARAMETERS="-H X-Bintray-Package:$2 -H X-Bintray-Version:${VERSION} -H X-Bintray-Publish:1 -H X-Bintray-Override:1"
  ${CURL} --write-out %{http_code} -T ${DIR}/$1 ${UPLOAD_PARAMETERS} ${API}/content/${BT_ORG}/${BT_REPO}/org/jetbrains/dekaf/$2/${VERSION}/$1
  echo ""
}


upload_package() {
  upload_file  $1-${VERSION}.pom          $1
  upload_file  $1-${VERSION}.jar          $1
  upload_file  $1-${VERSION}-sources.jar  $1
}


upload_packages() {
  upload_package dekaf-single
  upload_package dekaf-single-test-db
}


upload_packages
echo "OK"
