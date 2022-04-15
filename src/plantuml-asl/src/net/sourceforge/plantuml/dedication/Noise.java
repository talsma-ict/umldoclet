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
package net.sourceforge.plantuml.dedication;

import net.sourceforge.plantuml.argon2.Argon2;
import net.sourceforge.plantuml.argon2.Argon2Factory;
import net.sourceforge.plantuml.argon2.model.Argon2Type;
import net.sourceforge.plantuml.utils.MTRandom;

public class Noise {

	private static Argon2 argon2(byte[] buffer, byte[] salt) {
		final Argon2 argon = Argon2Factory.create() //
				.setType(Argon2Type.Argon2id) //
				.setMemory(8) //
				.setSalt(salt.clone()) //
				.setIterations(50) //
				.setPassword(buffer.clone());
		argon.hashNow();
		return argon;
	}

	public static String computeArgon2String(byte[] buffer, byte[] salt) {
		return argon2(buffer, salt).getOutputString();
	}

	public static byte[] computeArgon2bytes(byte[] buffer, byte[] salt) {
		return argon2(buffer, salt).getOutput();
	}

	public static int shortHash(byte[] buffer, byte[] salt) {
		final byte hash[] = argon2(buffer, salt).getOutput();
		int result = 0;
		for (byte b : hash) {
			final int b1 = b & 0x0F;
			final int b2 = (b & 0xF0) >> 4;
			result ^= b1 ^ b2;
		}
		return result;
	}
	
	public static void shuffle(byte[] buffer, MTRandom rnd) {
		for (int i = 0; i < buffer.length; i++) {
			final int r1 = rnd.nextInt();
			final int r2 = rnd.nextInt();
			final int a = Math.abs(r1) % buffer.length;
			final int b = Math.abs(r2) % buffer.length;
			final byte tmp = buffer[a];
			buffer[a] = buffer[b];
			buffer[b] = tmp;
		}
	}
	
	public static void xor(byte[] buffer, byte[] xor) {
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] ^= xor[i % xor.length];
		}
	}

	public static void xor(byte[] buffer, BlumBlumShub rnd) {
		for (int i = 0; i < buffer.length; i++) {
			final byte mask = (byte) (rnd.nextRnd(8) & 0xFF);
			buffer[i] = (byte) (buffer[i] ^ mask);
		}
	}
	
	public static byte[] reverse(byte[] buffer, int delta) {
		delta = Math.abs(delta) % buffer.length;
		final byte result[] = new byte[buffer.length];
		for (int i = 0; i < buffer.length; i++)
			result[i] = buffer[(buffer.length - 1 - i + delta) % buffer.length];
		return result;
	}







}
