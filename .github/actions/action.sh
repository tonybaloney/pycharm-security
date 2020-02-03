#!/bin/sh -l
cd /github/workspace
echo "Scanning $1"
/opt/pycharm-community/bin/inspect.sh $1 /sources/SecurityInspectionProfile.xml out.json -format json -v0 2> errors.log
cat out.json | python parse.py
