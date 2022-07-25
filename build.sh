#!/bin/sh

mkdir -p target

cd target
git clone https://github.com/rafi612/serverd
cd serverd
./mvnw install
cd ../..

./mvnw clean package
