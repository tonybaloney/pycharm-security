#!/bin/sh -l

echo "Scanning $1"
/opt/pycharm-community/bin/inspect.sh /code /sources/SecurityInspectionProfile.xml out.log -format plain -v0 2> /dev/null
echo ::set-output name=result::$(< out.log)
