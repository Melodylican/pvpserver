/**   
 * @文件名称: CountDown1.java
 * @类路径: com.dksy.netty.pvpser.test.timer
 * @描述: TODO
 * @作者：chris.li(李灿)
 * @时间：2017年3月1日 下午2:27:15
 * @版本：V1.0   
 */
package com.dksy.netty.pvpser.test.timer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @类功能说明：
 * @类修改者：
 * @修改日期：
 * @修改说明：
 * @公司名称：dsky
 * @作者：chris.li
 * @创建时间：2017年3月1日 下午2:27:15
 * @版本：V1.0
 */
public class CountDown1 {  
    private volatile int limitSec ; //记录倒计时时间  
    private int curSec;   //记录倒计时当下时间  
    public CountDown1(int limitSec) throws InterruptedException{  
        this.limitSec = limitSec;
        this.curSec = limitSec;
        System.out.println("count down form "+limitSec);  
        
        ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);  
        exec.scheduleAtFixedRate(new Task(),0,1,TimeUnit.SECONDS);  
        TimeUnit.SECONDS.sleep(limitSec);   //暂停本线程  
        exec.shutdownNow();  
        System.out.println("Time out！");  
    }  
    private class Task implements Runnable{  
        public void run(){  
            System.out.println("Time remains "+ --curSec +" s");  
        }  
    }  
    //Test  
   public static void main(String[] args) throws InterruptedException{ 
        new CountDown1(10); 
    }  
      
  
}  
