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
package net.sourceforge.plantuml.bpm;

public final class Navigators {

	private Navigators() {

	}

	public static <O> Navigator<O> iterate(final Chain<O> orig, final O from, final O to) {
		if (orig.compare(from, to) <= 0) {
			return orig.navigator(from);
		}
		return reverse(orig.navigator(from));
	}

	public static <O> Navigator<O> reverse(final Navigator<O> orig) {
		return new Navigator<O>() {

			public O next() {
				return orig.previous();
			}

			public O previous() {
				return orig.next();
			}

			public O get() {
				return orig.get();
			}

			public void set(O data) {
				orig.set(data);
			}

			public void insertBefore(O data) {
				throw new UnsupportedOperationException();
			}

			public void insertAfter(O data) {
				throw new UnsupportedOperationException();
			}
		};
	}

}
