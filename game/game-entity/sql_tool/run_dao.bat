
::数据库
set dburl=jdbc:mysql://127.0.0.1:3306/db_jubafang_jbf_001
set user=root
set pwd=admin
:: 输出路径
set outpath=../../../trunk/workspace/Entity/src/com/tgt/uu/dao
::文件名格式, 参数表名.
set filename=%%sDAO.java
::模板文件
set vm=vms/Dao.java.vm
::模板参数
set args="[package=com.tgt.uu.dao, entity_package=com.tgt.uu.entity, utils_package=com.tgt.uu.entity.utils, get_key_name=PlayerId]"
set argsg="[package=com.tgt.uu.dao, entity_package=com.tgt.uu.entity, utils_package=com.tgt.uu.entity.utils, get_key_name=GuildId]"

:: 执行, -table为表名, 如果有特殊需求, 更换模板即可.
java -jar ./daomaker.jar -table=t_u_user -type=class -language=java -url=%dburl% -user=%user% -pwd=%pwd% -out=%outpath% -filename=%filename% -vm=%vm% -args=%args% -of=true
java -jar ./daomaker.jar -table=t_u_player -type=class -language=java -url=%dburl% -user=%user% -pwd=%pwd% -out=%outpath% -filename=%filename% -vm=%vm% -args=%args%
java -jar ./daomaker.jar -table=t_u_playerextend -asname="PlayerExtend" -type=class -language=java -url=%dburl% -user=%user% -pwd=%pwd% -out=%outpath% -filename=%filename% -vm=%vm% -args=%args%

java -jar ./daomaker.jar -table=t_u_proxy -asname="Proxy" -type=class -language=java -url=%dburl% -user=%user% -pwd=%pwd% -out=%outpath% -filename=%filename% -vm=%vm% -args=%args%

java -jar ./daomaker.jar -table=t_u_item  -vm=vms/ListDao.java.vm -type=class -language=java -url=%dburl% -user=%user% -pwd=%pwd% -out=%outpath% -filename=%filename% -args=%args%
java -jar ./daomaker.jar -table=t_u_fashion  -vm=vms/ListDao.java.vm -type=class -language=java -url=%dburl% -user=%user% -pwd=%pwd% -out=%outpath% -filename=%filename% -args=%args%

java -jar ./daomaker.jar -table=t_u_mail -vm=vms/ListDao.java.vm -type=class -language=java -url=%dburl% -user=%user% -pwd=%pwd% -out=%outpath% -filename=%filename% -args=%args%

java -jar ./daomaker.jar -table=t_u_exploit -vm=vms/ListDao.java.vm -type=class -language=java -url=%dburl% -user=%user% -pwd=%pwd% -out=%outpath% -filename=%filename% -args=%args%

java -jar ./daomaker.jar -table=t_u_sign -vm=vms/Dao.java.vm -type=class -language=java -url=%dburl% -user=%user% -pwd=%pwd% -out=%outpath% -filename=%filename% -args=%args%

java -jar ./daomaker.jar -table=t_u_task -vm=vms/ListDao.java.vm -type=class -language=java -url=%dburl% -user=%user% -pwd=%pwd% -out=%outpath% -filename=%filename% -args=%args%

java -jar ./daomaker.jar -table=t_u_buff -vm=vms/ListDao.java.vm -type=class -language=java -url=%dburl% -user=%user% -pwd=%pwd% -out=%outpath% -filename=%filename% -args=%args%

java -jar ./daomaker.jar -table=t_u_rankdata -asname="RankData" -vm=vms/Dao.java.vm -type=class -language=java -url=%dburl% -user=%user% -pwd=%pwd% -out=%outpath% -filename=%filename% -args=%args%
java -jar ./daomaker.jar -table=t_s_snatch -asname="SnatchSData" -vm=vms/RunTempDao.java.vm  -filename=%%sTempDAO.java  -type=class -language=java -url=%dburl% -user=%user% -pwd=%pwd% -out=%outpath% -args=%args%

java -jar ./daomaker.jar -table=t_p_snatch -vm=vms/RunDao.java.vm  -filename=%%sDAO.java  -type=class -language=java -url=%dburl% -user=%user% -pwd=%pwd% -out=%outpath% -args=%args%
java -jar ./daomaker.jar -table=t_p_rank -vm=vms/RunDao.java.vm  -filename=%%sDAO.java  -type=class -language=java -url=%dburl% -user=%user% -pwd=%pwd% -out=%outpath% -args=%args%
java -jar ./daomaker.jar -table=t_u_feedback  -vm=vms/ListDao.java.vm  -filename=%%sDAO.java  -type=class -language=java -url=%dburl% -user=%user% -pwd=%pwd% -out=%outpath% -args=%args%

java -jar ./daomaker.jar -table=t_u_goods -vm=vms/ListDao.java.vm -type=class -language=java -url=%dburl% -user=%user% -pwd=%pwd% -out=%outpath% -filename=%filename% -args=%args%

java -jar ./daomaker.jar -table=t_s_contest -vm=vms/RunTempDao.java.vm  -filename=%%sTempDAO.java  -type=class -language=java -url=%dburl% -user=%user% -pwd=%pwd% -out=%outpath% -args=%args%
java -jar ./daomaker.jar -table=t_p_contest -vm=vms/RunDao.java.vm  -filename=%%sDAO.java  -type=class -language=java -url=%dburl% -user=%user% -pwd=%pwd% -out=%outpath% -args=%args%
java -jar ./daomaker.jar -table=t_u_contestdata -asname="ContestData" -vm=vms/ListDao.java.vm  -filename=%%sDAO.java  -type=class -language=java -url=%dburl% -user=%user% -pwd=%pwd% -out=%outpath% -args=%args%

java -jar ./daomaker.jar -table=t_s_goods   -vm=vms/RunTempDao.java.vm -type=class -language=java -url=%dburl% -user=%user% -pwd=%pwd% -out=%outpath% -filename=%%sTempDAO.java -args=%args%

java -jar ./daomaker.jar -table=t_s_globalconfig -asname="GlobalConfig" -vm=vms/RunTempDao.java.vm  -filename=%%sTempDAO.java  -type=class -language=java -url=%dburl% -user=%user% -pwd=%pwd% -out=%outpath% -args=%args%

java -jar ./daomaker.jar -table=t_s_activity -asname="Activity" -vm=vms/RunTempDao.java.vm  -filename=%%sTempDAO.java  -type=class -language=java -url=%dburl% -user=%user% -pwd=%pwd% -out=%outpath% -args=%args%

java -jar ./daomaker.jar -table=t_p_gamevideo -asname="GameVideo" -vm=vms/RunDao.java.vm  -filename=%%sDAO.java  -type=class -language=java -url=%dburl% -user=%user% -pwd=%pwd% -out=%outpath% -args=%args%
java -jar ./daomaker.jar -table=t_u_gamerecord -asname="GameRecord" -vm=vms/ListDao.java.vm  -filename=%%sDAO.java  -type=class -language=java -url=%dburl% -user=%user% -pwd=%pwd% -out=%outpath% -args=%args%


pause
