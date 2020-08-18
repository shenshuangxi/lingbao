package com.sundy.lingbao.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.util.Enumeration;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class MachineUtil {
	private static final Logger logger = LoggerFactory.getLogger(MachineUtil.class);
	private static final int MACHINE_IDENTIFIER = createMachineIdentifier();

	public static int getMachineIdentifier() {
		return MACHINE_IDENTIFIER;
	}
	
	public static void main(String[] args) {
		int code = createMachineIdentifier();
		System.out.println(code);
	}

	/**
	 * Get the machine identifier from mac address
	 *
	 * @see <a
	 *      href=https://github.com/mongodb/mongo-java-driver/blob/master/bson/src/main/org/bson/types/ObjectId.java>ObjectId.java</a>
	 */
	private static int createMachineIdentifier() {
		// build a 2-byte machine piece based on NICs info
		int machinePiece;
		try {
			StringBuilder sb = new StringBuilder();
			Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();

			if (e != null) {
				while (e.hasMoreElements()) {
					NetworkInterface ni = e.nextElement();
					sb.append(ni.toString());
					byte[] mac = ni.getHardwareAddress();
					if (mac != null) {
						for (byte b : mac) {
							sb.append(parseByte(b));
						}
					}
				}
			}

			machinePiece = sb.toString().hashCode();
		} catch (Throwable ex) {
			// exception sometimes happens with IBM JVM, use random
			machinePiece = new SecureRandom().nextInt();
			logger.warn("Failed to get machine identifier from network interface, using random number instead", ex);
		}
		return machinePiece;
	}
	
	private static String parseByte(byte b) {
        int intValue = 0;
        if (b >= 0) {
                intValue = b;
        } else {
                intValue = 256 + b;
        }
        return Integer.toHexString(intValue);
	}
}
