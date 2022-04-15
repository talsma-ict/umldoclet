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

import static java.nio.charset.StandardCharsets.UTF_8;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.math.BigInteger;

import net.sourceforge.plantuml.utils.MTRandom;

public class DedicationCrypted implements Dedication {

	private final String argon2;
	private final BigInteger pq;
	private final byte crypted[];
	private final int tinyHash;
	private String solution;

	private long next = 0L;

	public DedicationCrypted(byte crypted[], int tinyHash, String argon2, BigInteger pq) {
		this.crypted = crypted;
		this.pq = pq;
		this.argon2 = argon2;
		this.tinyHash = tinyHash;
	}

	public synchronized BufferedImage getImage(final TinyHashableString sentence) {
		final String line = sentence.getSentence();

		if (line.length() < 40) {
			return null;
		}

		try {
			if (solution == null || line.equals(this.solution) == false) {
				if (System.currentTimeMillis() < next) {
					return null;
				}
				if (this.tinyHash != sentence.tinyHash()) {
					return null;
				}
				this.next = System.currentTimeMillis() + 5000L;
			}

			final byte[] hash1 = Noise.computeArgon2bytes(line.getBytes(UTF_8),
					(pq.toString(35) + line).getBytes(UTF_8));
			final byte[] hash2 = Noise.computeArgon2bytes(line.getBytes(UTF_8),
					(pq.toString(36) + line).getBytes(UTF_8));

			final BlumBlumShub rndBBS = new BlumBlumShub(pq, hash1);
			final MTRandom rndMT = new MTRandom(hash2);

			byte[] current = crypted.clone();
			Noise.shuffle(current, rndMT);
			Noise.xor(current, rndBBS);
			Noise.xor(current, line.getBytes(UTF_8));

			Noise.shuffle(current, rndMT);

			final RBlocks init = RBlocks.readFrom(current, 513);
			final RBlocks decoded = init.change(E, N);

			current = decoded.toByteArray(512);

			Noise.shuffle(current, rndMT);
			Noise.xor(current, rndBBS);

			final String argon = Noise.computeArgon2String(current, (pq.toString(34) + line).getBytes(UTF_8));

			if (this.argon2.equals(argon) == false) {
				return null;
			}
			Noise.shuffle(current, rndMT);
			current = Noise.reverse(current, rndMT.nextInt());

			final BufferedImage img = PSystemDedication.getBufferedImage(new ByteArrayInputStream(current));
			this.solution = line;
			return img;
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}

	}

}
