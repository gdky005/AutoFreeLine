/**
 * 获取黑名单列表
 * Created by WangQing on 16/9/16.
 * @return
 */
def blackList() {
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
println blackList().toString()