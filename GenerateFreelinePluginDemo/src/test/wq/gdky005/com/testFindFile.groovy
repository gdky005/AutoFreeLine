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
def rootParentName
@Field
def modeBuild = "build.gradle"
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


println "我需要的路径是: $myApplicationPath"
println "我找到我需要modle 目录下的build.gradle 文件了哈: $myModleBuildPath"
println "我找到我需要root 目录下的build.gradle 文件了哈: $myRootBuildPath"

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