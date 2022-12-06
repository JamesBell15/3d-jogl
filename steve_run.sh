#!/bin/sh
rm *.class

javac *.java

java "$1"