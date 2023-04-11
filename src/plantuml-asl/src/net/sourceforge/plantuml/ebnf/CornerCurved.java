/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
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

import net.sourceforge.plantuml.klimt.CopyForegroundColorToBackgroundColor;
import net.sourceforge.plantuml.klimt.UPath;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.shape.UDrawable;

enum CornerType {
	NW, NE, SE, SW;
}

public class CornerCurved implements UDrawable {

	private final double delta;
	private final CornerType type;
	private final boolean withArrow;

	private CornerCurved(CornerType type, double delta, boolean withArrow) {
		this.delta = delta;
		this.type = type;
		this.withArrow = withArrow;
		if (delta <= 0)
			throw new IllegalArgumentException();
	}

	public static UDrawable createSW(double delta) {
		return new CornerCurved(CornerType.SW, delta, false);
	}

	public static UDrawable createSE(double delta) {
		return new CornerCurved(CornerType.SE, delta, false);
	}

	public static UDrawable createNE(double delta) {
		return new CornerCurved(CornerType.NE, delta, false);
	}

	public static UDrawable createNE_arrow(double delta) {
		return new CornerCurved(CornerType.NE, delta, true);
	}

	public static UDrawable createNW(double delta) {
		return new CornerCurved(CornerType.NW, delta, false);
	}

	public static UDrawable createNW_arrow(double delta) {
		return new CornerCurved(CornerType.NW, delta, true);
	}

	@Override
	public void drawU(UGraphic ug) {
		final UPath path = UPath.none();
		final double a = delta / 4;

		switch (type) {
		case SW:
			path.moveTo(0, -delta);
			path.cubicTo(0, -a, a, 0, delta, 0);
			break;
		case SE:
			path.moveTo(0, -delta);
			path.cubicTo(0, -a, -a, 0, -delta, 0);
			break;
		case NE:
			path.moveTo(-delta, 0);
			path.cubicTo(-a, 0, 0, a, 0, delta);
			if (withArrow)
				ug.apply(new CopyForegroundColorToBackgroundColor()).apply(UTranslate.dy(delta - 5))
						.draw(ETile.getArrowToBottom());
			break;
		case NW:
			path.moveTo(0, delta);
			path.cubicTo(0, a, a, 0, delta, 0);
			if (withArrow)
				ug.apply(new CopyForegroundColorToBackgroundColor()).apply(UTranslate.dy(delta))
						.draw(ETile.getArrowToTop());
			break;
		}

		ug.draw(path);
	}

}
