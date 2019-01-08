
::数据库
set dburl=jdbc:mysql://127.0.0.1:3306/db_jubafang_jbf_001
set user=root
set pwd=admin
:: 输出路径
set outpath=../../../trunk/workspace/Entity/src/com/tgt/uu/entity
::文件名格式, 参数表名.
set filename=%%sInfo.java
::模板文件
set vm=vms/Info.java.vm
::模板参数
set args="[package=com.tgt.uu.entity, utils_package=com.tgt.uu.entity.utils]"

:: 执行

java -jar ./daomaker.jar -table=t_u_item -type=class -language=java -url=%dburl% -user=%user% -pwd=%pwd% -out=%outpath% -filename=%filename% -vm=%vm% -args=%args% 

pause
