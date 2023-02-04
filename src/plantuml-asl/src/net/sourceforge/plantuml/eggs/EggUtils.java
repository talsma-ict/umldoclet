/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2023, Arnaud Roques
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
package net.sourceforge.plantuml.eggs;

import java.math.BigInteger;

import net.sourceforge.plantuml.StringUtils;

public class EggUtils {

	public static String fromByteArrays(byte data[]) {
		final StringBuilder sb = new StringBuilder();
		for (byte b : data) {
			final String hex = Integer.toHexString(b & 0xFF);
			if (hex.length() == 1)
				sb.append('0');

			sb.append(hex);
		}
		return sb.toString();
	}

	public static byte[] toByteArrays(String s) {
		final byte[] result = new byte[s.length() / 2];
		for (int i = 0; i < result.length; i++)
			result[i] = (byte) Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16);

		return result;
	}

	public static BigInteger fromSecretSentence(String s) {
		BigInteger result = BigInteger.ZERO;
		final BigInteger twentySix = BigInteger.valueOf(26);
		s = s.replace('\u00E9', 'e');
		s = s.replace('\u00EA', 'e');
		for (char c : s.toCharArray()) {
			final int num = convertChar(c);
			if (num != -1) {
				result = result.multiply(twentySix);
				result = result.add(BigInteger.valueOf(num));
			}
		}
		return result;

	}

	private static int convertChar(char c) {
		c = StringUtils.goLowerCase(c);
		if (c >= 'a' && c <= 'z')
			return c - 'a';
		return -1;
	}

	public static byte[] xor(byte data[], byte key[]) {
		final byte[] result = new byte[data.length];
		int pos = 0;
		for (int i = 0; i < result.length; i++) {
			result[i] = (byte) (data[i] ^ key[pos++]);
			if (pos == key.length)
				pos = 0;
		}
		return result;
	}

}
