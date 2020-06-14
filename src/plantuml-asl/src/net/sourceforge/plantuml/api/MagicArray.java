/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
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
package net.sourceforge.plantuml.api;

public final class MagicArray {

	private final int data[];
	private final int size;
	private long lastUpdatedKey = -1;
	private int lastUpdatedValue;
	private long sum;
	private long maxSum;

	public MagicArray(int size) {
		this.data = new int[size];
		this.size = size;
	}

	synchronized public void incKey(long key) {
		incKey(key, 1);
	}

	synchronized public void incKey(long key, int delta) {
		if (key < lastUpdatedKey) {
			return;
		}
		if (key != lastUpdatedKey) {
			if (lastUpdatedKey != -1) {
				setValue(lastUpdatedKey, lastUpdatedValue);
				for (long i = lastUpdatedKey + 1; i < key; i++) {
					setValue(i, 0);
				}
			}
			lastUpdatedValue = 0;
			lastUpdatedKey = key;
		}
		lastUpdatedValue += delta;
	}

	private void setValue(long key, int value) {
		final int i = (int) (key % size);
		sum += value - data[i];
		if (sum > maxSum) {
			maxSum = sum;
		}
		data[i] = value;
	}

	synchronized public long getSum() {
		return sum;
	}

	synchronized public long getMaxSum() {
		return maxSum;
	}

	private long getSumSlow() {
		long tmp = 0;
		for (int d : data) {
			tmp += d;
		}
		return tmp;
	}

}
