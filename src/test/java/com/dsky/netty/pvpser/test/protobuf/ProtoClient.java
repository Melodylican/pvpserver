/**   
 * @文件名称: ProtoClient.java
 * @类路径: com.dsky.netty.pvpser.test.protobuf
 * @描述: TODO
 * @作者：chris.li(李灿)
 * @时间：2017年2月23日 下午7:11:57
 * @版本：V1.0   
 */
package com.dsky.netty.pvpser.test.protobuf;

import java.io.InputStream;
import java.net.Socket;

import com.dsky.netty.pvpser.test.protobuf.Msg.CMsg;
import com.dsky.netty.pvpser.test.protobuf.Msg.CMsgHead;
import com.dsky.netty.pvpser.test.protobuf.Msg.CMsgReg;

/**
 * @类功能说明：
 * @类修改者：
 * @修改日期：
 * @修改说明：
 * @公司名称：dsky
 * @作者：chris.li
 * @创建时间：2017年2月23日 下午7:11:57
 * @版本：V1.0
 */
public class ProtoClient {
	public static void main(String[] args) {
		ProtoClient pc = new ProtoClient();
		System.out.println("beign:");
		pc.runget();
	}

	public void runget() {
		Socket socket = null;
		try {
			// socket = new Socket("localhost", 12345);
			socket = new Socket("127.0.0.1", 12345);
			// head
			CMsgHead head = CMsgHead.newBuilder().setMsglen(5).setMsgtype(1)
					.setMsgseq(3).setTermversion(41).setMsgres(5)
					.setTermid("Client:head").build();

			// body
			CMsgReg body = CMsgReg.newBuilder().setArea(11).setRegion(22)
					.setShop(33).setRet(44).setTermid("Clent:body").build();

			// Msg
			CMsg msg = CMsg.newBuilder()
					.setMsghead(head.toByteString().toStringUtf8())
					.setMsgbody(body.toByteString().toStringUtf8()).build();

			// 向服务器发送信息
			System.out.println("sendMsg...");
			msg.writeTo(socket.getOutputStream());

			// 接受服务器的信息
			InputStream input = socket.getInputStream();

			System.out.println("recvMsg:");
			byte[] by = recvMsg(input);
			printMsg(CMsg.parseFrom(by));

			input.close();
			socket.close();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	public void printMsg(CMsg g) {

		try {
			CMsgHead h = CMsgHead.parseFrom(g.getMsghead().getBytes());
			StringBuffer sb = new StringBuffer();
			if (h.hasMsglen())
				sb.append("==msglen===" + h.getMsglen() + "\n");
			if (h.hasMsgres())
				sb.append("==msgres===" + h.getMsgres() + "\n");
			if (h.hasMsgseq())
				sb.append("==msgseq===" + h.getMsgseq() + "\n");
			if (h.hasMsgtype())
				sb.append("==msgtype===" + h.getMsgtype() + "\n");
			if (h.hasTermid())
				sb.append("==termid===" + h.getTermid() + "\n");
			if (h.hasTermversion())
				sb.append("==termversion===" + h.getTermversion() + "\n");

			CMsgReg bo = CMsgReg.parseFrom(g.getMsgbody().getBytes());
			if (bo.hasArea())
				sb.append("==area==" + bo.getArea() + "\n");
			if (bo.hasRegion())
				sb.append("==region==" + bo.getRegion() + "\n");
			if (bo.hasShop())
				sb.append("==shop==" + bo.getShop() + "\n");
			if (bo.hasRet())
				sb.append("==ret==" + bo.getRet() + "\n");
			if (bo.hasTermid())
				sb.append("==termid==" + bo.getTermid() + "\n");

			System.out.println(sb.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public byte[] recvMsg(InputStream inpustream) {
		byte[] temp = null;
		try {

			byte len[] = new byte[1024];
			int count = inpustream.read(len);

			temp = new byte[count];
			for (int i = 0; i < count; i++) {
				temp[i] = len[i];
			}
			return temp;
		} catch (Exception e) {
			System.out.println(e.toString());
			return temp;
		}
	}
}
