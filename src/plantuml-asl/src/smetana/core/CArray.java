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

package smetana.core;

import java.util.ArrayList;
import java.util.List;

public class CArray<O> extends UnsupportedC {

	private final Class<O> cl;
	private final List<O> data;
	private final int offset;

	@Override
	public String toString() {
		return "Array " + cl + " offset=" + offset + " [" + data.size() + "]" + data;
	}

	public static <O> CArray<O> ALLOC__(int size, Class<O> cl) {
		final CArray<O> result = new CArray<O>(new ArrayList<O>(), 0, cl);
		result.reallocWithStructure(size);
		return result;
	}

	public static <O> CArray<O> REALLOC__(int size, CArray<O> old, Class<O> cl) {
		if (old == null) {
			return ALLOC__(size, cl);
		}
		old.reallocWithStructure(size);
		return old;
	}

	private CArray(List<O> data, int offset, Class<O> cl) {
		if (offset > 0) {
			// JUtilsDebug.LOG("offset=" + offset);
		}
		this.data = data;
		this.offset = offset;
		this.cl = cl;
	}

	public CArray<O> plus_(int delta) {
		return new CArray<O>(data, offset + delta, cl);
	}

	public int minus_(CArray<O> other) {
		if (this.data != other.data) {
			throw new IllegalArgumentException();
		}
		return this.offset - other.offset;
	}

	public O get__(int i) {
		return data.get(i + offset);
	}

	private void reallocWithStructure(int size) {
		if (offset != 0) {
			throw new IllegalStateException();
		}
		try {
			for (int i = 0; i < size; i++) {
				data.add(cl.getDeclaredConstructor().newInstance());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new UnsupportedOperationException();
		}
	}

}
