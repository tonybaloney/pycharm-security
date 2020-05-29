#!/bin/sh

export JAVA_TOOL_OPTIONS="-agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=y"
/action.sh . /sources/SecurityInspectionProfile.xml