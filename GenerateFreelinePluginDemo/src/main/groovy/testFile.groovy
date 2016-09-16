

def separator = "\r\n"

def rootPath = "/Users/WangQing/Android_Pro/JuMeiYouPin_Pro/MyApplication7";
//def rootGradleFileKeyword = "build"
def rootGradleFileKeyword = "wq"

def ROOT_GRADLE_KEYWORD_DEPENDENCIES = "dependencies"
def ROOT_GRADLE_KEYWORD_CLASSPATH = "classpath"
def ROOT_GRADLE_ADD_FREELINE = "classpath 'com.antfortune.freeline:gradle:0.5.5'"

def haveFreeline = false


File dir = new File(rootPath)
dir.eachFileMatch(~/.*\.gradle/) { file ->
    if (file.isFile() && file.name.contains(rootGradleFileKeyword)) {
        String line = ""

        int insertLine = traversFileContent(file,
                ROOT_GRADLE_KEYWORD_DEPENDENCIES, ROOT_GRADLE_KEYWORD_CLASSPATH)

        List<String> list = file.readLines()
        if (list == null || list.size() == 0 || insertLine == 0)
            return

        PrintWriter printWriter = file.newPrintWriter()
        for (int i = 0; i < list.size(); i++) {
            def currentLine = list.get(i)

            if (currentLine.contains(ROOT_GRADLE_ADD_FREELINE)) {
                println "已经存在: $ROOT_GRADLE_ADD_FREELINE"

                if (!haveFreeline) {
                    haveFreeline = true
                    printWriter.write(currentLine + separator)
                    continue;
                }
            }

            if (i == insertLine) {
                try {
                    def space = ""
                    line = list.get(insertLine - 1)

                    if (!line.contains(ROOT_GRADLE_ADD_FREELINE)) {

                        int index = line.indexOf(ROOT_GRADLE_KEYWORD_CLASSPATH)
                        if (index >= 0) space = line.substring(0, index);

                        String addLine = space + ROOT_GRADLE_ADD_FREELINE
                        line = addLine + separator + currentLine

                        haveFreeline = true
                        addLineCount = insertLine + 1
                        println "添加数据成功, 当前插入行数是: $addLineCount"
                    } else {
                        haveFreeline = true
                        line = currentLine
                    }

                } catch (Exception e) {
                    e.printStackTrace()
                    break;
                }

                printWriter.write(line + separator)
            } else {
                if (currentLine.contains(ROOT_GRADLE_ADD_FREELINE))
                    continue;

                printWriter.write(currentLine + separator)
            }
        }

            printWriter.flush()
            printWriter.close()
    }
}

/**
 * 首先遍历文件 按行读取文件,看是否有匹配的关键字, 闭包
 * @param f
 * @return
 */
int traversFileContent(File f, String preLineKeyword, String keyword) {
    def currentLineCount = 0
    //空行去除掉
    def preLine
    def insertLine = 0

    f.eachLine { line ->
        currentLineCount++;
        if (preLine != null && preLine.contains(preLineKeyword) && line.contains(keyword)) {
            insertLine = currentLineCount
        }

        if (line != null && !line.equals(""))  preLine = line
    }

    return insertLine
}