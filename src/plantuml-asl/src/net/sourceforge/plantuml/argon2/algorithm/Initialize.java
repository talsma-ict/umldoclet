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
package net.sourceforge.plantuml.argon2.algorithm;

import static net.sourceforge.plantuml.argon2.Constants.ARGON2_BLOCK_SIZE;
import static net.sourceforge.plantuml.argon2.Constants.ARGON2_PREHASH_DIGEST_LENGTH;
import static net.sourceforge.plantuml.argon2.Constants.ARGON2_PREHASH_SEED_LENGTH;

import net.sourceforge.plantuml.argon2.Argon2;
import net.sourceforge.plantuml.argon2.Util;
import net.sourceforge.plantuml.argon2.model.Instance;

public class Initialize {
    // ::remove folder when __HAXE__

	public static void initialize(Instance instance, Argon2 argon2) {
		byte[] initialHash = Functions.initialHash(Util.intToLittleEndianBytes(argon2.getLanes()),
				Util.intToLittleEndianBytes(argon2.getOutputLength()), Util.intToLittleEndianBytes(argon2.getMemory()),
				Util.intToLittleEndianBytes(argon2.getIterations()), Util.intToLittleEndianBytes(argon2.getVersion()),
				Util.intToLittleEndianBytes(argon2.getType().ordinal()),
				Util.intToLittleEndianBytes(argon2.getPasswordLength()), argon2.getPassword(),
				Util.intToLittleEndianBytes(argon2.getSaltLength()), argon2.getSalt(),
				Util.intToLittleEndianBytes(argon2.getSecretLength()), argon2.getSecret(),
				Util.intToLittleEndianBytes(argon2.getAdditionalLength()), argon2.getAdditional());
		fillFirstBlocks(instance, initialHash);
	}

	/**
	 * (H0 || 0 || i) 72 byte -> 1024 byte (H0 || 1 || i) 72 byte -> 1024 byte
	 */
	private static void fillFirstBlocks(Instance instance, byte[] initialHash) {

		final byte[] zeroBytes = { 0, 0, 0, 0 };
		final byte[] oneBytes = { 1, 0, 0, 0 };

		byte[] initialHashWithZeros = getInitialHashLong(initialHash, zeroBytes);
		byte[] initialHashWithOnes = getInitialHashLong(initialHash, oneBytes);

		for (int i = 0; i < instance.getLanes(); i++) {

			byte[] iBytes = Util.intToLittleEndianBytes(i);

			System.arraycopy(iBytes, 0, initialHashWithZeros, ARGON2_PREHASH_DIGEST_LENGTH + 4, 4);
			System.arraycopy(iBytes, 0, initialHashWithOnes, ARGON2_PREHASH_DIGEST_LENGTH + 4, 4);

			byte[] blockhashBytes = Functions.blake2bLong(initialHashWithZeros, ARGON2_BLOCK_SIZE);
			instance.memory[i * instance.getLaneLength() + 0].fromBytes(blockhashBytes);

			blockhashBytes = Functions.blake2bLong(initialHashWithOnes, ARGON2_BLOCK_SIZE);
			instance.memory[i * instance.getLaneLength() + 1].fromBytes(blockhashBytes);
		}
	}

	private static byte[] getInitialHashLong(byte[] initialHash, byte[] appendix) {
		byte[] initialHashLong = new byte[ARGON2_PREHASH_SEED_LENGTH];

		System.arraycopy(initialHash, 0, initialHashLong, 0, ARGON2_PREHASH_DIGEST_LENGTH);
		System.arraycopy(appendix, 0, initialHashLong, ARGON2_PREHASH_DIGEST_LENGTH, 4);

		return initialHashLong;
	}

}
