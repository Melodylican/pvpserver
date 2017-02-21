/**   
 * @文件名称: SerializeUtil.java
 * @类路径: com.dsky.netty.pvpser.utils
 * @描述: TODO
 * @作者：chris.li(李灿)
 * @时间：2017年2月21日 下午4:03:45
 * @版本：V1.0   
 */
package com.dsky.netty.pvpser.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @类功能说明：
 * @类修改者：
 * @修改日期：
 * @修改说明：
 * @公司名称：dsky
 * @作者：chris.li
 * @创建时间：2017年2月21日 下午4:03:45
 * @版本：V1.0
 */
public class SerializeUtil {
	public static byte[] serialize(Object object) {
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;
		try {
			// 序列化
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			byte[] bytes = baos.toByteArray();
			return bytes;
		} catch (Exception e) {

		}
		return null;
	}

	public static Object unserialize(byte[] bytes) {
		ByteArrayInputStream bais = null;
		try {
			// 反序列化
			bais = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (Exception e) {

		}
		return null;
	}

}
