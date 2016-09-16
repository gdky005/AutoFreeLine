import groovy.io.FileType

/**
 * 遍历当前目录下所有的文件
 *
 * Created by WangQing on 16/9/16.
 */

def rootPath = "/Users/WangQing/Android_Pro/JuMeiYouPin_Pro/TestFreelineAndroidDemo";

File file = new File(rootPath)

file.eachFileRecurse(FileType.FILES, {f->
    println f.getPath()
})