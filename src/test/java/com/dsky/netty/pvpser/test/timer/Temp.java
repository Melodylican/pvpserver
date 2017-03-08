/**   
 * @文件名称: Temp.java
 * @类路径: com.dksy.netty.pvpser.test.timer
 * @描述: TODO
 * @作者：chris.li(李灿)
 * @时间：2017年2月24日 上午10:29:25
 * @版本：V1.0   
 */
package com.dsky.netty.pvpser.test.timer;

import java.io.IOException;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;

/**
 * @类功能说明：
 * @类修改者：
 * @修改日期：
 * @修改说明：
 * @公司名称：dsky
 * @作者：chris.li
 * @创建时间：2017年2月24日 上午10:29:25
 * @版本：V1.0
 */
public class Temp {
	public static void main(String[] args) throws IOException {
		//创建Timer,精度为100毫秒
		HashedWheelTimer timer = new HashedWheelTimer();
		System.out.println(LocalTime.now());
        timer.newTimeout(new TimerTask() {
            public void run(Timeout timeout) throws Exception {
                System.out.println("timeout 5");
            }
        }, 5, TimeUnit.SECONDS);
        timer.newTimeout(new TimerTask() {
            public void run(Timeout timeout) throws Exception {
                System.out.println("timeout 10");
            }
        }, 10, TimeUnit.SECONDS);
    }

}
