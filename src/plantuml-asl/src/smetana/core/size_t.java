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

/**
 * "Pseudo size" of a C structure. In C, this is the actual size of the structure. In Java, this is an indication to
 * know which structure we are going to allocate.
 * 
 * @author Arnaud Roques
 * 
 */
public class size_t {

	public final Class tobeAllocated;
	
	@Override
	public String toString() {
		return super.toString()+" "+tobeAllocated;
	}

	public size_t(Class tobeAllocated) {
		this.tobeAllocated = tobeAllocated;
	}


	public size_t negate() {
		throw new UnsupportedOperationException();
	}


	public size_t multiply(int sz) {
		throw new UnsupportedOperationException();
	}

	public boolean isStrictPositive() {
		return true;
	}

	public boolean isStrictNegative() {
		throw new UnsupportedOperationException();
	}



	public final Class getTobeAllocated() {
		return tobeAllocated;
	}

	public __ptr__ malloc() {
		if (tobeAllocated != null) {
			return Memory.malloc(tobeAllocated);
		}
		throw new UnsupportedOperationException();
	}

	public size_t plus(int strlen) {
		throw new UnsupportedOperationException();
	}

	public boolean isZero() {
		return false;
	}

	
	public __ptr__ realloc(Object old) {
		throw new UnsupportedOperationException();
	}


	public int getInternalNb() {
		throw new UnsupportedOperationException();
	}


}
