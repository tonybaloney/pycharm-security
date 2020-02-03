#!/bin/sh -l
cd /github/workspace
echo "Scanning $1"
ls -R .
/opt/pycharm-community/bin/inspect.sh $1 /sources/SecurityInspectionProfile.xml out.log -format plain -v0 2> errors.log
echo ::set-output name=result::$(< out.log)
cat errors.log
