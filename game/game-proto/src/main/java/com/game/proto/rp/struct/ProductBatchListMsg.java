package com.game.proto.rp.struct;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.List;
import com.game.proto.rp.struct.ProductListMsg;
import com.game.proto.msg.ByteBufferHelper;
import com.game.proto.msg.RpMessage;

public class ProductBatchListMsg extends RpMessage {
	// 列表信息
	private List<ProductListMsg> product = new ArrayList<ProductListMsg>();

	public List<ProductListMsg> getProduct() {
		return product;
	}
		
	public void addProduct(ProductListMsg value) {
		this.product.add(value);
	}
		
	public void addAllProduct(List<ProductListMsg> values) {
		this.product.addAll(values);
	}

	@Override
	public void serialize(ByteBuf buffer) {
		ByteBufferHelper.putObjectArray(buffer, product);
	}

	public static ProductBatchListMsg deserialize(byte[] array) {
		return deserialize(ByteBuffer.wrap(array));
	}
		
	public static ProductBatchListMsg deserialize(ByteBuffer buffer) {
		ProductBatchListMsg messageInstance = new ProductBatchListMsg();
		int productSize = ByteBufferHelper.getShort(buffer);
		for (int i = 0; i < productSize; i++) {
			messageInstance.addProduct(ProductListMsg.deserialize(buffer));
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
		sb.append("ProductBatchListMsg[");
		sb.append("product=" + product + ", ");
		sb.append("]");
		return sb.toString();
	}
}