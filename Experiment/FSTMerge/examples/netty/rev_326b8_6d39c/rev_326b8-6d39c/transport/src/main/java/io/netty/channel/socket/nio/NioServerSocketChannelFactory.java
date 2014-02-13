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
package io.netty.channel.socket.nio; 

import java.nio.channels.Selector; 
import java.util.concurrent.Executor; 
import java.util.concurrent.Executors; 
import java.util.concurrent.RejectedExecutionException; 

import io.netty.channel.Channel; 
import io.netty.channel.ChannelPipeline; 
import io.netty.channel.ChannelSink; 
import io.netty.channel.group.ChannelGroup; 
import io.netty.channel.socket.ServerSocketChannel; 
import io.netty.channel.socket.ServerSocketChannelFactory; 
import io.netty.channel.socket.Worker; 
import io.netty.util.ExternalResourceReleasable; 

/**
 * A {@link ServerSocketChannelFactory} which creates a server-side NIO-based
 * {@link ServerSocketChannel}.  It utilizes the non-blocking I/O mode which
 * was introduced with NIO to serve many number of concurrent connections
 * efficiently.
 *
 * <h3>How threads work</h3>
 * <p>
 * There are two types of threads in a {@link NioServerSocketChannelFactory};
 * one is boss thread and the other is worker thread.
 *
 * <h4>Boss threads</h4>
 * <p>
 * Each bound {@link ServerSocketChannel} has its own boss thread.
 * For example, if you opened two server ports such as 80 and 443, you will
 * have two boss threads.  A boss thread accepts incoming connections until
 * the port is unbound.  Once a connection is accepted successfully, the boss
 * thread passes the accepted {@link Channel} to one of the worker
 * threads that the {@link NioServerSocketChannelFactory} manages.
 *
 * <h4>Worker threads</h4>
 * <p>
 * One {@link NioServerSocketChannelFactory} can have one or more worker
 * threads.  A worker thread performs non-blocking read and write for one or
 * more {@link Channel}s in a non-blocking mode.
 *
 * <h3>Life cycle of threads and graceful shutdown</h3>
 * <p>
 * All threads are acquired from the {@link Executor}s which were specified
 * when a {@link NioServerSocketChannelFactory} was created.  Boss threads are
 * acquired from the {@code bossExecutor}, and worker threads are acquired from
 * the {@code workerExecutor}.  Therefore, you should make sure the specified
 * {@link Executor}s are able to lend the sufficient number of threads.
 * It is the best bet to specify {@linkplain Executors#newCachedThreadPool() a cached thread pool}.
 * <p>
 * Both boss and worker threads are acquired lazily, and then released when
 * there's nothing left to process.  All the related resources such as
 * {@link Selector} are also released when the boss and worker threads are
 * released.  Therefore, to shut down a service gracefully, you should do the
 * following:
 *
 * <ol>
 * <li>unbind all channels created by the factory,
 * <li>close all child channels accepted by the unbound channels, and
 *     (these two steps so far is usually done using {@link ChannelGroup#close()})</li>
 * <li>call {@link #releaseExternalResources()}.</li>
 * </ol>
 *
 * Please make sure not to shut down the executor until all channels are
 * closed.  Otherwise, you will end up with a {@link RejectedExecutionException}
 * and the related resources might not be released properly.
 * @apiviz.landmark
 */
  class  NioServerSocketChannelFactory   {
	

    

	
    

	

    /**
     * Create a new {@link NioServerSocketChannelFactory} using
     * {@link Executors#newCachedThreadPool()} for the workers.
     * 
     * See {@link #NioServerSocketChannelFactory(Executor, Executor)}
     */
    <<<<<<< /mnt/Vbox/FSTMerge/binary/fstmerge_tmp1390772199851/fstmerge_var1_6223452982431840672
public NioServerSocketChannelFactory() {
        this(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
    }
=======
>>>>>>> /mnt/Vbox/FSTMerge/binary/fstmerge_tmp1390772199851/fstmerge_var2_745564566502669275


	
    
    /**
     * Creates a new instance which use the given {@link WorkerPool} for everything.
     *
     * @param genericExecutor
     *        the {@link Executor} which will execute the I/O worker threads ( this also includes handle the accepting of new connections)
     *        
     */
    <<<<<<< /mnt/Vbox/FSTMerge/binary/fstmerge_tmp1390772199902/fstmerge_var1_6060793024453134103
public NioServerSocketChannelFactory(Executor genericExecutor) {
        this(genericExecutor, SelectorUtil.DEFAULT_IO_ACCEPTING_THREADS + SelectorUtil.DEFAULT_IO_THREADS);
    }
=======
>>>>>>> /mnt/Vbox/FSTMerge/binary/fstmerge_tmp1390772199902/fstmerge_var2_7715169006814664540


	

    /**
     * Creates a new instance which use the given {@link WorkerPool} for everything.
     *
     * @param genericExecutor
     *        the {@link Executor} which will execute the I/O worker threads ( this also includes handle the accepting of new connections)
     * @param workerCount
     *        the maximum number of I/O worker threads
     *        
     */
    <<<<<<< /mnt/Vbox/FSTMerge/binary/fstmerge_tmp1390772199953/fstmerge_var1_5483198288207349216
public NioServerSocketChannelFactory(Executor genericExecutor, int workerCount) {
        this(new NioWorkerPool(genericExecutor, workerCount, true));
    }
=======
>>>>>>> /mnt/Vbox/FSTMerge/binary/fstmerge_tmp1390772199953/fstmerge_var2_6935855734879261297


	
    
    
    /**
     * Creates a new instance which use the given {@link WorkerPool} for everything.
     *
     * @param genericWorkerPool
     *        the {@link WorkerPool} which will be used to obtain the {@link Worker} that execute the I/O worker threads (that included accepting of new connections)
     */
    <<<<<<< /mnt/Vbox/FSTMerge/binary/fstmerge_tmp1390772200004/fstmerge_var1_705565990278551807
public NioServerSocketChannelFactory(WorkerPool<NioWorker> genericWorkerPool) {
        this(genericWorkerPool, genericWorkerPool);
    }
=======
>>>>>>> /mnt/Vbox/FSTMerge/binary/fstmerge_tmp1390772200004/fstmerge_var2_8545950515701518627


	

    
    <<<<<<< /mnt/Vbox/FSTMerge/binary/fstmerge_tmp1390772200055/fstmerge_var1_7818828554894839443
@Override
    public ServerSocketChannel newChannel(ChannelPipeline pipeline) {
        return NioServerSocketChannel.create(this, pipeline, sink, bossWorkerPool.nextWorker(), workerPool);
    }
=======
>>>>>>> /mnt/Vbox/FSTMerge/binary/fstmerge_tmp1390772200055/fstmerge_var2_5025750936568028099


	

    <<<<<<< /mnt/Vbox/FSTMerge/binary/fstmerge_tmp1390772200106/fstmerge_var1_761419080615267931
@Override
    public void releaseExternalResources() {
        if (bossWorkerPool instanceof ExternalResourceReleasable) {
            ((ExternalResourceReleasable) bossWorkerPool).releaseExternalResources();
        }
        if (workerPool instanceof ExternalResourceReleasable) {
            ((ExternalResourceReleasable) workerPool).releaseExternalResources();
        }
        
        
    }
=======
>>>>>>> /mnt/Vbox/FSTMerge/binary/fstmerge_tmp1390772200106/fstmerge_var2_3106491837243931292


	
    private final WorkerPool<NioWorker> bossWorkerPool;

	
    
    
    /**
     * Creates a new instance.  Calling this constructor is same with calling
     * {@link #NioServerSocketChannelFactory(Executor, Executor, int, int)} with 1 
     * as boss count and 2 * the number of available processors in the machine.  The number of
     * available processors is obtained by {@link Runtime#availableProcessors()}.
     *
     * @param bossExecutor
     *        the {@link Executor} which will execute the I/O worker threads that handle the accepting of new connections
     * @param workerExecutor
     *        the {@link Executor} which will execute the I/O worker threads
     */
    public NioServerSocketChannelFactory(Executor bossExecutor, Executor workerExecutor) {
        this(bossExecutor, workerExecutor, SelectorUtil.DEFAULT_IO_ACCEPTING_THREADS, SelectorUtil.DEFAULT_IO_THREADS);
    }

	

    /**
     * Creates a new instance.
     * 
     * @param bossExecutor
     *        the {@link Executor} which will execute the I/O worker threads that handle the accepting of new connections
     * @param workerExecutor
     *        the {@link Executor} which will execute the I/O worker threads
     * @param bossCount
     *        the maximum number of I/O worker threads that handling the accepting of connections
     * @param workerCount
     *        the maximum number of I/O worker threads
     */
    public NioServerSocketChannelFactory(Executor bossExecutor, Executor workerExecutor, int bossCount,
            int workerCount) {
        this(new NioWorkerPool(bossExecutor, bossCount, true), new NioWorkerPool(workerExecutor, workerCount, true));
    }

	

    /**
     * Creates a new instance.
     *
     * @param bossWorkerPool
     *        the {@link WorkerPool} which will be used to obtain the {@link Worker} that execute the I/O worker threads that handle the accepting of new connections
     * @param workerPool
     *        the {@link WorkerPool} which will be used to obtain the {@link Worker} that execute the I/O worker threads
     */
    public NioServerSocketChannelFactory(WorkerPool<NioWorker> bossWorkerPool, WorkerPool<NioWorker> workerPool) {
        if (bossWorkerPool == null) {
            throw new NullPointerException("bossWorkerPool");
        }
        if (workerPool == null) {
            throw new NullPointerException("workerPool");
        }
        this.bossWorkerPool = bossWorkerPool;
        this.workerPool = workerPool;
        sink = new NioServerSocketPipelineSink();
    }


}
