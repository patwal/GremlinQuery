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
import static io.netty.channel.Channels.fireChannelDisconnectedLater; 
import static io.netty.channel.Channels.fireExceptionCaught; 
import static io.netty.channel.Channels.fireExceptionCaughtLater; 
import static io.netty.channel.Channels.fireMessageReceived; 
import static io.netty.channel.Channels.succeededFuture; 
import io.netty.buffer.ChannelBufferFactory; 
import io.netty.channel.ChannelException; 
import io.netty.channel.ChannelFuture; 
import io.netty.channel.Channels; 
import io.netty.channel.MessageEvent; 
import io.netty.channel.ReceiveBufferSizePredictor; 
import io.netty.channel.socket.nio.SendBufferPool.SendBuffer; 
import java.net.SocketAddress; 
import java.nio.ByteBuffer; 
import java.nio.channels.AsynchronousCloseException; 
import java.nio.channels.ClosedChannelException; 
import java.nio.channels.DatagramChannel; 
import java.nio.channels.SelectionKey; 
import java.nio.channels.Selector; 
import java.util.Queue; 
import java.util.concurrent.Executor; 

import java.io.IOException; 

/**
 * A class responsible for registering channels with {@link Selector}.
 * It also implements the {@link Selector} loop.
 */
  class  NioDatagramWorker  extends AbstractNioWorker {
	

    /**
     * Sole constructor.
     *
     * @param executor the {@link Executor} used to execute {@link Runnable}s
     *                 such as {@link ChannelRegistionTask}
     */
    

	

    

	
    
    

	

    

	


    <<<<<<< /mnt/Vbox/FSTMerge/binary/fstmerge_tmp1390772199340/fstmerge_var1_5003987821671789804
@Override
    protected void registerTask(AbstractNioChannel channel, ChannelFuture future) {
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
                channel.getJdkChannel().register(
                        selector, channel.getRawInterestOps(), channel);
            }
            if (future != null) {
                future.setSuccess();
            }
        } catch (final IOException e) {
            if (future != null) {
                future.setFailure(e);
            }
            close(channel, succeededFuture(channel));
            if (!(e instanceof ClosedChannelException)) {
                throw new ChannelException(
                        "Failed to register a socket to the selector.", e);
            }
        }
    }
=======
>>>>>>> /mnt/Vbox/FSTMerge/binary/fstmerge_tmp1390772199340/fstmerge_var2_6625785192035795157


	

    

	
    
    


}
