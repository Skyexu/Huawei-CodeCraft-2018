#!/bin/bash
basepath=$(cd `dirname $0`; pwd)
APP_HOME=$basepath/..

JAVA=$JAVA_HOME/bin/java

JVM_OPT="-Xms64M -Xmx64M"
JVM_OPT="$JVM_OPT -Djava.library.path=$APP_HOME/bin"
JVM_OPT="$JVM_OPT -classpath"
JVM_OPT="$JVM_OPT $APP_HOME/bin/ecs.jar"
dataCaseFile=$1
inputCaseFile=$2
resultFilePath=$3
$JAVA $JVM_OPT $JAVAENV com.filetool.main.Main $dataCaseFile $inputCaseFile $resultFilePath 2>&1
exit
