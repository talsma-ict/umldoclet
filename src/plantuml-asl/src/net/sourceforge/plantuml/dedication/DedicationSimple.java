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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

public class DedicationSimple implements Dedication {

	private final byte crypted[];
	private final String sentence;

	public DedicationSimple(byte crypted[], String sentence) {
		this.crypted = crypted;
		this.sentence = sentence;
	}

	public synchronized BufferedImage getImage(TinyHashableString sentence) {
		if (same(this.sentence, sentence.getSentence()) == false) {
			return null;
		}

		try {
			byte[] current = crypted.clone();

			final RBlocks init = RBlocks.readFrom(current, 513);
			final RBlocks decoded = init.change(E, N);
			current = decoded.toByteArray(512);
			return PSystemDedication.getBufferedImage(new ByteArrayInputStream(current));
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}

	private boolean same(String s1, String s2) {
		s1 = s1.replaceAll("[^\\p{L}0-9]+", "");
		s2 = s2.replaceAll("[^\\p{L}0-9]+", "");
		return s1.equalsIgnoreCase(s2);
	}

}
