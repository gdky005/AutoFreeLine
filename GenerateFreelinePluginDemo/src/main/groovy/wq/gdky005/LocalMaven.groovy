package wq.gdky005

///**
// * Created by WangQing on 2017/9/22.
// */
//class LocalMaven {
//}

def proDir = "/Users/WangQing/Android_Pro/JuMeiYouPin_Pro/LocalMavenDemo"

def gradlePublishCMD = "gradle uploadArchives"
//def gradlePublishCMD = "gradle clean"
//def gradlePublishCMD = "gradle build"

System.out.println(System.getProperty("user.dir"))
println("项目目录是：" + proDir)

Process publish = gradlePublishCMD.execute(null, new File(proDir))
publish.waitFor()
println(publish.text)


//Process p=path.execute("ls ", new File(proDir))
//println "AAAA: ${p.text}"