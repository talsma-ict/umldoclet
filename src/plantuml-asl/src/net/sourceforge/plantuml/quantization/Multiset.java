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
package net.sourceforge.plantuml.quantization;

import java.util.Collection;
import java.util.Set;

/**
 * A collection which permits duplicates, and provides methods adding/removing
 * several counts of an element.
 *
 * @param <E> the element type
 */
public interface Multiset<E> extends Collection<E> {
	/**
	 * Add n counts of an element.
	 *
	 * @param element the element to add
	 * @param n       how many to add
	 */
	public void add(E element, int n);

	/**
	 * Remove up to n counts of an element.
	 *
	 * @param element the element the remove
	 * @param n       how many to remove
	 * @return the number of elements removed
	 */
	public int remove(Object element, int n);

	public int count(Object element);

	public Set<E> getDistinctElements();
}
