#!/bin/bash


API=https://api.bintray.com
PACKAGE_DESCRIPTOR=bintray-package.json

## Define the following variables:
## BT_USER=???
## BT_KEY=???
## BT_ORG=???
## BT_REPO=dekaf

echo BinTray User:         ${BT_USER}
echo BinTray Organization: ${BT_ORG}
echo BinTray Repor:        ${BT_REPO}

if [ -z "${BT_USER}" ]; then echo "BinTray User Name is not specified :(";    exit -91; fi
if [ -z "${BT_ORG}"  ]; then echo "BinTray Organization is not specified :("; exit -92; fi
if [ -z "${BT_REPO}" ]; then echo "BinTray Repository is not specified :(";   exit -93; fi
if [ -z "${BT_KEY}"  ]; then echo "BinTray API key is not specified :(";      exit -99; fi


PACKAGE=dekaf2
VERSION=2.0.0.313.beta1

CURL="curl -u${BT_USER}:${BT_KEY} -H Content-Type:application/json -H Accept:application/json"

DIR=dist


upload_file() {
  echo "Uploading file $1..."
  UPLOAD_PARAMETERS="-H X-Bintray-Package:${PACKAGE} -H X-Bintray-Version:${VERSION} -H X-Bintray-Publish:1 -H X-Bintray-Override:1"
  ${CURL} --write-out %{http_code} -T ${DIR}/$1 ${UPLOAD_PARAMETERS} ${API}/content/${BT_ORG}/${BT_REPO}/$1
  echo ""
}


upload_files() {
  upload_file dekaf-single-${VERSION}.jar
  upload_file dekaf-single-${VERSION}-sources.jar
  upload_file dekaf-single-test-db-${VERSION}.jar
  upload_file dekaf-single-test-db-${VERSION}-sources.jar
}


upload_files

