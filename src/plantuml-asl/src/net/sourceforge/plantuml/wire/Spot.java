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
package net.sourceforge.plantuml.wire;

import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UShape;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;

public class Spot {

	private final WBlock block;
	private final HColor color;
	private final String x;
	private final String y;

	public Spot(WBlock block, HColor color, String x, String y) {
		this.block = block;
		this.color = color == null ? HColorUtils.RED : color;
		this.x = x == null ? "0" : x;
		this.y = y == null ? "0" : y;
	}

	public void drawMe(UGraphic ug) {

		final UTranslate pos = block.getAbsolutePosition(x, y);
		final UTranslate tr = pos.compose(new UTranslate(-2, -2));
		final UShape circle = new UEllipse(5, 5);

		ug = ug.apply(color).apply(color.bg());
		ug.apply(tr).draw(circle);

	}

}
