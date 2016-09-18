#!/usr/bin/env bash
echo "当前 sh 的命令路径是:" $0
filePath=$0
chart="/"
if [[ $filePath =~ $chart ]]; then
	parentPath=${filePath%/*}
	echo "自动跳转到 " $parentPath
	cd $parentPath
fi
pwd
java -jar GenerateFreelinePluginDemo-1.0.jar

parentPath=$(ls *.jar)
echo "查找到 jar 文件：" $parentPath
java -jar $parentPath