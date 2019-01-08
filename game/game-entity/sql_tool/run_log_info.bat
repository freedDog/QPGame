::数据库
set dburl=jdbc:mysql://127.0.0.1:3306/db_tgtgame_log
set user=root
set pwd=admin
:: 输出路径
set outpath=../../../trunk/workspace/Entity/src/com/tgt/uu/entity
::文件名格式, 参数表名.
set filename=%%sInfo.java
::模板文件
set vm=vms/LogInfo.java.vm
::模板参数
set args="[package=com.tgt.uu.entity, utils_package=com.tgt.uu.entity.utils]"

:: 执行
java -jar ./daomaker.jar -table=t_l_productlog -asname="ProductLog" -type=class -language=java -url=%dburl% -user=%user% -pwd=%pwd% -out=%outpath% -filename=%filename% -vm=%vm% -args=%args% -of=true
java -jar ./daomaker.jar -table=t_l_match -asname="MatchLog" -type=class -language=java -url=%dburl% -user=%user% -pwd=%pwd% -out=%outpath% -filename=%filename% -vm=%vm% -args=%args%	
java -jar ./daomaker.jar -table=t_l_revenue -asname="RevenueLog" -type=class -language=java -url=%dburl% -user=%user% -pwd=%pwd% -out=%outpath% -filename=%filename% -vm=%vm% -args=%args%	
java -jar ./daomaker.jar -table=t_l_vip -asname="VipLog" -type=class -language=java -url=%dburl% -user=%user% -pwd=%pwd% -out=%outpath% -filename=%filename% -vm=%vm% -args=%args%	
java -jar ./daomaker.jar -table=t_l_contest -asname="ContestLog" -type=class -language=java -url=%dburl% -user=%user% -pwd=%pwd% -out=%outpath% -filename=%filename% -vm=%vm% -args=%args%	
java -jar ./daomaker.jar -table=t_l_exchange -asname="ExchangeLog" -type=class -language=java -url=%dburl% -user=%user% -pwd=%pwd% -out=%outpath% -filename=%filename% -vm=%vm% -args=%args%	
java -jar ./daomaker.jar -table=t_l_goods -asname="GoodsLog" -type=class -language=java -url=%dburl% -user=%user% -pwd=%pwd% -out=%outpath% -filename=%filename% -vm=%vm% -args=%args%	
java -jar ./daomaker.jar -table=t_l_playermatch -asname="PlayerMatchLog" -type=class -language=java -url=%dburl% -user=%user% -pwd=%pwd% -out=%outpath% -filename=%filename% -vm=%vm% -args=%args%	
java -jar ./daomaker.jar -table=t_l_activity -asname="ActivityLog" -type=class -language=java -url=%dburl% -user=%user% -pwd=%pwd% -out=%outpath% -filename=%filename% -vm=%vm% -args=%args%	
java -jar ./daomaker.jar -table=t_l_room -asname="RoomLog" -type=class -language=java -url=%dburl% -user=%user% -pwd=%pwd% -out=%outpath% -filename=%filename% -vm=%vm% -args=%args%	
java -jar ./daomaker.jar -table=t_l_snatch -asname="SnatchLog" -type=class -language=java -url=%dburl% -user=%user% -pwd=%pwd% -out=%outpath% -filename=%filename% -vm=%vm% -args=%args%	
java -jar ./daomaker.jar -table=t_l_recharge -asname="RechargeLog" -type=class -language=java -url=%dburl% -user=%user% -pwd=%pwd% -out=%outpath% -filename=%filename% -vm=%vm% -args=%args%	
java -jar ./daomaker.jar -table=t_l_pay -asname="PayLog" -type=class -language=java -url=%dburl% -user=%user% -pwd=%pwd% -out=%outpath% -filename=%filename% -vm=%vm% -args=%args%	
java -jar ./daomaker.jar -table=t_l_proxy -asname="ProxyLog" -type=class -language=java -url=%dburl% -user=%user% -pwd=%pwd% -out=%outpath% -filename=%filename% -vm=%vm% -args=%args%

pause