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

import static io.netty.channel.Channels.fireChannelDisconnected; 
import static io.netty.channel.Channels.fireExceptionCaught; 
import static io.netty.channel.Channels.fireMessageReceived; 
import static io.netty.channel.Channels.succeededFuture; 

import io.netty.buffer.ChannelBufferFactory; 
import io.netty.channel.ChannelException; 
import io.netty.channel.ChannelFuture; 
import io.netty.channel.ReceiveBufferSizePredictor; 
import java.net.SocketAddress; 
import java.nio.ByteBuffer; 
import java.nio.channels.ClosedChannelException; 
import java.nio.channels.DatagramChannel; 
import java.nio.channels.SelectionKey; 
import java.nio.channels.Selector; 
import java.util.concurrent.Executor; 

import static io.netty.channel.Channels.*; 

import java.io.IOException; 
import java.nio.channels.AsynchronousCloseException; 
import java.nio.channels.CancelledKeyException; 
import java.nio.channels.NotYetBoundException; 
import java.util.Iterator; 
import java.util.Queue; 
import java.util.Set; 
import java.util.concurrent.ExecutorService; 
import java.util.concurrent.atomic.AtomicBoolean; 
import java.util.concurrent.locks.ReadWriteLock; 
import java.util.concurrent.locks.ReentrantReadWriteLock; 
import io.netty.channel.Channel; 
import io.netty.channel.MessageEvent; 
import io.netty.channel.socket.nio.SocketSendBufferPool.SendBuffer; 
import io.netty.logging.InternalLogger; 
import io.netty.logging.InternalLoggerFactory; 
import io.netty.util.internal.QueueFactory; 

/**
 * A class responsible for registering channels with {@link Selector}.
 * It also implements the {@link Selector} loop.
 */
 

/**
 * A class responsible for registering channels with {@link Selector}.
 * It also implements the {@link Selector} loop.
 */
class  NioDatagramWorker  extends AbstractNioWorker   {
	

    /**
     * Sole constructor.
     *
     * @param executor the {@link Executor} used to execute {@link Runnable}s
     *                 such as {@link ChannelRegistionTask}
     */
    NioDatagramWorker(final Executor executor) {
        super(executor);
    }


	

    /**
     * Read is called when a Selector has been notified that the underlying channel
     * was something to be read. The channel would previously have registered its interest
     * in read operations.
     *
     * @param key The selection key which contains the Selector registration information.
     */
    @Override
    protected boolean read(final SelectionKey key) {
        final NioDatagramChannel channel = (NioDatagramChannel) key.attachment();
        ReceiveBufferSizePredictor predictor =
            channel.getConfig().getReceiveBufferSizePredictor();
        final ChannelBufferFactory bufferFactory = channel.getConfig().getBufferFactory();
        final DatagramChannel nioChannel = (DatagramChannel) key.channel();

        // Allocating a non-direct buffer with a max udp packge size.
        // Would using a direct buffer be more efficient or would this negatively
        // effect performance, as direct buffer allocation has a higher upfront cost
        // where as a ByteBuffer is heap allocated.
        final ByteBuffer byteBuffer = ByteBuffer.allocate(
                predictor.nextReceiveBufferSize()).order(bufferFactory.getDefaultOrder());

        boolean failure = true;
        SocketAddress remoteAddress = null;
        try {
            // Receive from the channel in a non blocking mode. We have already been notified that
            // the channel is ready to receive.
            remoteAddress = nioChannel.receive(byteBuffer);
            failure = false;
        } catch (ClosedChannelException e) {
            // Can happen, and does not need a user attention.
        } catch (Throwable t) {
            fireExceptionCaught(channel, t);
        }

        if (remoteAddress != null) {
            // Flip the buffer so that we can wrap it.
            byteBuffer.flip();

            int readBytes = byteBuffer.remaining();
            if (readBytes > 0) {
                // Update the predictor.
                predictor.previousReceiveBufferSize(readBytes);

                // Notify the interested parties about the newly arrived message.
                fireMessageReceived(
                        channel, bufferFactory.getBuffer(byteBuffer), remoteAddress);
            }
        }

        if (failure) {
            key.cancel(); // Some JDK implementations run into an infinite loop without this.
            close(channel, succeededFuture(channel));
            return false;
        }

        return true;
    }


	
    

    @Override
    protected boolean scheduleWriteIfNecessary(final AbstractNioChannel<?> channel) {
        final Thread workerThread = thread;
        if (workerThread == null || Thread.currentThread() != workerThread) {
            if (channel.writeTaskInTaskQueue.compareAndSet(false, true)) {
                // "add" the channels writeTask to the writeTaskQueue.
                boolean offered = writeTaskQueue.offer(channel.writeTask);
                assert offered;
            }

            final Selector selector = this.selector;
            if (selector != null) {
                if (wakenUp.compareAndSet(false, true)) {
                    selector.wakeup();
                }
            }
            return true;
        }

        return false;
    }

	

