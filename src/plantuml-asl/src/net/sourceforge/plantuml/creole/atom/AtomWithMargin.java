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
package net.sourceforge.plantuml.creole.atom;

import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class AtomWithMargin extends AbstractAtom implements Atom {

	private final double marginY1;
	private final double marginY2;
	private final Atom atom;

	public AtomWithMargin(Atom atom, double marginY1, double marginY2) {
		this.atom = atom;
		this.marginY1 = marginY1;
		this.marginY2 = marginY2;
	}

	public XDimension2D calculateDimension(StringBounder stringBounder) {
		return atom.calculateDimension(stringBounder).delta(0, marginY1 + marginY2);
	}

	public double getStartingAltitude(StringBounder stringBounder) {
		return atom.getStartingAltitude(stringBounder);
	}

	public void drawU(UGraphic ug) {
		atom.drawU(ug.apply(UTranslate.dy(marginY1)));
	}

}
