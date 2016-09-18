# AutoFreeLine 自动集成指南

		做这个脚本插件的原因是: 每次在新项目里面集成 freeline 的一些代码。至少要修改五个地方才能运行。而且由于项目是 基于 HG 操作的，有很多的分支，每次切换分支后都必须重新初始化这些东西，因此同学们建议我弄个脚本自动完成这些东西。所以有了这个插件。
		
### 这个可以做到什么样的操作呢？
使用这个后，可以达到如下效果：

- 不需要手动在项目根目录下的 build.gradle 文件中插入
		
		 `classpath 'com.antfortune.freeline:gradle:0.5.5'`



- 不需要手动在项目的 主modle目录 下的 build.gradle 文件中插入 
	
		`apply plugin: 'com.antfortune.freeline'`
		
		android {
		
		freeline {
        	hack true
    	}
    	
        }
    
- 不需要再主的 Application 的 onCreate 里面插入

		FreelineCore.init(this);
		
	
		

### 使用方式
请将里面的文件放在同一个目录下面

1. 如果当前命令行直接在你的目录的下面请直接使用： `sh jar.sh`
2. 如果你当前命令行在其他位置（没有在你们的目录下）请使用：`sh XX/xxx/xx/jar.sh`

### 注意事项

1. 请注意 自定义的 Application 的命名里面必须 包含 Application，否则将无法成功，可以修改下即可。例如你的自定义Application 的名字是： ABCApplication.java / WuLiApplication.java / WoApplicationTest.java 等等

### 使用技巧

1. 如果不能运行成功，但是实在看不出问题的话，可以在 jar.sh 文件的目录下 直接新建文件 命名为 debug.properties ，里面直接写一个 true 即可。
2. 如果不想把这个东西放到项目根目录下，那么可以 新建 your_pro_path.properties 文件，里面写上你的项目绝对路径。
3. 如果项目比较大，这个工具运行时间比较长，可以 新建 exclude_dir.properties 文件，里面写上某些目录或者文件不需要查找，直接过滤即可。默认加了很多的过滤规则，都是很标准的。可以加 lib 或 其他 非主 model, 速度会更快。


