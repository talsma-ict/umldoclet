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
package net.sourceforge.plantuml.activitydiagram3.ftile;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class Genealogy {

	private Map<Ftile, Ftile> myFatherIs = new HashMap<Ftile, Ftile>();
	private final Ftile root;

	public Genealogy(Ftile root) {
		this.root = root;
		process(root);
		// System.err.println("myFatherIs=" + myFatherIs);
	}

	private void process(Ftile current) {
		final Collection<Ftile> children = current.getMyChildren();
		// System.err.println("current=" + current);
		// System.err.println("children=" + children);
		for (Ftile child : children) {
			setMyFather(child, current);
			process(child);
		}
	}

	public Ftile getMyFather(Ftile me) {
		return myFatherIs.get(me);
	}

	private void setMyFather(Ftile child, Ftile father) {
		if (myFatherIs.containsKey(child)) {
			throw new IllegalArgumentException();
		}
		myFatherIs.put(child, father);
	}

	public UTranslate getTranslate(Ftile child, StringBounder stringBounder) {
		Ftile current = child;
		UTranslate result = new UTranslate();
		while (current != root) {
			final Ftile father = getMyFather(current);
			final UTranslate tr = father.getTranslateFor(current, stringBounder);
//			System.err.println("Father=" + father);
//			System.err.println("current=" + current);
//			System.err.println("TR=" + tr);
			result = tr.compose(result);
			current = father;
		}
		return result;
	}

}
