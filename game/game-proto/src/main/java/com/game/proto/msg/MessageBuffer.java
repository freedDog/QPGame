package com.game.proto.msg;

import java.util.ArrayList;

/**
 * 客户端消息数据对象<br>
 * 服务器下发客户端的数据结构对象, 这里实际上只是个消息数组.
 * MessageBuffer.java
 * @author JiangBangMing
 * 2019年1月4日下午2:59:50
 */
public class MessageBuffer extends ArrayList<Message>
{
	private static final long serialVersionUID = 1L;
}
