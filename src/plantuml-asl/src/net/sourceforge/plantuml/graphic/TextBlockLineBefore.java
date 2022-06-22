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
package net.sourceforge.plantuml.graphic;

import java.awt.geom.Rectangle2D;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.awt.geom.Dimension2D;
import net.sourceforge.plantuml.svek.Ports;
import net.sourceforge.plantuml.svek.WithPorts;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UHorizontalLine;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorNone;

public class TextBlockLineBefore extends AbstractTextBlock implements TextBlock, WithPorts {

	private final TextBlock textBlock;
	private final char separator;
	private final TextBlock title;
	private final double defaultThickness;

	public TextBlockLineBefore(double defaultThickness, TextBlock textBlock, char separator, TextBlock title) {
		this.textBlock = textBlock;
		this.separator = separator;
		this.title = title;
		this.defaultThickness = defaultThickness;
	}

	public TextBlockLineBefore(double defaultThickness, TextBlock textBlock, char separator) {
		this(defaultThickness, textBlock, separator, null);
	}

	public TextBlockLineBefore(double defaultThickness, TextBlock textBlock) {
		this(defaultThickness, textBlock, '\0');
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		final Dimension2D dim = textBlock.calculateDimension(stringBounder);
		if (title != null) {
			final Dimension2D dimTitle = title.calculateDimension(stringBounder);
			return Dimension2DDouble.atLeast(dim, dimTitle.getWidth() + 8, dimTitle.getHeight());
		}
		return dim;
	}

	public void drawU(UGraphic ug) {
		final HColor color = ug.getParam().getColor();
		if (title == null)
			UHorizontalLine.infinite(defaultThickness, 1, 1, separator).drawMe(ug);

		textBlock.drawU(ug);
		if (color == null)
			ug = ug.apply(new HColorNone());
		else
			ug = ug.apply(color);

		if (title != null)
			UHorizontalLine.infinite(defaultThickness, 1, 1, title, separator).drawMe(ug);

	}

	@Override
	public Rectangle2D getInnerPosition(String member, StringBounder stringBounder, InnerStrategy strategy) {
		return textBlock.getInnerPosition(member, stringBounder, strategy);
	}

	@Override
	public Ports getPorts(StringBounder stringBounder) {
		if (textBlock instanceof WithPorts)
			return ((WithPorts) textBlock).getPorts(stringBounder);
		return new Ports();
	}

}
