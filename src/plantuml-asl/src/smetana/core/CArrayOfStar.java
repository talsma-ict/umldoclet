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

package smetana.core;

import com.plantuml.api.cheerpj.WasmLog;

final public class CArrayOfStar<O> extends UnsupportedC {

	private final Object[] data;
	private final int offset;

	@Override
	public String toString() {
		return "*Array offset=" + offset + " [" + data.length + "]" + data;
	}

	private CArrayOfStar(Object data[], int offset) {
		this.data = data;
		this.offset = offset;
	}

	public static <O> CArrayOfStar<O> ALLOC(int size, ZType type) {
		final CArrayOfStar<O> result = new CArrayOfStar<O>(new Object[size], 0);
		return result;
	}

	public static <O> CArrayOfStar<O> REALLOC(int size, CArrayOfStar<O> old, ZType type) {
		if (old == null)
			return ALLOC(size, type);

		if (size <= old.data.length)
			return old;

		if (old.offset != 0)
			throw new IllegalStateException();

		WasmLog.log("Realloc* from " + old.data.length + " to " + size);

		final CArrayOfStar<O> result = ALLOC(size, type);
		System.arraycopy(old.data, 0, result.data, 0, old.data.length);
		return result;
	}

	public int comparePointer_(CArrayOfStar<O> other) {
		if (this.data != other.data)
			throw new IllegalArgumentException();

		return this.offset - other.offset;
	}

	public O get_(int i) {
		return (O) data[i + offset];
	}

	public void set_(int i, O value) {
		data[i + offset] = value;
	}

	public CArrayOfStar<O> plus_(int delta) {
		return new CArrayOfStar<O>(data, offset + delta);
	}

	public void _swap(int i, int j) {
		if (offset != 0)
			throw new IllegalStateException();

		final Object tmp = data[i];
		data[i] = data[j];
		data[j] = tmp;
	}

}
