/*
 * Copyright 2011 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.traffic; 

import java.util.concurrent.Executor; 
import java.util.concurrent.atomic.AtomicBoolean; 

import io.netty.buffer.ChannelBuffer; 

import io.netty.channel.Channel; 
import io.netty.channel.ChannelEvent; 
import io.netty.channel.ChannelHandlerContext; 
import io.netty.channel.ChannelState; 
import io.netty.channel.ChannelStateEvent; 
import io.netty.channel.MessageEvent; 
import io.netty.channel.SimpleChannelHandler; 
import io.netty.logging.InternalLogger; 
import io.netty.logging.InternalLoggerFactory; 
import io.netty.util.ExternalResourceReleasable; 
import io.netty.util.internal.ExecutorUtil; 

import java.util.concurrent.TimeUnit; 
import io.netty.handler.execution.ObjectSizeEstimator; 
import io.netty.handler.execution.DefaultObjectSizeEstimator; 
import io.netty.util.Timeout; 
import io.netty.util.Timer; 
import io.netty.util.TimerTask; 

/**
 * AbstractTrafficShapingHandler allows to limit the global bandwidth
 * (see {@link GlobalTrafficShapingHandler}) or per session
 * bandwidth (see {@link ChannelTrafficShapingHandler}), as traffic shaping.
 * It allows too to implement an almost real time monitoring of the bandwidth using
 * the monitors from {@link TrafficCounter} that will call back every checkInterval
 * the method doAccounting of this handler.<br>
 * <br>
 *
 * An {@link ObjectSizeEstimator} can be passed at construction to specify what
 * is the size of the object to be read or write accordingly to the type of
 * object. If not specified, it will used the {@link DefaultObjectSizeEstimator} implementation.<br><br>
 *
 * If you want for any particular reasons to stop the monitoring (accounting) or to change
 * the read/write limit or the check interval, several methods allow that for you:<br>
 * <ul>
 * <li><tt>configure</tt> allows you to change read or write limits, or the checkInterval</li>
 * <li><tt>getTrafficCounter</tt> allows you to have access to the TrafficCounter and so to stop
 * or start the monitoring, to change the checkInterval directly, or to have access to its values.</li>
 * <li></li>
 * </ul>
 */
  class  AbstractTrafficShapingHandler  extends
        SimpleChannelHandler   {
	
    /**
     * Internal logger
     */
    

	

    /**
     * Default delay between two checks: 1s
     */
    

	

    /**
     * Default minimal time to wait
     */
    

	

    /**
     * Traffic Counter
     */
    

	

    /**
     * Executor to associated to any TrafficCounter
     */
    protected Executor executor;

	
    
    /**
     * Limit in B/s to apply to write
     */
    

	

    /**
     * Limit in B/s to apply to read
     */
    

	

    /**
     * Delay between two performance snapshots
     */
    

	 // default 1 s

    /**
     * Boolean associated with the release of this TrafficShapingHandler.
     * It will be true only once when the releaseExternalRessources is called
     * to prevent waiting when shutdown.
     */
    

	

    private void init(
            Executor newExecutor, long newWriteLimit, long newReadLimit, long newCheckInterval) {
        executor = newExecutor;
        writeLimit = newWriteLimit;
        readLimit = newReadLimit;
        checkInterval = newCheckInterval;
        //logger.info("TSH: "+writeLimit+":"+readLimit+":"+checkInterval+":"+isPerChannel());
    }

	

    /**
     *
     * @param newTrafficCounter the TrafficCounter to set
     */
    

	

    /**
     * @param executor
     *          created for instance like Executors.newCachedThreadPool
     * @param writeLimit
     *          0 or a limit in bytes/s
     * @param readLimit
     *          0 or a limit in bytes/s
     * @param checkInterval
     *          The delay between two computations of performances for
     *            channels or 0 if no stats are to be computed
     */
    public AbstractTrafficShapingHandler(Executor executor, long writeLimit,
            long readLimit, long checkInterval) {
        init(executor, writeLimit, readLimit, checkInterval);
    }

	

    /**
     * @param executor
     *          created for instance like Executors.newCachedThreadPool
     * @param writeLimit
     *          0 or a limit in bytes/s
     * @param readLimit
     *          0 or a limit in bytes/s
     */
    public AbstractTrafficShapingHandler(Executor executor, long writeLimit,
            long readLimit) {
        init(executor, writeLimit, readLimit, DEFAULT_CHECK_INTERVAL);
    }

	

    /**
     * Change the underlying limitations and check interval.
     *
     * @param newWriteLimit
     * @param newReadLimit
     * @param newCheckInterval
     */
    

	

    /**
     * Change the underlying limitations.
     *
     * @param newWriteLimit
     * @param newReadLimit
     */
    

	

    /**
     * Change the check interval.
     *
     * @param newCheckInterval
     */
    

	

    /**
     * Called each time the accounting is computed from the TrafficCounters.
     * This method could be used for instance to implement almost real time accounting.
     *
     * @param counter
     *            the TrafficCounter that computes its performance
     */
    

	

    /**
     * Class to implement setReadable at fix time
 */
    private  class  ReopenRead  implements Runnable {
		
        /**
         * Associated ChannelHandlerContext
         */
        private ChannelHandlerContext ctx;

		

        /**
         * Time to wait before clearing the channel
         */
        private long timeToWait;

		

        /**
         * @param ctx
         *            the associated channelHandlerContext
         * @param timeToWait
         */
        protected ReopenRead(ChannelHandlerContext ctx, long timeToWait) {
            this.ctx = ctx;
            this.timeToWait = timeToWait;
        }

		

        /**
         * Truly run the waken up of the channel
         */
        @Override
        public void run() {
            try {
                if (release.get()) {
                    return;
                }
                Thread.sleep(timeToWait);
            } catch (InterruptedException e) {
                // interruption so exit
                return;
            }
            // logger.info("WAKEUP!");
            if (ctx != null && ctx.getChannel() != null &&
                    ctx.getChannel().isConnected()) {
                //logger.info(" setReadable TRUE: "+timeToWait);
                // readSuspended = false;
                ctx.setAttachment(null);
                ctx.getChannel().setReadable(true);
            }
        }


	}

	

    /**
    *
    * @return the time that should be necessary to wait to respect limit. Can
    *         be negative time
    */
    <<<<<<< /mnt/Vbox/FSTMerge/binary/fstmerge_tmp1390772194998/fstmerge_var1_6902130876597131031
private long getTimeToWait(long limit, long bytes, long lastTime,
            long curtime) {
        long interval = curtime - lastTime;
        if (interval == 0) {
            // Time is too short, so just lets continue
            return 0;
        }
        return ((bytes * 1000 / limit - interval) / 10) * 10;
    }
=======
>>>>>>> /mnt/Vbox/FSTMerge/binary/fstmerge_tmp1390772194998/fstmerge_var2_7380816970693851783


	

    <<<<<<< /mnt/Vbox/FSTMerge/binary/fstmerge_tmp1390772195051/fstmerge_var1_3029288421804269347
@Override
    public void messageReceived(ChannelHandlerContext arg0, MessageEvent arg1)
            throws Exception {
        try {
            long curtime = System.currentTimeMillis();
            long size = objectSizeEstimator.estimateSize(arg1.getMessage());
            if (trafficCounter != null) {
                trafficCounter.bytesRecvFlowControl(arg0, size);
                if (readLimit == 0) {
                    // no action
                    return;
                }
                // compute the number of ms to wait before reopening the channel
                long wait = getTimeToWait(readLimit,
                        trafficCounter.getCurrentReadBytes(),
                        trafficCounter.getLastTime(), curtime);
                if (wait >= MINIMAL_WAIT) { // At least 10ms seems a minimal
                                            // time in order to
                    Channel channel = arg0.getChannel();
                    // try to limit the traffic
                    if (channel != null && channel.isConnected()) {
                        // Channel version
                        if (timer == null) {
                            // Sleep since no executor
                            // logger.warn("Read sleep since no timer for "+wait+" ms for "+this);
                            if (release.get()) {
                                return;
                            }
                            Thread.sleep(wait);
                            return;
                        }
                        if (arg0.getAttachment() == null) {
                            // readSuspended = true;
                            arg0.setAttachment(Boolean.TRUE);
                            channel.setReadable(false);
                            // logger.warn("Read will wakeup after "+wait+" ms "+this);
                            TimerTask timerTask = new ReopenReadTimerTask(arg0);
                            timeout = timer.newTimeout(timerTask, wait,
                                    TimeUnit.MILLISECONDS);
                        } else {
                            // should be waiting: but can occurs sometime so as
                            // a FIX
                            // logger.warn("Read sleep ok but should not be here: "+wait+" "+this);
                            if (release.get()) {
                                return;
                            }
                            Thread.sleep(wait);
                        }
                    } else {
                        // Not connected or no channel
                        // logger.warn("Read sleep "+wait+" ms for "+this);
                        if (release.get()) {
                            return;
                        }
                        Thread.sleep(wait);
                    }
                }
            }
        } finally {
            // The message is then just passed to the next handler
            super.messageReceived(arg0, arg1);
        }
    }
=======
>>>>>>> /mnt/Vbox/FSTMerge/binary/fstmerge_tmp1390772195051/fstmerge_var2_291629672556568452


	

    <<<<<<< /mnt/Vbox/FSTMerge/binary/fstmerge_tmp1390772195103/fstmerge_var1_2785197979210406784
@Override
    public void writeRequested(ChannelHandlerContext arg0, MessageEvent arg1)
            throws Exception {
        try {
            long curtime = System.currentTimeMillis();
            long size = objectSizeEstimator.estimateSize(arg1.getMessage());
            if (trafficCounter != null) {
                trafficCounter.bytesWriteFlowControl(size);
                if (writeLimit == 0) {
                    return;
                }
                // compute the number of ms to wait before continue with the
                // channel
                long wait = getTimeToWait(writeLimit,
                        trafficCounter.getCurrentWrittenBytes(),
                        trafficCounter.getLastTime(), curtime);
                if (wait >= MINIMAL_WAIT) {
                    // Global or Channel
                    if (release.get()) {
                        return;
                    }
                    Thread.sleep(wait);
                }
            }
        } finally {
            // The message is then just passed to the next handler
            super.writeRequested(arg0, arg1);
        }
    }
=======
>>>>>>> /mnt/Vbox/FSTMerge/binary/fstmerge_tmp1390772195103/fstmerge_var2_7297875183473233172


	
    

	

    /**
     *
     * @return the current TrafficCounter (if
     *         channel is still connected)
     */
    

	

    <<<<<<< /mnt/Vbox/FSTMerge/binary/fstmerge_tmp1390772195259/fstmerge_var1_2697662516449779971
public void releaseExternalResources() {
        if (trafficCounter != null) {
            trafficCounter.stop();
        }
        release.set(true);
        if (timeout != null) {
            timeout.cancel();
        }
    }
=======
>>>>>>> /mnt/Vbox/FSTMerge/binary/fstmerge_tmp1390772195259/fstmerge_var2_5657246203609600045


	

    

	

    /**
     * ObjectSizeEstimator
     */
    private ObjectSizeEstimator objectSizeEstimator;

	

    /**
     * Timer to associated to any TrafficCounter
     */
    protected Timer timer;

	
    /**
     * used in releaseExternalResources() to cancel the timer
     */
    private volatile Timeout timeout;

	

     private void init(ObjectSizeEstimator newObjectSizeEstimator,
             Timer newTimer, long newWriteLimit, long newReadLimit,
             long newCheckInterval) {
         objectSizeEstimator = newObjectSizeEstimator;
         timer = newTimer;
         writeLimit = newWriteLimit;
         readLimit = newReadLimit;
         checkInterval = newCheckInterval;
         //logger.warn("TSH: "+writeLimit+":"+readLimit+":"+checkInterval);
     }

	

    /**
     * Constructor using default {@link ObjectSizeEstimator}
     *
     * @param timer
     *          created once for instance like HashedWheelTimer(10, TimeUnit.MILLISECONDS, 1024)
     * @param writeLimit
     *          0 or a limit in bytes/s
     * @param readLimit
     *          0 or a limit in bytes/s
     * @param checkInterval
     *          The delay between two computations of performances for
     *            channels or 0 if no stats are to be computed
     */
    public AbstractTrafficShapingHandler(Timer timer, long writeLimit,
            long readLimit, long checkInterval) {
        init(new DefaultObjectSizeEstimator(), timer, writeLimit, readLimit, checkInterval);
    }

	

    /**
     * Constructor using the specified ObjectSizeEstimator
     *
     * @param objectSizeEstimator
     *            the {@link ObjectSizeEstimator} that will be used to compute
     *            the size of the message
     * @param timer
     *          created once for instance like HashedWheelTimer(10, TimeUnit.MILLISECONDS, 1024)
     * @param writeLimit
     *          0 or a limit in bytes/s
     * @param readLimit
     *          0 or a limit in bytes/s
     * @param checkInterval
     *          The delay between two computations of performances for
     *            channels or 0 if no stats are to be computed
     */
    public AbstractTrafficShapingHandler(
            ObjectSizeEstimator objectSizeEstimator, Timer timer,
            long writeLimit, long readLimit, long checkInterval) {
        init(objectSizeEstimator, timer, writeLimit, readLimit, checkInterval);
    }

	

    /**
     * Constructor using default {@link ObjectSizeEstimator} and using default Check Interval
     *
     * @param timer
     *          created once for instance like HashedWheelTimer(10, TimeUnit.MILLISECONDS, 1024)
     * @param writeLimit
     *          0 or a limit in bytes/s
     * @param readLimit
     *          0 or a limit in bytes/s
     */
    public AbstractTrafficShapingHandler(Timer timer, long writeLimit,
            long readLimit) {
        init(new DefaultObjectSizeEstimator(), timer, writeLimit, readLimit, DEFAULT_CHECK_INTERVAL);
    }

	

    /**
     * Constructor using the specified ObjectSizeEstimator and using default Check Interval
     *
     * @param objectSizeEstimator
     *            the {@link ObjectSizeEstimator} that will be used to compute
     *            the size of the message
     * @param timer
     *          created once for instance like HashedWheelTimer(10, TimeUnit.MILLISECONDS, 1024)
     * @param writeLimit
     *          0 or a limit in bytes/s
     * @param readLimit
     *          0 or a limit in bytes/s
     */
    public AbstractTrafficShapingHandler(
            ObjectSizeEstimator objectSizeEstimator, Timer timer,
            long writeLimit, long readLimit) {
        init(objectSizeEstimator, timer, writeLimit, readLimit, DEFAULT_CHECK_INTERVAL);
    }

	

    /**
     * Constructor using default {@link ObjectSizeEstimator} and using NO LIMIT and default Check Interval
     *
     * @param timer
     *          created once for instance like HashedWheelTimer(10, TimeUnit.MILLISECONDS, 1024)
     */
    public AbstractTrafficShapingHandler(Timer timer) {
        init(new DefaultObjectSizeEstimator(), timer, 0, 0, DEFAULT_CHECK_INTERVAL);
    }

	

    /**
     * Constructor using the specified ObjectSizeEstimator and using NO LIMIT and default Check Interval
     *
     * @param objectSizeEstimator
     *            the {@link ObjectSizeEstimator} that will be used to compute
     *            the size of the message
     * @param timer
     *          created once for instance like HashedWheelTimer(10, TimeUnit.MILLISECONDS, 1024)
     */
    public AbstractTrafficShapingHandler(
            ObjectSizeEstimator objectSizeEstimator, Timer timer) {
        init(objectSizeEstimator, timer, 0, 0, DEFAULT_CHECK_INTERVAL);
    }

	

    /**
     * Constructor using default {@link ObjectSizeEstimator} and using NO LIMIT
     *
     * @param timer
     *          created once for instance like HashedWheelTimer(10, TimeUnit.MILLISECONDS, 1024)
     * @param checkInterval
     *          The delay between two computations of performances for
     *            channels or 0 if no stats are to be computed
     */
    public AbstractTrafficShapingHandler(Timer timer, long checkInterval) {
        init(new DefaultObjectSizeEstimator(), timer, 0, 0, checkInterval);
    }

	

    /**
     * Constructor using the specified ObjectSizeEstimator and using NO LIMIT
     *
     * @param objectSizeEstimator
     *            the {@link ObjectSizeEstimator} that will be used to compute
     *            the size of the message
     * @param timer
     *          created once for instance like HashedWheelTimer(10, TimeUnit.MILLISECONDS, 1024)
     * @param checkInterval
     *          The delay between two computations of performances for
     *            channels or 0 if no stats are to be computed
     */
    public AbstractTrafficShapingHandler(
            ObjectSizeEstimator objectSizeEstimator, Timer timer,
            long checkInterval) {
        init(objectSizeEstimator, timer, 0, 0, checkInterval);
    }

	

    /**
     * Class to implement setReadable at fix time
     */
    private  class  ReopenReadTimerTask  implements TimerTask {
		
        ChannelHandlerContext ctx;

		
        ReopenReadTimerTask(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

		
        public void run(Timeout timeoutArg) throws Exception {
            //logger.warn("Start RRTT: "+release.get());
            if (release.get()) {
                return;
            }
            /*
            logger.warn("WAKEUP! "+
                    (ctx != null && ctx.getChannel() != null &&
                            ctx.getChannel().isConnected()));
             */
            if (ctx != null && ctx.getChannel() != null &&
                    ctx.getChannel().isConnected()) {
                //logger.warn(" setReadable TRUE: ");
                // readSuspended = false;
                ctx.setAttachment(null);
                ctx.getChannel().setReadable(true);
            }
        }


	}


}
