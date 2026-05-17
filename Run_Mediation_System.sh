#!/bin/bash

MAVEN=mvn

cd $HOME/telecom-mediation-system/Mediation-System

echo "====================================="
echo "⚙️  COMPILING PROJECT..."
echo "====================================="
$MAVEN clean compile

echo "====================================="
echo "🚀 RUNNING MEDIATION SYSTEM..."
echo "====================================="
$MAVEN exec:java -Dexec.mainClass=Main_PK.MediationSystem