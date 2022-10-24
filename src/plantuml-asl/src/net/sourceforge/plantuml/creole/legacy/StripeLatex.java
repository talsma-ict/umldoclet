/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2023, Arnaud Roques
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
package net.sourceforge.plantuml.creole.legacy;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.creole.Parser;
import net.sourceforge.plantuml.creole.atom.Atom;
import net.sourceforge.plantuml.creole.atom.AtomMath;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.math.ScientificEquationSafe;
import net.sourceforge.plantuml.ugraphic.UGraphic;

public class StripeLatex implements StripeRaw {

	final private FontConfiguration fontConfiguration;
	final private StringBuilder formula = new StringBuilder();
	private AtomMath atom;

	private boolean terminated;

	public StripeLatex(FontConfiguration fontConfiguration) {
		this.fontConfiguration = fontConfiguration;
	}

	public List<Atom> getAtoms() {
		return Collections.<Atom>singletonList(this);
	}

	public Atom getLHeader() {
		return null;
	}

	@Override
	public boolean addAndCheckTermination(String line) {
		if (Parser.isLatexEnd(line)) {
			this.terminated = true;
			return true;
		}
		this.formula.append(line);
		return false;
	}

	@Override
	public final boolean isTerminated() {
		return terminated;
	}

	private Atom getAtom() {
		if (atom == null) {
			final ScientificEquationSafe math = ScientificEquationSafe.fromLatex(formula.toString());
			atom = new AtomMath(math, fontConfiguration.getColor(), fontConfiguration.getExtendedColor());
		}
		return atom;
	}

	public XDimension2D calculateDimension(StringBounder stringBounder) {
		return getAtom().calculateDimension(stringBounder);
	}

	public double getStartingAltitude(StringBounder stringBounder) {
		return 0;
	}

	public void drawU(UGraphic ug) {
		getAtom().drawU(ug);
	}

	public List<Atom> splitInTwo(StringBounder stringBounder, double width) {
		return Arrays.asList((Atom) this);
	}

}
