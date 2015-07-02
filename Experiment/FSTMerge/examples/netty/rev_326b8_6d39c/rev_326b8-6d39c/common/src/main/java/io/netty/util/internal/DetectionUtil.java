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
package io.netty.util.internal; 

import java.security.AccessController; 
import java.security.PrivilegedActionException; 
import java.security.PrivilegedExceptionAction; 
import java.util.concurrent.atomic.AtomicInteger; 
import java.util.zip.Deflater; 


/**
 * Utility that detects various properties specific to the current runtime
 * environment, such as Java version and the availability of the
 * {@code sun.misc.Unsafe} object.
 * 
 * <br>
 * You can disable the use of {@code sun.misc.Unsafe} if you specify 
 * the System property <strong>io.netty.tryUnsafe</strong> with
 * value of <code>false</code>. Default is <code>true</code>.
 */
public final  class  DetectionUtil {
	

    private static final int JAVA_VERSION = javaVersion0();

	
    private static final boolean HAS_UNSAFE = hasUnsafe(AtomicInteger.class.getClassLoader());

	
    
    public static boolean hasUnsafe() {
        return HAS_UNSAFE;
    }


	

    public static int javaVersion() {
        return JAVA_VERSION;
    }


	

    <<<<<<< /mnt/Vbox/FSTMerge/binary/fstmerge_tmp1390772187892/fstmerge_var1_3181222183294709040
private static boolean hasUnsafe(ClassLoader loader) {
        String value = SystemPropertyUtil.get("io.netty.tryUnsafe");
        if (value == null) {
            value = SystemPropertyUtil.get("org.jboss.netty.tryUnsafe", "true");
        }
        boolean useUnsafe = Boolean.valueOf(value);
        if (!useUnsafe) {
            return false;
        }
        
        try {
            Class<?> unsafeClazz = Class.forName("sun.misc.Unsafe", true, loader);
            return hasUnsafeField(unsafeClazz);
        } catch (Exception e) {
            // Ignore
        }
        return false;
=======
private static boolean hasUnsafe(ClassLoader loader) {
        try {
            Class<?> unsafeClazz = Class.forName("sun.misc.Unsafe", true, loader);
            return hasUnsafeField(unsafeClazz);
        } catch (Exception e) {
            // Ignore
        }
        return false;
>>>>>>> /mnt/Vbox/FSTMerge/binary/fstmerge_tmp1390772187892/fstmerge_var2_75836530404530234
    }


	

    private static boolean hasUnsafeField(final Class<?> unsafeClass) throws PrivilegedActionException {
        return AccessController.doPrivileged(new PrivilegedExceptionAction<Boolean>() {
            @Override
            public Boolean run() throws Exception {
                unsafeClass.getDeclaredField("theUnsafe");
                return true;
            }
        });
    }


	

    <<<<<<< /mnt/Vbox/FSTMerge/binary/fstmerge_tmp1390772187997/fstmerge_var1_1918443594410390977
private static int javaVersion0() {
        try {
            // Check if its android, if so handle it the same way as java6.
            //
            // See https://github.com/netty/netty/issues/282
            Class.forName("android.app.Application");
            return 6;
        } catch (ClassNotFoundException e) {
            //Ignore
        }
        try {
            Deflater.class.getDeclaredField("SYNC_FLUSH");
            return 7;
        } catch (Exception e) {
            // Ignore
        }

        return 6;
=======
private static int javaVersion0() {
        try {
            Deflater.class.getDeclaredField("SYNC_FLUSH");
            return 7;
        } catch (Exception e) {
            // Ignore
        }

        return 6;
>>>>>>> /mnt/Vbox/FSTMerge/binary/fstmerge_tmp1390772187997/fstmerge_var2_5165496586561346470
    }


	
    
    private DetectionUtil() {
        // only static method supported
    }


	
    private static final boolean IS_WINDOWS;

	
    static {
        String os = System.getProperty("os.name").toLowerCase();
        // windows
        IS_WINDOWS =  os.indexOf("win") >= 0;
    }

	
    
    /**
     * Return <code>true</code> if the JVM is running on Windows
     * 
     */
    public static boolean isWindows() {
        return IS_WINDOWS;
    }


}
