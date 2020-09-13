package com.jack.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;

/**
 * @author cwl
 * @description: TODO
 * @date 2020/8/26 7:30
 */
@Service
public class InitService {

    private static final Logger log= LoggerFactory.getLogger(InitService.class);

    public static final int ThreadNum = 50000;

    private static int mobile=0;
    @Autowired
    private ConcurrencyService concurrencyService;
    @Autowired
    private CommonMqService commonMqService;

    public void generateMultiThread() {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        for (int i = 0; i < ThreadNum; i++) {
            RunThread runThread = new RunThread(countDownLatch);
            runThread.start();
        }
        countDownLatch.countDown();
    }

    public class RunThread extends Thread {
        private final CountDownLatch startLatch;

        public RunThread(CountDownLatch startLatch) {
            this.startLatch = startLatch;
        }

        @Override
        public void run() {
            try {
                startLatch.await();
                mobile = mobile + 1;
                // v1.0
                // concurrencyService.manageRobbing(String.valueOf(mobile));

                //v2.0
                commonMqService.sendRobbingMsg(String.valueOf(mobile));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
