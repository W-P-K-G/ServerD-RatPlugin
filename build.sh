#!/bin/sh

git clone https://github.com/rafi612/serverd

cd serverd && mvn install && cd ..

mvn clean package
