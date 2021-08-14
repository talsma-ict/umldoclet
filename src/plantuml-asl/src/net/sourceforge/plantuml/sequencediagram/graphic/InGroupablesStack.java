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
package net.sourceforge.plantuml.sequencediagram.graphic;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.sequencediagram.InGroupable;
import net.sourceforge.plantuml.sequencediagram.InGroupableList;

class InGroupablesStack {

	final private List<InGroupableList> inGroupableStack = new ArrayList<>();

	public void addList(InGroupableList inGroupableList) {
		for (InGroupableList other : inGroupableStack) {
			other.addInGroupable(inGroupableList);
		}
		inGroupableStack.add(inGroupableList);

	}

	public void pop() {
		final int idx = inGroupableStack.size() - 1;
		inGroupableStack.remove(idx);
	}

	public void addElement(InGroupable inGroupable) {
		for (InGroupableList groupingStructure : inGroupableStack) {
			groupingStructure.addInGroupable(inGroupable);
		}
	}

	public InGroupableList getTopGroupingStructure() {
		if (inGroupableStack.size() == 0) {
			return null;
		}
		return inGroupableStack.get(inGroupableStack.size() - 1);
	}

}
