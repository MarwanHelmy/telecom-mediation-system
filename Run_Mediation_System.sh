#!/bin/bash

docker start msc_node
docker start pgw_node
docker start smsc-node

export JAVA_HOME=$HOME/portable-java/current
export PATH=$JAVA_HOME/bin:$PATH
MAVEN=$HOME/portable-netbeans/current/java/maven/bin/mvn

clear;
cd $HOME/telecom-mediation-system/Mediation-System;
$MAVEN clean compile > /dev/null 2>&1;

clear;
# run app (quiet mode)
$MAVEN -q exec:java -Dexec.mainClass=Main_PK.MediationSystem;
clear;
