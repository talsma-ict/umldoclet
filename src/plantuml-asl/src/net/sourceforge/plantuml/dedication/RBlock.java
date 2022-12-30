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

import java.math.BigInteger;

public class RBlock {

	private final byte[] buffer;

	private RBlock(final byte[] init) {
		this.buffer = new byte[init.length + 1];
		System.arraycopy(init, 0, buffer, 1, init.length);
	}

	public RBlock(final byte[] init, int start, int size) {
		this.buffer = new byte[size + 1];
		if (start + size < init.length)
			System.arraycopy(init, start, buffer, 1, size);
		else
			System.arraycopy(init, start, buffer, 1, init.length - start);
	}

	public RBlock change(BigInteger E, BigInteger N) {
		final BigInteger big = new BigInteger(buffer);
		final BigInteger changed = big.modPow(E, N);
		return new RBlock(changed.toByteArray());
	}

	public byte[] getData(int size) {
		if (buffer.length == size)
			return buffer;

		final byte[] result = new byte[size];
		System.arraycopy(buffer, buffer.length - size, result, 0, size);
		return result;
	}

}
