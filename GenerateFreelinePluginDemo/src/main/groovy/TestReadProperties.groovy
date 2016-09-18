/**
 * Created by WangQing on 16/9/16.
 */
println "开始获取配置文件"


/** 默认情况下是 false, 当打成 jar 之后就设置为 true */
def isRunJar = true
def path = System.getProperty("user.dir")

def configFile = "my.properties"
InputStream inputStream;

println "当前运行文件的路径是：" + path
println ""
println ""
println "输出结果是："

if (isRunJar) {
    String proFilePath = System.getProperty("user.dir") + "/" + configFile;
    inputStream = new BufferedInputStream(new FileInputStream(proFilePath));
} else {
    inputStream = getClass().getResourceAsStream(configFile)
}

ResourceBundle property = new PropertyResourceBundle(inputStream);
String name = property.getString("name");
println name