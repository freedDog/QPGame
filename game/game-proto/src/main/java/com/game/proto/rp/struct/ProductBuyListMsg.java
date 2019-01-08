package com.game.proto.rp.struct;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.List;
import com.game.proto.rp.struct.ProductMsg;
import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class ProductBuyListMsg extends RpMessage {
	// 商品列表
	private List<ProductMsg> product = new ArrayList<ProductMsg>();
	// vip商品列表
	private List<ProductMsg> vip = new ArrayList<ProductMsg>();
	// 活动奖励列表
	private List<ProductMsg> activity = new ArrayList<ProductMsg>();

	public List<ProductMsg> getProduct() {
		return product;
	}
		
	public void addProduct(ProductMsg value) {
		this.product.add(value);
	}
		
	public void addAllProduct(List<ProductMsg> values) {
		this.product.addAll(values);
	}

	public List<ProductMsg> getVip() {
		return vip;
	}
		
	public void addVip(ProductMsg value) {
		this.vip.add(value);
	}
		
	public void addAllVip(List<ProductMsg> values) {
		this.vip.addAll(values);
	}

	public List<ProductMsg> getActivity() {
		return activity;
	}
		
	public void addActivity(ProductMsg value) {
		this.activity.add(value);
	}
		
	public void addAllActivity(List<ProductMsg> values) {
		this.activity.addAll(values);
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putObjectArray(buffer, product);
		ByteBufferHelper.putObjectArray(buffer, vip);
		ByteBufferHelper.putObjectArray(buffer, activity);
	}

	public static ProductBuyListMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static ProductBuyListMsg deserialize(ByteBuffer buffer) {
		ProductBuyListMsg messageInstance = new ProductBuyListMsg();
		int productSize = ByteBufferHelper.getShort(buffer);
		for (int i = 0; i < productSize; i++) {
			messageInstance.addProduct(ProductMsg.deserialize(buffer));
		}
		int vipSize = ByteBufferHelper.getShort(buffer);
		for (int i = 0; i < vipSize; i++) {
			messageInstance.addVip(ProductMsg.deserialize(buffer));
		}
		int activitySize = ByteBufferHelper.getShort(buffer);
		for (int i = 0; i < activitySize; i++) {
			messageInstance.addActivity(ProductMsg.deserialize(buffer));
		}
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 0;
		length += ByteBufferHelper.calcObjectArrayLength(product);
		length += ByteBufferHelper.calcObjectArrayLength(vip);
		length += ByteBufferHelper.calcObjectArrayLength(activity);
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("ProductBuyListMsg[");
		sb.append("product=" + product + ", ");
		sb.append("vip=" + vip + ", ");
		sb.append("activity=" + activity + ", ");
		sb.append("]");
		return sb.toString();
	}
}