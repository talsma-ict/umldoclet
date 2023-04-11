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
package net.sourceforge.plantuml.svek;

import net.sourceforge.plantuml.klimt.UPath;
import net.sourceforge.plantuml.klimt.UShape;
import net.sourceforge.plantuml.klimt.UStroke;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.shape.UDrawable;
import net.sourceforge.plantuml.klimt.shape.URectangle;

public final class RoundedSouth implements UDrawable {

	private final double width;
	private final double height;
	private final HColor backColor;
	private final double rounded;

	public RoundedSouth(double width, double height, HColor backColor, double rounded) {
		if (width == 0)
			throw new IllegalArgumentException();
		if (height == 0)
			throw new IllegalArgumentException();

		this.width = width;
		this.height = height;
		this.rounded = rounded;
		this.backColor = backColor;
	}

	public void drawU(UGraphic ug) {
		if (backColor.isTransparent())
			return;

		final UShape header;
		if (rounded == 0) {
			header = URectangle.build(width, height);
		} else {
			final UPath path = UPath.none();
			path.moveTo(0, 0);
			path.lineTo(width, 0);
			path.lineTo(width, height - rounded / 2);
			path.arcTo(rounded / 2, rounded / 2, 0, 0, 1, width - rounded / 2, height);
			path.lineTo(rounded / 2, height);
			path.arcTo(rounded / 2, rounded / 2, 0, 0, 1, 0, height - rounded / 2);
			path.lineTo(0, 0);
			path.closePath();
			header = path;
		}
		ug.apply(UStroke.simple()).apply(backColor).apply(backColor.bg()).draw(header);

	}
}
