
### 注册码

零售版：服务器端使用JointCodeGenerator.java生成8位注册码，参数为整型ID
工厂版：需要提取build.prop的hash，作为注册码。

### 防重复注册：

工厂版：使用和零售版不同的注册方法。无防重复注册机制。注册成功的，记录AndroidID和Mac。
零售版：每次注册，记录AndroidID和Mac。已注册码搜索数据库，将之前的注册的记录，更新为新的AndroidID和Mac。注册次数也更新。
Launcher每次启动，以及每隔n个小时，都验证注册。
