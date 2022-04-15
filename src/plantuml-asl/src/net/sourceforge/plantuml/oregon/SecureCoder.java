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
package net.sourceforge.plantuml.oregon;

public class SecureCoder {

	private static final int m[] = { 38, 152, 264, 491, 573, 616, 727, 880, 905, 1090, 1188, 1315, 1544, 1603, 1756,
			1831, 1962, 2025, 2100, 2257, 2381, 2469, 2536, 2714, 2948, 3077, 3166, 3219, 3340, 3455, 3701, 3892, 3934,
			4193, 4372, 4404, 4521, 4650, 4739, 4865, 4987, 5053, 5135, 5282, 5309, 5446, 5628, 5817, 5970, 6002, 6174,
			6295, 6367, 6420, 6558, 6689, 6913, 7061, 7129, 7206, 7333, 7510, 7697, 7742, 7854, 8084, 8147, 8230, 8326,
			8412, 8599, 8675, 8763, 8808, 8951, 9049, 9111, 9223, 9394, 9478, 9507, 9632, 9785 };

	private static final int dec[] = new int[10000];

	static {
		for (int i = 0; i < dec.length; i++) {
			dec[i] = -1;
		}
		for (int i = 0; i < m.length; i++) {
			final int enc = m[i];
			dec[enc] = i;
			for (int n : MagicTable.getNeighbours(enc)) {
				if (dec[n] != -1) {
					throw new IllegalStateException();
				}
				dec[n] = i + 1000;
			}
		}
	}

	public int encode(int i) {
		return m[i];
	}

	public int decode(int v) {
		return dec[v];
	}

}
