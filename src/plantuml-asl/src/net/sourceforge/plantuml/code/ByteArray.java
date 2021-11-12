/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
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
package net.sourceforge.plantuml.code;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class ByteArray {

	private final byte data[];
	private final int length;

	private ByteArray(byte data[], int length) {
		this.data = data;
		this.length = length;
	}

	public static ByteArray from(byte[] input) {
		return new ByteArray(input, input.length);
	}

	public String toUFT8String() throws UnsupportedEncodingException {
		return new String(data, 0, length, UTF_8);
	}

	public String toUPF9String() throws IOException {
		return Upf9Decoder.decodeString(data, length);
	}

	public int getByteAt(int i) {
		return data[i];
	}

	public int length() {
		return length;
	}

}
