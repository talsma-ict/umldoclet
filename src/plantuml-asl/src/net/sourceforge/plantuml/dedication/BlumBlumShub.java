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
package net.sourceforge.plantuml.dedication;

import java.math.BigInteger;

public class BlumBlumShub {

	private static final BigInteger two = BigInteger.valueOf(2L);

	private BigInteger state;
	private final BigInteger pq;

	public BlumBlumShub(BigInteger pq, byte[] seed) {
		this.pq = pq;
		this.state = new BigInteger(1, seed).mod(pq);
	}

	public int nextRnd(int numBits) {
		int result = 0;
		for (int i = numBits; i != 0; --i) {
			state = state.modPow(two, pq);
			final int bit = state.testBit(0) ? 1 : 0;
			result = (result << 1) | bit;
		}
		return result;
	}

}
