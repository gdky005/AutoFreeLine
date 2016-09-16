/**
 * 主入口，读取文件，插入 freeline 的代码
 * Created by WangQing on 16/9/16.
 */
import groovy.transform.Field

/**
 * 查找文件
 * 1. 查找 Application.java 路径
 * 2. modle 目录下的build.gradle
 * 3. root 目录下的build.gradle
 *
 * Created by WangQing on 16/9/16.
 */
def rootPath = "/Users/WangQing/Android_Pro/JuMeiYouPin_Pro/TestFreelineAndroidDemo"

@Field
CharSequence myApplication = "MyApplication.java"
@Field
def modeBuild = "build.gradle"
@Field
def rootParentName
@Field
CharSequence myApplicationPath = ""
@Field
CharSequence myModleBuildPath = ""
@Field
CharSequence myRootBuildPath = ""
@Field
boolean isDebug = false



File dir = new File(rootPath)

rootParentName = dir.getName()

ArrayList blackList = getBlackList()

long pre = System.currentTimeMillis()
findFile(dir, blackList)

if (isDebug)
    println("时间差是:" + (System.currentTimeMillis() - pre))
println ""
println ""
println ""
println "( ⊙o⊙ )哇 找到 Application 的路径: $myApplicationPath"
println "( ⊙o⊙ )哇 找到 modle 目录下的build.gradle 文件路径: $myModleBuildPath"
println "( ⊙o⊙ )哇 找到 root 目录下的build.gradle 文件路径: $myRootBuildPath"
println ""
println ""
println ""
/**
 * 递归查询文件
 * @param dir
 */
void findFile(File dir, ArrayList blackList) {
    String name = dir.getName()

    //设置黑名单,如果是名单里面的目录,直接跳过
    for (int i = 0; i < blackList.size(); i++) {
        String blackName = blackList.get(i)
        if (blackName.equalsIgnoreCase(name))
            return;
    }

    if (dir.isDirectory()) {
        File[] files = dir.listFiles()
        for (int i = 0; i < files.size(); i++) {
            File file = files[i];

            findFile(file, blackList)
        }
    } else if (dir.isFile()) {
        def path = dir.getPath()

        //查找需要的自定义Application 文件路径
        if (name.contains(myApplication)) {
            myApplicationPath = path
        }

        //查找子build文件
        if (name.equalsIgnoreCase(modeBuild)) {
            def parentName = new File(dir.getParent()).getName()
            if (!parentName.equalsIgnoreCase(rootParentName)){
                myModleBuildPath = path
            } else {
                myRootBuildPath = path
            }
        }

        if (isDebug)
            println(path)
    }
}

/**
 * 获取黑名单列表
 * @return
 */
private ArrayList getBlackList() {
    ArrayList blackList = new ArrayList()

    blackList.add("bin")
    blackList.add(".gradle")
    blackList.add(".iml")
    blackList.add(".idea")
    blackList.add("build")
    blackList.add("gradle")
    blackList.add("libs")
    blackList.add("androidTest")
    blackList.add("test")
    blackList.add("res")
    blackList.add(".git")
    blackList.add(".gitignore")
    blackList
}

/**
 * 首先遍历文件 按行读取文件,看是否有匹配的关键字, 闭包
 * @param f
 * @return
 */
def traversFileContent = { File f, def preLineKeyword, String keyword, boolean isPreLineCount ->
    def currentLineCount = 0
    //空行去除掉
    def preLine
    def insertLine = 0
    def preLineFlagNum = 0

    f.eachLine { fileLine ->
        currentLineCount++;
        if (preLine != null && preLine.contains(preLineKeyword) && fileLine.contains(keyword)) {

            if (isPreLineCount)
                insertLine = preLineFlagNum
            else
                insertLine = currentLineCount
        }
        if (fileLine != null && !fileLine.equals("")) {
            preLine = fileLine
            preLineFlagNum = currentLineCount
        }
    }
    return insertLine
}

/**
 * 添加数据
 *
 * isPreLineCount  默认传false
 */
def handleData = { String filePath, String addDataLine,
                   String preKeyword, String checkKeyword, boolean isPreLineCount ->

    File file = new File(filePath)
    def haveFreeLine = false
    def separator = "\r\n"

    int insertLine = traversFileContent(file,
            preKeyword, checkKeyword, isPreLineCount)

    List<String> list = file.readLines()
    if (list == null || list.size() == 0 || insertLine == 0)
        return

    PrintWriter printWriter = file.newPrintWriter()
    for (int i = 0; i < list.size(); i++) {
        def currentLine = list.get(i)

        if (currentLine.contains(addDataLine)) {
            println "-_-# 已经存在: $addDataLine"

            if (!haveFreeLine) {
                haveFreeLine = true
                printWriter.write(currentLine + separator)
                continue;
            }
        }

        if (i == insertLine) {
            try {
                def space = ""

                line = list.get(insertLine - 1)

                if (!line.contains(addDataLine)) {

                    int index = line.indexOf(checkKeyword)
                    if (index >= 0) space = line.substring(0, index);

                    String addLine = space + addDataLine
                    line = addLine + separator + currentLine

                    haveFreeLine = true
                    addLineCount = insertLine + 1
                    if (isDebug)
                        println ":-> 添加数据成功, 当前插入行数是: $addLineCount"
                } else {
                    haveFreeLine = true
                    line = currentLine
                }

            } catch (Exception e) {
                e.printStackTrace()
                break;
            }

            printWriter.write(line + separator)
        } else {
            if (currentLine.contains(addDataLine))
                continue;

            printWriter.write(currentLine + separator)
        }
    }

    printWriter.flush()
    printWriter.close()


}

//给根目录下的build.gradle 文件插入相关数据
def rootBuildFile = { String rootBuildFilePath ->
    String classpathKeyword = "classpath 'com.antfortune.freeline:gradle:0.5.5'"
    handleData(rootBuildFilePath, classpathKeyword, "dependencies", "classpath", false)
}

//给Application添加
def modelApplicationFile = { String modelApplicationFilePath ->
    def initKeyword = "FreelineCore.init(this);"
    handleData(modelApplicationFilePath, initKeyword, "onCreate()", "super.onCreate()", false)
    def importKeyword = "import com.antfortune.freeline.FreelineCore;"
    handleData(modelApplicationFilePath, importKeyword, "package", "import", false)
}

//插入 model 的依赖
def modelBuildGradleFile = { String modelBuildGradleFilePath ->
    def compileKeword = "compile 'com.antfortune.freeline:runtime:0.5.5'"
    handleData(modelBuildGradleFilePath, compileKeword, "dependencies", "compile fileTree", false)

    //在 model 里面插入需要的代码
    def freelineKeword = '''
    freeline {
        hack true
    }
    '''
    handleData(modelBuildGradleFilePath, freelineKeword, "apply plugin", "android {", false)

    //在modle 的依赖中添加 apply 插件
    def applyKeword = "apply plugin: 'com.antfortune.freeline'"
    handleData(modelBuildGradleFilePath, applyKeword, "apply plugin", "android {", true)
}

rootBuildFile(myRootBuildPath)
modelApplicationFile(myApplicationPath)
modelBuildGradleFile(myModleBuildPath)

println ""
println ""
println ""
println "~~~^_^~~~  已经将 Freeline 插入到项目中，请享受吧 ~~~^_^~~~ "
println ""
println ""
println ""