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
//def rootPath = "/Users/WangQing/Android_Pro/JuMeiYouPin_Pro/TestFreelineAndroidDemo"
def rootPath = ""

@Field
CharSequence myApplication = "Application.java"
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
/**
 * 当前运行路径
 */
@Field
String currentPath = System.getProperty("user.dir")

def excludeDirFile = "exclude_dir.properties"
def proPathFile = "your_pro_path.properties"
def debugPathFile = "debug.properties"

/**
 * 读取配置文件
 */
def localProperties = { String configFile ->
    String proFilePath = currentPath + "/" + configFile;
    InputStream inputStream = new BufferedInputStream(new FileInputStream(proFilePath));
    ResourceBundle property = new PropertyResourceBundle(inputStream);
    Set set = property.keySet()

    List list = new ArrayList();
    list.addAll(set)

    if (isDebug) {
        for (String item : list) {
            println "当前过滤的内容：$item"
        }
    }
    list
}

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
            if (!parentName.equalsIgnoreCase(rootParentName)) {
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

/**
 * 给根目录下的build.gradle 文件插入相关数据
 */
def rootBuildFile = { String rootBuildFilePath ->
    String classpathKeyword = "classpath 'com.antfortune.freeline:gradle:0.5.5'"
    handleData(rootBuildFilePath, classpathKeyword, "dependencies", "classpath", false)
}

/**
 * 给Application添加
 */
def modelApplicationFile = { String modelApplicationFilePath ->
    def initKeyword = "FreelineCore.init(this);"
    handleData(modelApplicationFilePath, initKeyword, "onCreate()", "super.onCreate()", false)
    def importKeyword = "import com.antfortune.freeline.FreelineCore;"
    handleData(modelApplicationFilePath, importKeyword, "package", "import", false)
}

/**
 * 插入 model 的依赖
 */
def modelBuildGradleFile = { String modelBuildGradleFilePath ->
    def compileKeword = "compile 'com.antfortune.freeline:runtime:0.5.5'"
    handleData(modelBuildGradleFilePath, compileKeword, "dependencies", "compile fileTree", false)

    //在 model 里面插入需要的代码
    def freelineKeword = '''
    freeline {
        hack true
    }
    '''
    //专门针对聚美
//    def freelineKeword = '''
//    def flavor = "jmtest"
//
//    version = android.defaultConfig.versionCode
//
//    freeline {
//        productFlavor flavor
//        hack true
//
//        def dir = System.getProperty("user.dir");
//        apkPath dir + "/ExportApks/"+ flavor + "_" + version + ".apk"
//    }
//    '''
    handleData(modelBuildGradleFilePath, freelineKeword, "apply plugin", "android {", false)

    //在modle 的依赖中添加 apply 插件
    def applyKeword = "apply plugin: 'com.antfortune.freeline'"
    handleData(modelBuildGradleFilePath, applyKeword, "apply plugin", "android {", true)
}

/**
 * 开始的 log
 * @param isDebug
 * @param pre
 * @param myApplicationPath
 * @param myModleBuildPath
 * @param myRootBuildPath
 */
private void preLog(boolean isDebug, long pre, String myApplicationPath, String myModleBuildPath, String myRootBuildPath) {
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
}

/**
 * 判断字符串是否为空
 * @param str
 * @return
 */
private boolean isEmpty(String str) {
    if (str == null || str.length() == 0)
        return true;
    else
        return false;
}

/**
 * 结束的 log
 */
private void endLog() {
    println ""
    println ""
    println ""
    println "~~~^_^~~~  已经将 Freeline 插入到项目中，请享用吧 ~~~^_^~~~ "
    println ""
    println ""
    println ""
}

/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * ~~~~~~~~~~~~~~~ Main 代码 ~~~~~~~~~~~~~~
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

try {
    String path = localProperties(debugPathFile).get(0)
    isDebug = path.contains("true")
    println "欢迎使用 freeline 快速集成脚本, 这是 Debug 版本，可以根据下面的提示信息进行调试。"
} catch (Exception e) {
    println "欢迎使用 freeline 快速集成脚本"
    isDebug = false
}



try {
    String path = localProperties(proPathFile).get(0)
    rootPath = path.trim()
} catch (Exception e) {
    println "读取配置文件失败，请在当前文件目录放入 your_pro_path.properties(没有请自建文件) 文件，并在里面写入当前项目的绝对路径，否则将会自动寻找项目路径（比较重要，如果不配置项目地址，请把这些文件放在你的项目的跟目录下面）"
    rootPath = currentPath
}
println "当前项目的真实路径是： $rootPath"

File dir = new File(rootPath)
rootParentName = dir.getName()

ArrayList dirBlackList = new ArrayList()
try {
    dirBlackList = localProperties(excludeDirFile)
} catch (Exception e) {
    println "（可以忽略）读取配置文件失败，请在当前文件目录放入 exclude_dir.properties (没有请自建文件)文件，并在里面写入需要忽略的文件，否则会使用默认忽略配置属性"
} finally {
    dirBlackList.addAll(getBlackList())
}

long pre = System.currentTimeMillis()
findFile(dir, dirBlackList)

if (isEmpty(myApplicationPath) || isEmpty(myModleBuildPath) || isEmpty(myRootBuildPath)) {
    println "sorry, 查找项目失败，不能正确配置 freeline，请根据以上 log 查看是否有地方配置错误，更正后，请重试，谢谢！"
    return
}
println "查找文件 成功"

preLog(isDebug, pre, myApplicationPath, myModleBuildPath, myRootBuildPath)

rootBuildFile(myRootBuildPath)
modelApplicationFile(myApplicationPath)
modelBuildGradleFile(myModleBuildPath)



println "开始执行命令：gradle initFreeline -Pmirror, 请耐心等待一小会，就会完成。莫要着急哦"
def cmd = "gradle initFreeline -Pmirror"
println cmd.execute().text
println "初始化 freeline 成功，请使用 python freeline.py 运行项目。 或者可以使用插件运行"

endLog()