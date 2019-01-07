package com.game.base.server.codec;

/**
 * 协议解析定义
 * CodecConstant.java
 * @author JiangBangMing
 * 2019年1月7日下午5:27:27
 */
public interface CodecConstant {
	/** 消息头数据 **/
	short HEAD = 0x0C03;

	/** 与客户端通信消息头长度，消息头数据[2]+codeId长度[2]+数据长度[4] **/
	int HEAD_SIZE = 8;

	/** 服务器内部消息头长度, 消息头数据[2]+playerId长度[8]+codeId长度[2]+数据长度[4] **/
	int INNER_HEAD_SIZE = 16;
}
