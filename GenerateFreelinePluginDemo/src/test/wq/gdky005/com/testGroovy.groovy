/**
 * 测试 groovy 脚本
 * 读取 build.gradle 文件
 *
 * Created by WangQing on 16/9/16.
 */
def path = "/Users/WangQing/Android_Pro/JuMeiYouPin_Pro/TestFreelineAndroidDemo/app/build.gradle"
def ROOT_GRADLE_KEYWORD_CLASSPATH1 = "classpath"
def ROOT_GRADLE_KEYWORD_DEPENDENCIES1 = "dependencies"

def traversFileContent = { File f, String preLineKeyword, String keyword ->
    def currentLineCount = 0
    //空行去除掉
    def preLine
    def insertLine = 0

    f.eachLine { fileLine ->
        currentLineCount++;
        if (preLine != null && preLine.contains(preLineKeyword) && fileLine.contains(keyword)) {
            insertLine = currentLineCount
        }

        println(fileLine)
        if (fileLine != null && !fileLine.equals("")) preLine = fileLine
    }

    return insertLine
}

int a = traversFileContent(new File(path), "dependencies", "classpath")