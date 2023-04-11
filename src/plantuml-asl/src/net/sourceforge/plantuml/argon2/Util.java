/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
 *
 * Project Info:  https://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * https://plantuml.com/patreon (only 1$ per month!)
 * https://plantuml.com/paypal
 * 
 * This file is part of PlantUML.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Original Author:  Arnaud Roques
 */
/* 	This file is taken from
	https://github.com/andreas1327250/argon2-java

	Original Author: Andreas Gadermaier <up.gadermaier@gmail.com>
 */
package net.sourceforge.plantuml.argon2;

public class Util {

	public static String bytesToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02x", b & 0xff));
		}
		return sb.toString();
	}

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	public static long littleEndianBytesToLong(byte[] b) {
		long result = 0;
		for (int i = 7; i >= 0; i--) {
			result <<= 8;
			result |= (b[i] & 0xFF);
		}
		return result;
	}

	public static byte[] intToLittleEndianBytes(int a) {
		byte[] result = new byte[4];
		result[0] = (byte) (a & 0xFF);
		result[1] = (byte) ((a >> 8) & 0xFF);
		result[2] = (byte) ((a >> 16) & 0xFF);
		result[3] = (byte) ((a >> 24) & 0xFF);
		return result;
	}

	public static byte[] longToLittleEndianBytes(long a) {
		byte[] result = new byte[8];
		result[0] = (byte) (a & 0xFF);
		result[1] = (byte) ((a >> 8) & 0xFF);
		result[2] = (byte) ((a >> 16) & 0xFF);
		result[3] = (byte) ((a >> 24) & 0xFF);
		result[4] = (byte) ((a >> 32) & 0xFF);
		result[5] = (byte) ((a >> 40) & 0xFF);
		result[6] = (byte) ((a >> 48) & 0xFF);
		result[7] = (byte) ((a >> 56) & 0xFF);
		return result;
	}

	public static long intToLong(int x) {
		byte[] intBytes = intToLittleEndianBytes(x);
		byte[] bytes = new byte[8];
		System.arraycopy(intBytes, 0, bytes, 0, 4);
		return littleEndianBytesToLong(bytes);
	}

}