    static void disconnect(NioDatagramChannel channel, ChannelFuture future) {
        boolean connected = channel.isConnected();
        try {
            channel.getDatagramChannel().disconnect();
            future.setSuccess();
            if (connected) {
                fireChannelDisconnected(channel);
            }
        } catch (Throwable t) {
            future.setFailure(t);
            fireExceptionCaught(channel, t);
        }
    }


	


    @Override
    protected Runnable createRegisterTask(AbstractNioChannel<?> channel, ChannelFuture future) {
        return new ChannelRegistionTask((NioDatagramChannel) channel, future);
    }

	

    /**
     * RegisterTask is a task responsible for registering a channel with a
     * selector.
     */
    private final  class  ChannelRegistionTask  implements Runnable {
		
        private final NioDatagramChannel channel;

		

        private final ChannelFuture future;

		

        ChannelRegistionTask(final NioDatagramChannel channel,
                final ChannelFuture future) {
            this.channel = channel;
            this.future = future;
        }


		

        /**
         * This runnable's task. Does the actual registering by calling the
         * underlying DatagramChannels peer DatagramSocket register method.
         */
        @Override
        public void run() {
            final SocketAddress localAddress = channel.getLocalAddress();
            if (localAddress == null) {
                if (future != null) {
                    future.setFailure(new ClosedChannelException());
                }
                close(channel, succeededFuture(channel));
                return;
            }

            try {
                synchronized (channel.interestOpsLock) {
                    channel.getDatagramChannel().register(
                            selector, channel.getRawInterestOps(), channel);
                }
                if (future != null) {
                    future.setSuccess();
                }
            } catch (final ClosedChannelException e) {
                if (future != null) {
                    future.setFailure(e);
                }
                close(channel, succeededFuture(channel));
                throw new ChannelException(
                        "Failed to register a socket to the selector.", e);
            }
        }



	}

	
    /**
     * Internal Netty logger.
     */
    

	

    /**
     * Executor used to execute {@link Runnable}s such as
     * {@link ChannelRegistionTask}.
     */
    

	

    /**
     * Boolean to indicate if this worker has been started.
     */
    

	

    /**
     * If this worker has been started thread will be a reference to the thread
     * used when starting. i.e. the current thread when the run method is executed.
     */
    

	

    /**
     * The NIO {@link Selector}.
     */
    

	

    /**
     * Boolean that controls determines if a blocked Selector.select should
     * break out of its selection process. In our case we use a timeone for
     * the select method and the select method will block for that time unless
     * waken up.
     */
    

	

    /**
     * Lock for this workers Selector.
     */
    

	

    /**
     * Monitor object used to synchronize selector open/close.
     */
    

	

    /**
     * Queue of {@link ChannelRegistionTask}s
     */
    

	

    /**
     * Queue of WriteTasks
     */
    

	

    

	 // should use AtomicInteger but we just need approximation

    

	

    /**
     * Registers the passed-in channel with a selector.
     *
     * @param channel the channel to register
     * @param future  the {@link ChannelFuture} that has to be notified on
     *                completion
     */
    <<<<<<< /mnt/Vbox/FSTMerge/binary/fstmerge_tmp1390772355763/fstmerge_var1_6244366017375883256
void register(final NioDatagramChannel channel, final ChannelFuture future) {
        final Runnable channelRegTask = new ChannelRegistionTask(channel,
                future);
        Selector selector;

        synchronized (startStopLock) {
            if (!started) {
                // Open a selector if this worker didn't start yet.
                try {
                    this.selector = selector = Selector.open();
                } catch (final Throwable t) {
                    throw new ChannelException("Failed to create a selector.",
                            t);
                }

                boolean success = false;
                try {
                    // Start the main selector loop. See run() for details.
                    executor.execute(this);
                    success = true;
                } finally {
                    if (!success) {
                        try {
                            // Release the Selector if the execution fails.
                            selector.close();
                        } catch (final Throwable t) {
                            if (logger.isWarnEnabled()) {
                                logger.warn("Failed to close a selector.", t);
                            }
                        }
                        this.selector = selector = null;
                        // The method will return to the caller at this point.
                    }
                }
            } else {
                // Use the existing selector if this worker has been started.
                selector = this.selector;
            }
            assert selector != null && selector.isOpen();

            started = true;

            // "Add" the registration task to the register task queue.
            boolean offered = registerTaskQueue.offer(channelRegTask);
            assert offered;
        }

        if (wakenUp.compareAndSet(false, true)) {
            selector.wakeup();
        }
    }
=======
>>>>>>> /mnt/Vbox/FSTMerge/binary/fstmerge_tmp1390772355763/fstmerge_var2_4956413971459871660


	

    /**
     * Selector loop.
     */
    

	

    /**
     * Will go through all the {@link ChannelRegistionTask}s in the
     * task queue and run them (registering them).
     */
    

	

    /**
     * Will go through all the WriteTasks and run them.
     */
    

	

    

	

    

	

    

	

    

	

    

	

    

	

    

	

    

	

    

	

    

	

    

	

    

	

    


}
