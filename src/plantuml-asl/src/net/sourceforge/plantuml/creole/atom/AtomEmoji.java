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
import net.sourceforge.plantuml.emoji.Emoji;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class AtomEmoji extends AbstractAtom implements Atom {

	private static final double MAGIC = 24.0;
	private final Emoji emoji;
	private final double factor;
	private final HColor color;

	public AtomEmoji(Emoji emoji, double scale, double size2D, HColor color) {
		this.emoji = emoji;
		this.factor = scale * size2D / MAGIC;
		this.color = color;
	}

	public XDimension2D calculateDimension(StringBounder stringBounder) {
		final double size = 36 * factor;
		return new XDimension2D(size, size);
	}

	public double getStartingAltitude(StringBounder stringBounder) {
		return -3 * factor;
	}

	public void drawU(UGraphic ug) {
		emoji.drawU(ug, this.factor, this.color);
	}

}
