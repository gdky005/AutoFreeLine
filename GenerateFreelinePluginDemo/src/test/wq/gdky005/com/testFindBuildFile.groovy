/**
 * 查找 build.gradle 文件
 * Created by WangQing on 16/9/16.
 */

def rootPath = "/Users/WangQing/Android_Pro/JuMeiYouPin_Pro/TestFreelineAndroidDemo";
File dir = new File(rootPath)

/**
 * 在当前目录下匹配出 build.gradle 文件
 */
dir.eachFileMatch(~/.*build\.gradle/, {file ->
    println file.getPath()
})
