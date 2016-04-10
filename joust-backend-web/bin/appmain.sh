#!/bin/sh


# resolve links - $0 may be a softlink
PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

BIN_DIR=`dirname $PRG`;
APP_DIR=`readlink -f $BIN_DIR/..`;
ETC_DIR=$APP_DIR/etc;
LIB_DIR=$APP_DIR/lib;

JAVA_OPTS="$JAVA_OPTS -Dproject.basedir=$APP_DIR";
CLASSPATH="$ETC_DIR:$LIB_DIR/*"

cygwin=false

case "`uname`" in
CYGWIN*) cygwin=true;;
MING*) cygwin=true;;
esac

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin; then
  [ -n "$CLASSPATH" ] && CLASSPATH=`cygpath --path -w "$CLASSPATH"`
fi


COMMAND="java $JAVA_OPTS -cp $CLASSPATH com.joust.backend.web.AppMain"
echo $COMMAND;
$COMMAND;

