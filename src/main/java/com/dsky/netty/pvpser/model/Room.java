/**   
 * @文件名称: Room.java
 * @类路径: com.dsky.netty.pvpser.model
 * @描述: TODO
 * @作者：chris.li(李灿)
 * @时间：2017年2月21日 下午2:44:29
 * @版本：V1.0   
 */
package com.dsky.netty.pvpser.model;

import java.io.Serializable;
import java.util.Map;

/**
 * @类功能说明：房间信息类
 * @类修改者：
 * @修改日期：
 * @修改说明：
 * @公司名称：dsky
 * @作者：chris.li
 * @创建时间：2017年2月21日 下午2:44:29
 * @版本：V1.0
 */
public class Room implements Serializable{

	private static final long serialVersionUID = 1L;
	//房间号
	private String roomId;
	//房间创建时间
	private int roomCreatetime;
	//房间销毁时间
	private int roomDeltime;
	//房间创建者
	private String roomOwner;
	//房间人数
	private int numbers;
	//房间当前人数
	private int currentNumber;
	//房间成员
	private Map<String,User> member;
	//房间状态
	private int roomStatus;
	
	public String getRoomId() {
		return roomId;
	}
	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}
	public void setRoomOwner(String roomOwner) {
		this.roomOwner = roomOwner;
	}
	public int getNumbers() {
		return numbers;
	}
	public void setNumbers(int numbers) {
		this.numbers = numbers;
	}
	public Map<String, User> getMember() {
		return member;
	}
	public void setMember(Map<String, User> member) {
		this.member = member;
	}
	public int getRoomStatus() {
		return roomStatus;
	}
	public void setRoomStatus(int roomStatus) {
		this.roomStatus = roomStatus;
	}
	public int getCurrentNumber() {
		return currentNumber;
	}
	public void setCurrentNumber(int currentNumber) {
		this.currentNumber = currentNumber;
	}
	public int getRoomCreatetime() {
		return roomCreatetime;
	}
	public void setRoomCreatetime(int roomCreatetime) {
		this.roomCreatetime = roomCreatetime;
	}
	public int getRoomDeltime() {
		return roomDeltime;
	}
	public void setRoomDeltime(int roomDeltime) {
		this.roomDeltime = roomDeltime;
	}
	public String getRoomOwner() {
		return roomOwner;
	}
	@Override
	public String toString() {
		return "Room [roomId=" + roomId + ", roomCreatetime=" + roomCreatetime
				+ ", roomDeltime=" + roomDeltime + ", roomOwner=" + roomOwner
				+ ", numbers=" + numbers + ", currentNumber=" + currentNumber
				+ ", member=" + member + ", roomStatus=" + roomStatus + "]";
	}
}
