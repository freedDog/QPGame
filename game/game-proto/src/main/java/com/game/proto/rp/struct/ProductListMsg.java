package com.game.proto.rp.struct;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.List;
import com.game.proto.rp.struct.ProductMsg;
import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;
public class ProductListMsg extends RpMessage {
	// 商品列表
	private List<ProductMsg> product = new ArrayList<ProductMsg>();

	public List<ProductMsg> getProduct() {
		return product;
	}
		
	public void addProduct(ProductMsg value) {
		this.product.add(value);
	}
		
	public void addAllProduct(List<ProductMsg> values) {
		this.product.addAll(values);
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putObjectArray(buffer, product);
	}

	public static ProductListMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static ProductListMsg deserialize(ByteBuffer buffer) {
		ProductListMsg messageInstance = new ProductListMsg();
		int productSize = ByteBufferHelper.getShort(buffer);
		for (int i = 0; i < productSize; i++) {
			messageInstance.addProduct(ProductMsg.deserialize(buffer));
		}
		return messageInstance;
	}

	@Override
	public int calcLength() {
		int length = 0;
		length += ByteBufferHelper.calcObjectArrayLength(product);
		return length;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("ProductListMsg[");
		sb.append("product=" + product + ", ");
		sb.append("]");
		return sb.toString();
	}
}