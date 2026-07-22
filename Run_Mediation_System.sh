#!/bin/bash
cp -u upstream-node-image/cdr-generator.sh /tmp/upstream-node-image/cdr-generator.sh;
cp -u upstream-node-image/pgw-cdr-generator.sh /tmp/upstream-node-image/pgw-cdr-generator.sh;
cp -u upstream-node-image/smsc-cdr-generator.sh /tmp/upstream-node-image/smsc-cdr-generator.sh;
clear;
cd Mediation-System;
mvn clean compile > /dev/null 2>&1;

clear;
# run app (quiet mode)
mvn -q exec:java;
clear;