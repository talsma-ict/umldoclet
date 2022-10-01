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
package net.sourceforge.plantuml.ebnf;

import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UPath;

public class HLineCurved implements UDrawable {

	private final double height;
	private final double delta;

	public HLineCurved(double height, double delta) {
		this.height = height;
		this.delta = delta;
	}

	@Override
	public void drawU(UGraphic ug) {
		if (delta == 0) {
			ug.draw(ULine.vline(height));
			return;
		}
		final UPath path = new UPath();
		path.moveTo(-delta, 0);

		final double a = delta / 4;
		path.cubicTo(-a, 0, 0, Math.abs(a), 0, Math.abs(delta));
		// path.lineTo(0, delta);

		path.lineTo(0, height - Math.abs(delta));

		path.cubicTo(0, height - a, a, height, delta, height);
		// path.lineTo(delta, height);

		ug.draw(path);
	}

}
