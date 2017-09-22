package wq.gdky005

///**
// * Created by WangQing on 2017/9/22.
// */
//class LocalMaven {
//}

def proDir = "/Users/WangQing/Android_Pro/JuMeiYouPin_Pro/LocalMavenDemo"
def mainModule = "/app"
def gradleProperties = "gradle.properties"

//-----------------------------------GAV-start------------------------------------------------------------------------------------
def projectStr = "project"
def libName = "lib"

def props = new Properties()
new File(proDir + "/" + gradleProperties).withInputStream {
    stream -> props.load(stream)
}
def group = props.getProperty("GROUP_ID")
def artifact = props.getProperty("ARTIFACT_ID")
def version = props.getProperty("VERSION")

def GAV = group + ":" + artifact + ":" + version
def libGAV = "\timplementation '" + GAV + "'"
//----------------------------------GAV-end------------------------------------------------------------------------------------




//----------------------------------local cmd--start----------------------------------------------------------------------
//运行本地命令

def gradlePublishCMD = "gradle uploadArchives"
//def gradlePublishCMD = "gradle clean"
//def gradlePublishCMD = "gradle build"
//System.out.println(System.getProperty("user.dir"))

//----------------------------------local cmd--end------------------------------------------------------------------------


currentPath = proDir

println("项目目录是：" + proDir)

Process publish = gradlePublishCMD.execute(null, new File(proDir))
publish.waitFor()
println(publish.text)

// 开始替换文件

//替换完文件以后 先尝试构建一次

//
///**
// * 读取配置文件
// */
//def localProperties = { String configFile ->
//    String proFilePath = currentPath + "/" + configFile;
//    InputStream inputStream = new BufferedInputStream(new FileInputStream(proFilePath));
//    ResourceBundle property = new PropertyResourceBundle(inputStream);
//    Set set = property.keySet()
//
//    List list = new ArrayList();
//    list.addAll(set)
//
//    if (isDebug) {
//        for (String item : list) {
//            println "当前过滤的内容：$item"
//        }
//    }
//    list
//}
//
//localProperties("app")

List<String> list = new ArrayList<>()
new File(currentPath + mainModule).eachFileMatch(~/.*\.gradle/) { file ->
    println "当前文件名：" + file.getName()

    def i = 0
    def needNum = 0
    def gavNum = 0
    def projectNum = 0

    // 查询 位置
    file.eachLine { line ->
        println line
        ++i
        if (line != null && line.contains("dependencies")) {
            needNum = i
        }

        if (line != null && line.contains(group + ":" + artifact)) {
            gavNum = i
        }

        if (line != null && line.contains(projectStr) && line.contains(libName)) {
            projectNum = i
        }

        list.add(line)
    }

    println "当前的 dependencies 是第 " + needNum + " 行"
    println "当前的" + GAV + "是第 " + gavNum + " 行"
    println "当前的" + "project:lib" + "是第 " + projectNum + " 行"


    //插入数据
    if (gavNum > 0) { //说明存在，只修改版本号
        int number = gavNum - 1
        def gavStr = list.get(number)

        String newStr = gavStr.substring(0, gavStr.lastIndexOf(":") + 1) + version + "'"

        if (number > 0)
            list.remove(number)

        list.add(number, newStr)

        if (projectNum > 0)
            list.remove(projectNum - 1)

    } else { // 说明不存在，需要直接添加。  need 加3后添加
        list.add(needNum + 3, libGAV)
    }

    // 将 list 写入到原始文件中
    println(list.toString())

    write2File(file.getPath(), list)

}

/**
 * 将 List 写入到 文件中
 *
 * @param file
 * @param list
 */
private static void write2File(String file, List<String> list) {
    File newFile = new File(file)

    if (!newFile.exists())
        newFile.createNewFile()

    newFile.withPrintWriter { out ->
        for (int k = 0; k < list.size(); k++) {
            out.println(list.get(k))
        }
    }
}