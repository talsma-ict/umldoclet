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
package net.sourceforge.plantuml.eggs;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.UnsupportedEncodingException;

public class SentenceDecoder {
	// ::remove file when __CORE__

	private final String secret;

	public SentenceDecoder(String sentence1, byte[] crypted) throws UnsupportedEncodingException {
		final byte[] key = EggUtils.fromSecretSentence(sentence1).toByteArray();
		final byte[] sen2 = EggUtils.xor(crypted, key);
		this.secret = new String(sen2, UTF_8);
	}

	public boolean isOk() {
		for (char c : secret.toCharArray()) {
			if ((int) c > 256) {
				return false;
			}
			if (Character.isDefined(c) == false) {
				return false;
			}
			if (Character.isISOControl(c)) {
				return false;
			}
		}
		return true;
	}

	public String getSecret() {
		return secret;
	}

}
