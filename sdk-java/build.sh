#!/bin/bash
basepath=$(cd `dirname $0`; pwd)
APP_HOME=$basepath

#编译
echo building...
MAKE_FILE=$APP_HOME/makelist.txt
cd "$APP_HOME/code/ecs/src"
javac -source 1.8 -target 1.8 -d $APP_HOME/code/ecs/bin -encoding UTF-8 @$MAKE_FILE
tmp=$?
if [ ${tmp} -ne 0 ]
then
 echo "ERROR: javac failed:" ${tmp}
 exit -1
fi



#打包
echo make jar...
cd "$APP_HOME/code/ecs/bin"
JAR_NAME=$APP_HOME/bin/ecs.jar
jar -cvf $JAR_NAME *
tmp=$?
if [ ${tmp} -ne 0 ]
then
 echo "ERROR: jar failed:" ${tmp}
 exit -1
fi

cd $APP_HOME


if [ -f ecs.tar.gz ]
then
    rm -f ecs.tar.gz
fi

tar -zcPf ecs.tar.gz *

echo build jar success!
exit
