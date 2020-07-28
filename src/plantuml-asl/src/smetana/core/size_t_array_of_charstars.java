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

import smetana.core.amiga.StarArrayOfCString;


public class size_t_array_of_charstars implements size_t {

	final private int size;

	public size_t_array_of_charstars(int size) {
		this.size = size;
	}

	public boolean isZero() {
		return size == 0;
	}
	
	public int getInternalNb() {
		return size;
	}


	public __ptr__ malloc() {
		return new StarArrayOfCString(__array_of_cstring_impl__.mallocStarChar(size));
		// return new StarArray(__array__.mallocStarChar(size));
		// return AreaArray.mallocStarChar(size);
	}

	public size_t negate() {
		throw new UnsupportedOperationException();
	}

	public size_t plus(int length) {
		throw new UnsupportedOperationException();
	}

	public boolean isStrictPositive() {
		throw new UnsupportedOperationException();
	}

	public boolean isStrictNegative() {
		throw new UnsupportedOperationException();
	}

	public __ptr__ realloc(Object old) {
		((StarArrayOfCString) old).realloc(size);
		return (__ptr__) old;
	}

}
