#!/bin/sh -l
cd /github/workspace
echo "Scanning $1"
/opt/pycharm-community/bin/inspect.sh $1 /sources/SecurityInspectionProfile.xml out/ -format json -v0 2> errors.log
ls -aR out/
cat out/*.json | python3 parse.py
