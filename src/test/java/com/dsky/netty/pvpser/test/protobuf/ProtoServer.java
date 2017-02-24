/**   
 * @文件名称: ProtoServer.java
 * @类路径: com.dsky.netty.pvpser.test.protobuf
 * @描述: TODO
 * @作者：chris.li(李灿)
 * @时间：2017年2月23日 下午7:09:20
 * @版本：V1.0   
 */
package com.dsky.netty.pvpser.test.protobuf;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
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
 * @创建时间：2017年2月23日 下午7:09:20
 * @版本：V1.0
 */
public class ProtoServer implements Runnable {  
  
    public void run() {  
        try {  
            System.out.println("beign:");  
            ServerSocket serverSocket = new ServerSocket(12345);  
            while (true) {  
                System.out.println("等待接收用户连接：");  
                // 接受客户端请求  
                Socket client = serverSocket.accept();  
  
                DataOutputStream dataOutputStream;  
                DataInputStream dataInputStream;  
  
                try {  
                    InputStream inputstream = client.getInputStream();  
                    dataOutputStream = new DataOutputStream(  
                            client.getOutputStream());  
  
                    byte len[] = new byte[1024];  
                    int count = inputstream.read(len);  
                    byte[] temp = new byte[count];  
                    for (int i = 0; i < count; i++) {  
                        temp[i] = len[i];  
                    }  
  
                    CMsg msg = CMsg.parseFrom(temp);  
  
                    CMsgHead head = CMsgHead.parseFrom(msg.getMsghead()  
                            .getBytes());  
                    System.out.println("==len===" + head.getMsglen());  
                    System.out.println("==res===" + head.getMsgres());  
                    System.out.println("==seq===" + head.getMsgseq());  
                    System.out.println("==type===" + head.getMsgtype());  
                    System.out.println("==Termid===" + head.getTermid());  
                    System.out.println("==Termversion==="  
                            + head.getTermversion());  
  
                    CMsgReg body = CMsgReg.parseFrom(msg.getMsgbody()  
                            .getBytes());  
                    System.out.println("==area==" + body.getArea());  
                    System.out.println("==Region==" + body.getRegion());  
                    System.out.println("==shop==" + body.getShop());  
  
                    sendProtoBufBack(dataOutputStream);  
                    inputstream.close();  
  
                } catch (Exception ex) {  
                    System.out.println(ex.getMessage());  
                    ex.printStackTrace();  
                } finally {  
                    client.close();  
                    System.out.println("close");  
                }  
            }  
              
        } catch (IOException e) {  
            System.out.println(e.getMessage());  
        }  
    }  
  
    private byte[] getProtoBufBack() {  
  
        // head  
        CMsgHead head = CMsgHead.newBuilder().setMsglen(10).setMsgtype(21)  
                .setMsgseq(32).setTermversion(43).setMsgres(54)  
                .setTermid("Server:head").build();  
  
        // body  
        CMsgReg body = CMsgReg.newBuilder().setArea(11).setRegion(22)  
                .setShop(33).setRet(44).setTermid("Server:body").build();  
  
        // Msg  
        CMsg msg = CMsg.newBuilder()  
                .setMsghead(head.toByteString().toStringUtf8())  
                .setMsgbody(body.toByteString().toStringUtf8()).build();  
  
        return msg.toByteArray();  
    }  
  
    private void sendProtoBufBack(DataOutputStream dataOutputStream) {  
  
        byte[] backBytes = getProtoBufBack();  
          
        // Integer len2 = backBytes.length;  
          
        // byte[] cmdHead2 = BytesUtil.IntToBytes4(len2);  
  
        try {  
            // dataOutputStream.write(cmdHead2, 0, cmdHead2.length);  
            dataOutputStream.write(backBytes, 0, backBytes.length);  
            dataOutputStream.flush();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
  
    public static void main(String[] args) {  
        Thread desktopServerThread = new Thread(new ProtoServer());  
        desktopServerThread.start();  
    }  
  
}