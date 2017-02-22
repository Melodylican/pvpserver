/**   
 * @文件名称: User.java
 * @类路径: com.dsky.netty.pvpser.model
 * @描述: TODO
 * @作者：chris.li(李灿)
 * @时间：2017年2月21日 下午3:00:28
 * @版本：V1.0   
 */
package com.dsky.netty.pvpser.model;

import java.io.Serializable;

/**
 * @类功能说明：记录房间中用户的信息
 * @类修改者：
 * @修改日期：
 * @修改说明：
 * @公司名称：dsky
 * @作者：chris.li
 * @创建时间：2017年2月21日 下午3:00:28
 * @版本：V1.0
 */
public class User implements Serializable{

	private static final long serialVersionUID = 1L;
	//用户Id
	private String userId;
	//用户当前状态
	private int userStatus;
	//用户当前最新的游戏数据
	private String data;
	public User(String userId,String data) {
		this.userId = userId;
		this.data = data;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public int getUserStatus() {
		return userStatus;
	}
	public void setUserStatus(int userStatus) {
		this.userStatus = userStatus;
	}
	@Override
	public String toString() {
		return "User [userId=" + userId + ", userStatus=" + userStatus
				+ ", data=" + data + "]";
	}
}
