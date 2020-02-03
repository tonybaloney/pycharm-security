#!/bin/sh -l
cd /github/workspace
echo "Scanning $1"
/opt/pycharm-community/bin/inspect.sh $1 /sources/SecurityInspectionProfile.xml out/ -format json -v0 2> errors.log

for i in out/*.json; do
    cat $i | python3 /code/parse.py
done