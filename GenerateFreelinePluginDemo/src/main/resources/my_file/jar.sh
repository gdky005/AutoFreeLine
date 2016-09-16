#!/usr/bin/env bash
echo "当前 sh 的命令路径是:" $0
a=$0
b=${a%/*}
echo "自动跳转到 " $b
cd $b
# pwd
# java -jar GenerateFreelinePluginDemo-1.0-SNAPSHOT.jar

b=$(ls *.jar)
echo "查找到 jar 文件：" $b
java -jar $b