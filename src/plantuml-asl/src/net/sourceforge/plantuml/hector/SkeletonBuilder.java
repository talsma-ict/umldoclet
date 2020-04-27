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
import java.util.Collections;
import java.util.List;

public class SkeletonBuilder {

	private List<PinLinksContinuousSet> sets = new ArrayList<PinLinksContinuousSet>();

	public void add(PinLink pinLink) {
		addInternal(pinLink);
		merge();

	}

	private void merge() {
		for (int i = 0; i < sets.size() - 1; i++) {
			for (int j = i + 1; j < sets.size(); j++) {
				if (sets.get(i).doesTouch(sets.get(j))) {
					sets.get(i).addAll(sets.get(j));
					sets.remove(j);
					return;
				}
			}
		}
	}

	private void addInternal(PinLink pinLink) {
		for (PinLinksContinuousSet set : sets) {
			if (set.doesTouch(pinLink)) {
				set.add(pinLink);
				return;
			}
		}
		final PinLinksContinuousSet newSet = new PinLinksContinuousSet();
		newSet.add(pinLink);
		sets.add(newSet);
	}

	public List<Skeleton> createSkeletons() {
		final List<Skeleton> result = new ArrayList<Skeleton>();

		for (PinLinksContinuousSet set : sets) {
			result.add(set.createSkeleton());
		}

		return Collections.unmodifiableList(result);
	}
}
