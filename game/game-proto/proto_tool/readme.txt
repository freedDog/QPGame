协议生成工具


调用
proto_create_server.bat	生成服务器协议文件
proto_create_client.bat	生成客户端协议文件





说明:
例如:java -jar proto_tool.jar ./proto ../../../trunk/workspace/GameProto/src com.tgt.uu.rp java
java -jar proto_tool.jar	为java程序启动路径
参数1	./proto	为协议所在目录
参数2	../../../trunk/workspace/GameProto/src为导出路径
参数3	com.tgt.uu.rp	为包参数, 用于传入模板使用
参数4	java	生成语言

vm下的2个文件分别为java语言和lua语言的生成模板, 基于java velocity模板引擎实现.




