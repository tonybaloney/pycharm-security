#!/bin/sh -l
cd /github/workspace
echo "Scanning $1 with profile $2"
if ["$4" != ""]; then
  EXCLUDE="-e \"$4\""
else
  EXCLUDE=""
fi
/opt/pycharm-community/bin/inspect.sh -d "$1" -profilePath "$2" out/ -format json -v0 $EXCLUDE 2> errors.log

set -e
HASWARNINGS=0
for i in $(find out -name "*.json"); do
    RUNHASWARNINGS=0
    cat $i | python3 /code/parse.py || RUNHASWARNINGS=$?
    if [ $RUNHASWARNINGS -ne 0 ]; then
        HASWARNINGS=1
    fi
    echo "::set-output name=result::$i"
done

if [ $HASWARNINGS -ne 0 ]; then
  if [ "$3" != "no" ]; then
    echo "Found issues in code"
    exit 1
  fi
else
  echo "Found no issues in the code"
fi
