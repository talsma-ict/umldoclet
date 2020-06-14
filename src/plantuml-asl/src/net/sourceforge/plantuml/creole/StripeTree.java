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
package net.sourceforge.plantuml.creole;

import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.graphic.FontConfiguration;

public class StripeTree implements Stripe {

	private FontConfiguration fontConfiguration;
	final private ISkinSimple skinParam;
	final private AtomTree tree;
	final private Atom marged;
	final private StripeStyle stripeStyle = new StripeStyle(StripeStyleType.TREE, 0, '\0');

	public StripeTree(FontConfiguration fontConfiguration, ISkinSimple skinParam, String line) {
		this.fontConfiguration = fontConfiguration;
		this.skinParam = skinParam;
		this.tree = new AtomTree(fontConfiguration.getColor());
		this.marged = new AtomWithMargin(tree, 2, 2);
		analyzeAndAdd(line);
	}

	public List<Atom> getAtoms() {
		return Collections.<Atom> singletonList(marged);
	}
	
	public Atom getHeader() {
		return null;
	}

	public void analyzeAndAdd(String line) {
		final List<String> lines = StripeTable.getWithNewlinesInternal(line);
		for (String s : lines) {
			final StripeSimple cell = new StripeSimple(fontConfiguration, stripeStyle, new CreoleContext(), skinParam,
					CreoleMode.FULL);
			// EMTEC
			final String text = s.replaceFirst("^\\s*\\|_", "");
			final int level = (s.length() - text.length()) / 2;
			cell.analyzeAndAdd(text);
			this.tree.addCell(StripeTable.asAtom(Collections.singletonList(cell), 0), level);
		}

	}

}
