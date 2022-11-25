#!/bin/sh

export PATH=/Users/jamesbell/jogl24/lib:$PATH
export CLASSPATH=.:/Users/jamesbell/jogl24/jar/jogl-all.jar:/Users/jamesbell/jogl24/jar/gluegen-rt.jar:$CLASSPATH

rm *.class

javac *.java

java "$1"