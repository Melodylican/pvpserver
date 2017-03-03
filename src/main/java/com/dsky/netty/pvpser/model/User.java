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
	//用户当前状态 [0 无状态,1 准备状态,2 游戏中, 3 游戏结束 , 4 退出房间状态, 5 掉线状态]
	private int userStatus; 
	//用户当前最新的游戏数据(指令等)
	private String gameData;
	//用户当前的状态(用户当前积分等)
	private String userData;
	public User(String userId, String gameData, String userData) {
		this.userId = userId;
		this.gameData = gameData;
		this.userData = userData;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public int getUserStatus() {
		return userStatus;
	}
	public void setUserStatus(int userStatus) {
		this.userStatus = userStatus;
	}
	public String getGameData() {
		return gameData;
	}
	public void setGameData(String gameData) {
		this.gameData = gameData;
	}
	public String getUserData() {
		return userData;
	}
	public void setUserData(String userData) {
		this.userData = userData;
	}
	@Override
	public String toString() {
		return "User [userId=" + userId + ", userStatus=" + userStatus
				+ ", gameData=" + gameData + ", userData=" + userData + "]";
	}

}
