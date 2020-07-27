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

package smetana.core;

import smetana.core.amiga.Area;

public class ArrayOfInteger implements __array_of_integer__ {

	private final int[] data;
	private final int position;

	public ArrayOfInteger(int[] data, int position) {
		this.data = data;
		this.position = position;

	}

	public String getUID36() {
		throw new UnsupportedOperationException();
	}

	public void swap(int i, int j) {
		throw new UnsupportedOperationException();
	}

	public void realloc(int nb) {
		throw new UnsupportedOperationException();
	}

	public int comparePointerInternal(__array_of_integer__ other) {
		throw new UnsupportedOperationException();
	}

	public final __array_of_integer__ move(int delta) {
		return plus(delta);
	}

	public __array_of_integer__ plus(int delta) {
		return new ArrayOfInteger(data, position + delta);
	}

	public Area getInternal(int idx) {
		throw new UnsupportedOperationException();
	}

	public void memcopyFrom(Area source) {
		throw new UnsupportedOperationException();
	}

	public int getInt() {
		return data[position];
	}

	public void setInt(int value) {
		data[position] = value;
	}

}
