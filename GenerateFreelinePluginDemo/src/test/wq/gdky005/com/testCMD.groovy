/**
 * 测试 命令
 * Created by WangQing on 16/9/16.
 */
def rootPath = "/Users/WangQing/Android_Pro/JuMeiYouPin_Pro/TestFreelineAndroidDemo";

//// 输出 gradle 的版本号
//def my = "gradle -v"
//Process p = my.execute()
//println "${p.text}"

//想要进入 rootPath 目录下，并列举出目录下的文件，但是貌似这个管道命令有问题
println "${"cd $rootPath | ls".execute().text}"

//这是是测试项目 freeline 的初始化
println "${"gradle initFreeline -Pmirror".execute().text}"