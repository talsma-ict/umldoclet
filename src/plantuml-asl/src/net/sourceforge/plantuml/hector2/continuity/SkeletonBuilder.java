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
package net.sourceforge.plantuml.hector2.continuity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.cucadiagram.Link;

public class SkeletonBuilder {

	private List<Skeleton> all = new ArrayList<Skeleton>();

	public void add(Link link) {
		addInternal(link);
		do {
			final boolean changed = merge();
			if (changed == false) {
				return;
			}
		} while (true);

	}

	private boolean merge() {
		for (int i = 0; i < all.size() - 1; i++) {
			for (int j = i + 1; j < all.size(); j++) {
				if (all.get(i).doesTouch(all.get(j))) {
					all.get(i).addAll(all.get(j));
					all.remove(j);
					return true;
				}
			}
		}
		return false;
	}

	private void addInternal(Link link) {
		for (Skeleton skeleton : all) {
			if (skeleton.doesTouch(link)) {
				skeleton.add(link);
				return;
			}
		}
		final Skeleton newSkeleton = new Skeleton();
		newSkeleton.add(link);
		all.add(newSkeleton);
	}

	public List<Skeleton> getSkeletons() {
		return Collections.unmodifiableList(all);
	}
}
