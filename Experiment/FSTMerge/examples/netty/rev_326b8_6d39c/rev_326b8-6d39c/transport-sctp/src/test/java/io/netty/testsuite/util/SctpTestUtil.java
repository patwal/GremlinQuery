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
package io.netty.testsuite.util; 
import java.util.Locale; 

import java.io.IOException; 

import com.sun.nio.sctp.SctpChannel; 

  class  SctpTestUtil {
	
    //io.netty.util.SocketAddresses.LOCALHOST interface has MTU SIZE issues with SCTP, we have  to use local loop back interface for testing
    

	
    

	
    
    /**
     * Return <code>true</code> if SCTP is supported by the running os.
     * 
     */
    <<<<<<< /mnt/Vbox/FSTMerge/binary/fstmerge_tmp1390772207848/fstmerge_var1_6175772560453981420
public static boolean isSctpSupported() {
        String os = System.getProperty("os.name").toLowerCase(Locale.UK);
        if (os.equals("unix") || os.equals("linux") || os.equals("sun") || os.equals("solaris")) {
            try {
                SctpChannel.open();
            } catch (IOException e) {
                // ignore
            } catch (UnsupportedOperationException e) {
                // This exception may get thrown if the OS does not have
                // the shared libs installed.
                System.out.print("Not supported: " + e.getMessage());
                return false;
                
            }

            return true;
        }
        return false;
    }
=======
>>>>>>> /mnt/Vbox/FSTMerge/binary/fstmerge_tmp1390772207848/fstmerge_var2_4253769790806324998



}
