#!/bin/bash

SCRIPT=$(readlink -f "$0")
BASEDIR=$(dirname "$SCRIPT")
cd $BASEDIR

if [ ! -d code ] || [ ! -f makelist.txt ]
then
    echo "ERROR: $BASEDIR is not a valid directory of SDK-java for ecs."
    echo "  Please run this script in a regular directory of SDK-java."
    exit -1
fi

rm -f ecs.tar.gz
tar -zcPf ecs.tar.gz *