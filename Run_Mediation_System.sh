#!/bin/bash

clear;
cd Mediation-System;
mvn clean compile > /dev/null 2>&1;

clear;
# run app (quiet mode)
mvn -q exec:java;
clear;