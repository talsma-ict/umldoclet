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
package net.sourceforge.plantuml.hector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class PinLinksContinuousSet {

	private final Collection<PinLink> all = new ArrayList<PinLink>();

	public Skeleton createSkeleton() {
		final GrowingTree tree = new GrowingTree();
		final Collection<PinLink> pendings = new ArrayList<PinLink>(all);
		while (pendings.size() > 0) {
			for (Iterator<PinLink> it = pendings.iterator(); it.hasNext();) {
				final PinLink candidat = it.next();
				if (tree.canBeAdded(candidat)) {
					tree.add(candidat);
					it.remove();
				}
			}
		}
		return tree.createSkeleton();

	}

	public void add(PinLink newPinLink) {
		if (all.size() == 0) {
			all.add(newPinLink);
			return;
		}
		if (all.contains(newPinLink)) {
			throw new IllegalArgumentException("already");
		}
		for (PinLink aLink : all) {
			if (newPinLink.doesTouch(aLink)) {
				all.add(newPinLink);
				return;
			}
		}
		throw new IllegalArgumentException("not connex");
	}

	public void addAll(PinLinksContinuousSet other) {
		if (doesTouch(other) == false) {
			throw new IllegalArgumentException();
		}
		this.all.addAll(other.all);
	}

	public boolean doesTouch(PinLink other) {
		for (PinLink aLink : all) {
			if (other.doesTouch(aLink)) {
				return true;
			}
		}
		return false;
	}

	public boolean doesTouch(PinLinksContinuousSet otherSet) {
		for (PinLink otherLink : otherSet.all) {
			if (doesTouch(otherLink)) {
				return true;
			}
		}
		return false;
	}

}
