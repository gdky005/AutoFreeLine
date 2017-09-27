package wq.gdky005

///**
// * Created by WangQing on 2017/9/22.
// */
//class LocalMaven {
//}

def runLocalMaven = { def proDir, String[] mainModuleList, String libModule, def gradleProperties ->

    log("当前的项目路径是：$proDir")
    log("当前的 mainModule 路径是：$mainModuleList")
    log("当前的 libModule 路径是：$libModule")

//-----------------------------------GAV-start------------------------------------------------------------------------------------
    def projectStr = "project"
    def libName = libModule.replace("/", ":")

    def props = new Properties()
    def propsPath = proDir + libModule + gradleProperties
    log("当前读取的配置文件路径是：$propsPath")
    new File(propsPath).withInputStream {
        stream -> props.load(stream)
    }
    def group = props.getProperty("GROUP_ID")
    def artifact = props.getProperty("ARTIFACT_ID")
    def version = props.getProperty("VERSION")
    logSeparator()
    log("GAV-->")
    log("GROUP_ID：" + group)
    log("ARTIFACT_ID：" + artifact)
    log("VERSION：" + version)
    def GAV = group + ":" + artifact + ":" + version
    def libGAV = "\tcompile '" + GAV + "'"
//----------------------------------GAV-end------------------------------------------------------------------------------------

//----------------------------------local cmd--start----------------------------------------------------------------------
//运行本地命令
    def uploadCMD = "gradle " + libModule.replace("/", ":") + ":uploadArchives"
    log("当前 uploadArchives 命令是：$uploadCMD")
//----------------------------------local cmd--end------------------------------------------------------------------------


    def currentPath = proDir

    logSeparator()
    log("$libModule is start uploadArchives!")
    Process publish = uploadCMD.execute(null, new File(proDir))
    try {
        publish.text
//        log("$libModule is uploadArchives: " + publish.text)
//        publish.waitFor()
    } catch (Exception e) {
        println "出错了:" + e.getMessage()
    }

    log("$libModule is uploadArchives finish!")
    logSeparator()

    if (mainModuleList != null) {
        mainModuleList.each { mainModule ->
            List<String> list = new ArrayList<>()
            log("开始处理 $mainModule")
            new File(currentPath + mainModule).eachFileMatch(~/.*\.gradle/) { file ->
                log("当前文件名是：" + file.getName())
                logSeparator()

                def i = 0
                def needNum = 0
                def gavNum = 0
                def projectNum = 0
                def projectData = ""

                // 查询 位置
                log("开始查找相关的字段")
                file.eachLine { line ->
                    ++i
                    if (line != null && line.contains("dependencies")) {
                        log("查找到 dependencies")
                        needNum = i
                    }

                    if (line != null && line.contains(group + ":" + artifact)) {
                        log("查找到" + group + ":" + artifact)
                        gavNum = i
                    }

                    if (line != null && line.contains(projectStr) && line.contains(libName)) {
                        log("查找到 " + libName)
                        projectNum = i
                        projectData = line
                    }

                    list.add(line)
                }
                log("查找相关的字段 完成")

                logSeparator()
                log("当前的 dependencies 是第 $needNum 行")
                log("当前的" + GAV + "是第 $gavNum 行")
                log("当前的" + "project:lib" + "是第 $projectNum 行")
                logSeparator()

                //插入数据
                log("开始处理文件中的数据")
                if (gavNum > 0) { //说明存在，只修改版本号
                    int number = gavNum - 1
                    def gavStr = list.get(number)

                    String newStr = gavStr.substring(0, gavStr.lastIndexOf(":") + 1) + version + "'"

                    if (number > 0) {
                        log("移除 $number 行")
                        list.remove(number)

                        log("添加 $number 行， 新内容是：$newStr")
                        list.add(number, newStr)
                    }

                    if (projectNum > 0) {
                        log("移除 $projectNum 行")
                        list.remove(projectNum - 1)
                    }

                } else { // 说明不存在，需要直接添加。  need 加3后添加
//                    def addNum = needNum + 3
                    if (projectNum > 0) {
                        log("添加 $projectNum 行， 新内容是：$libGAV")
                        list.add(projectNum, libGAV)
                    }

                    // projectData
                    if (projectData != null && !projectData.equals("")) {
                        log("需要删除的数据是：$projectData")
                        list.remove(projectData)
                    }
                }
                logSeparator()

                def newFilePath = file.getPath()
                log("开始将处理后的数据写入到文件中: $newFilePath")
                write2File(newFilePath, list)
                log("写入文件完成")

            }
            logSeparator()
            log("$mainModule 处理完成！")
        }

    } else {
        log("$mainModuleList 为空，跳过")
    }
}

//批量运行
def operationLocalMaven = { String[] modulePath, proDir, String[] mainModule ->
    def gradleProperties = "/gradle.properties"
    logSeparator()
    log("LocalMaven is starting!", true)
    logSeparator()

    modulePath.each { model ->
        runLocalMaven(proDir, mainModule, model, gradleProperties)
    }

    logSeparator()
    log("LocalMaven already ending!", true)
    logSeparator()
}

/**
 * 将 List 写入到 文件中
 *
 * @param file
 * @param list
 */
private static write2File(String file, List<String> list) {
    File newFile = new File(file)

    if (!newFile.exists()) {
        newFile.createNewFile()
        log("需要处理的文件不存在，已经重新创建")
    }

    newFile.withPrintWriter { out ->
        for (int k = 0; k < list.size(); k++) {
            out.println(list.get(k))
        }
    }
}

private static log(String msg) {
    println(msg)
}

private static log(String msg, boolean isCenter) {
    if (isCenter) {
        println()
        println("                                              " + msg)
        println()
    } else {
        log(msg)
    }
}

private static logSeparator() {
    println("-----------------------------------------------------------------------------------------------")
}


def modulePath
def mainModule = null
def proDir = "/Users/WangQing/mobile_android/JuMeiYouPin"

/**
 *************************************************************************************
 *******  以下方式可以保证，能上次打出来的 aar, 速度略快，可以逐步控制。   ***********
 *************************************************************************************
 */

/**
 * 第一组
 */
//需要替换的 module 中包含 project,可以自动替换
mainModule = [
        "/bizlib/uiwidget",
        "/baselib",
] as String[]

//需要打包，发布到本地仓库。
// 1. 务必修改该 Lib 里面的 build.gradle， 添加 apply from 相关属性。
// 2. gradle.properties 必须添加 GAV
modulePath = [
        "/JuMeiUI",
        "/bizlib/resources",
        "/bizlib/protocol",
] as String[]

operationLocalMaven(modulePath, proDir, mainModule)

/**
 * 第二组
 */
mainModule = null
modulePath = [
        "/baselib",
] as String[]
operationLocalMaven(modulePath, proDir, mainModule)

/**
 * 第三组
 */
mainModule = null
modulePath = [
        "/addcart_lib",
        "/bizlib/uiwidget",
] as String[]
operationLocalMaven(modulePath, proDir, mainModule)

