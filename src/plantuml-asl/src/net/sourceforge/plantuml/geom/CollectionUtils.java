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
package net.sourceforge.plantuml.geom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CollectionUtils {

	public static <E> Collection<List<E>> selectUpTo(List<E> original, int nb) {
		final List<List<E>> result = new ArrayList<List<E>>();
		for (int i = 1; i <= nb; i++) {
			result.addAll(selectExactly(original, i));
		}
		return Collections.unmodifiableList(result);
	}

	public static <E> Collection<List<E>> selectExactly(List<E> original, int nb) {
		if (nb < 0) {
			throw new IllegalArgumentException();
		}
		if (nb == 0) {
			return Collections.emptyList();
		}
		if (nb == 1) {
			final List<List<E>> result = new ArrayList<List<E>>();
			for (E element : original) {
				result.add(Collections.singletonList(element));
			}
			return result;

		}
		if (nb > original.size()) {
			return Collections.emptyList();
		}
		if (nb == original.size()) {
			return Collections.singletonList(original);
		}
		final List<List<E>> result = new ArrayList<List<E>>();

		for (List<E> subList : selectExactly(original.subList(1, original.size()), nb - 1)) {
			final List<E> newList = new ArrayList<E>();
			newList.add(original.get(0));
			newList.addAll(subList);
			result.add(Collections.unmodifiableList(newList));
		}
		result.addAll(selectExactly(original.subList(1, original.size()), nb));

		return Collections.unmodifiableList(result);
	}
}
