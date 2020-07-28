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
package net.sourceforge.plantuml.hector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class SkeletonConfigurationSet implements Iterable<SkeletonConfiguration> {

	private final List<SkeletonConfiguration> all;
	private final SkeletonConfigurationComparator comparator;
	private final int limitSize;

	public SkeletonConfigurationSet(int limitSize, SkeletonConfigurationEvaluator evaluator) {
		this.comparator = new SkeletonConfigurationComparator(evaluator);
		this.all = new ArrayList<SkeletonConfiguration>();
		this.limitSize = limitSize;
	}

	public void add(SkeletonConfiguration skeletonConfiguration) {
		this.all.add(skeletonConfiguration);
		sortAndTruncate();
	}

	public void addAll(Collection<SkeletonConfiguration> others) {
		all.addAll(others);
		sortAndTruncate();
	}

	private void sortAndTruncate() {
		Collections.sort(all, comparator);
		while (all.size() > limitSize) {
			all.remove(all.size() - 1);
		}
	}

	@Override
	public String toString() {
		return all.toString();
	}

	public int size() {
		return all.size();
	}

	public Iterator<SkeletonConfiguration> iterator() {
		return new ArrayList<SkeletonConfiguration>(all).iterator();
	}

	public SkeletonConfiguration first() {
		return all.get(0);
	}

}
