/**
 * 读取 properties 文件内容
 * Created by WangQing on 16/9/16.
 */

/** 默认情况下是 false, 当打成 jar 之后就设置为 true */
def isRunJar = false
def path = System.getProperty("user.dir")

def myConfigFile = "my.properties"
def excludeDirFile = "exclude_dir.properties"
def excludeRegexpFile = "exclude_regexp_file_name.properties"

/* 配置文件 */
def config = excludeRegexpFile

println "当前运行文件的路径是：" + path
println ""
println ""
println "输出结果是："

//InputStream inputStream;
//if (isRunJar) {
//    String proFilePath = path + "/" + configFile;
//    inputStream = new BufferedInputStream(new FileInputStream(proFilePath));
//} else {
//    inputStream = getClass().getResourceAsStream(configFile)
//}
//
//ResourceBundle property = new PropertyResourceBundle(inputStream);


def propertyMap = { String configFile ->
    InputStream inputStream;
    if (isRunJar) {
        String proFilePath = path + "/" + configFile;
        inputStream = new BufferedInputStream(new FileInputStream(proFilePath));
    } else {
        inputStream = getClass().getResourceAsStream(configFile)
    }

    ResourceBundle property = new PropertyResourceBundle(inputStream);
    property
}

ResourceBundle resourceBundle = propertyMap(config)
Set set = resourceBundle.keySet()

List list = new ArrayList();
list.addAll(set)

for(String s : list){
    println s
}


//Enumeration keys = resourceBundle.keys
//List list = keys.toList();
//for (int i = 0; i < list.size(); i++) {
//
//}
//
//println "hello"

//String name = resourceBundle.getString("age");
//println name