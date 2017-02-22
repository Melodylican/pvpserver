/**   
 * @文件名称: MapTypeHandler.java
 * @类路径: com.dsky.netty.pvpser.maptypehandler
 * @描述: TODO
 * @作者：chris.li(李灿)
 * @时间：2017年2月22日 上午10:07:57
 * @版本：V1.0   
 */
package com.dsky.netty.pvpser.maptypehandler;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import com.alibaba.fastjson.JSON;
import com.dsky.netty.pvpser.model.User;


/**
 * @类功能说明：
 * @类修改者：
 * @修改日期：
 * @修改说明：
 * @公司名称：dsky
 * @作者：chris.li
 * @创建时间：2017年2月22日 上午10:07:57
 * @版本：V1.0
 */
public class MapTypeHandler implements TypeHandler<Map<String, User>> {

    //private static JsonBinder binder = JsonBinder.buildNonDefaultBinder();

	
	public Map<String, User> getResult(ResultSet rs, String columnName)
			throws SQLException {
		String value = rs.getString(columnName);
		return (Map<String,User>)JSON.parseObject(value,Map.class);
	}


	public Map<String, User> getResult(ResultSet rs, int columnIndex)
			throws SQLException {
		String value = rs.getString(columnIndex);
		return (Map<String,User>)JSON.parseObject(value,Map.class);
	}


	public Map<String, User> getResult(CallableStatement cs, int parameterIndex)
			throws SQLException {
		String value = cs.getString(parameterIndex);
		return (Map<String,User>)JSON.parseObject(value,Map.class);
	}


	public void setParameter(java.sql.PreparedStatement ps, int i,
			Map<String, User> parameter, JdbcType jdbcType) throws SQLException {
		if(parameter == null) {
			ps.setNull(i, Types.VARCHAR);
		} else {
			ps.setString(i, JSON.toJSONString(parameter));
		}
	}
}