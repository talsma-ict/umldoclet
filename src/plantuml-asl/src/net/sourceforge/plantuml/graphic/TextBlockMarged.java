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
package net.sourceforge.plantuml.graphic;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.svek.Ports;
import net.sourceforge.plantuml.svek.WithPorts;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

class TextBlockMarged extends AbstractTextBlock implements TextBlock, WithPorts {

	private final TextBlock textBlock;
	private final double x1;
	private final double x2;
	private final double y1;
	private final double y2;

	TextBlockMarged(TextBlock textBlock, double x1, double x2, double y1, double y2) {
		this.textBlock = textBlock;
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		final Dimension2D dim = textBlock.calculateDimension(stringBounder);
		return Dimension2DDouble.delta(dim, x1 + x2, y1 + y2);
	}

	public void drawU(UGraphic ug) {
		final UTranslate translate = new UTranslate(x1, y1);
		textBlock.drawU(ug.apply(translate));
	}

	@Override
	public Rectangle2D getInnerPosition(String member, StringBounder stringBounder, InnerStrategy strategy) {
		final Rectangle2D parent = textBlock.getInnerPosition(member, stringBounder, strategy);
		if (parent == null) {
			return null;
		}
		final UTranslate translate = new UTranslate(x1, y1);
		return translate.apply(parent);
	}

	public Ports getPorts(StringBounder stringBounder) {
		return ((WithPorts) textBlock).getPorts(stringBounder).translateY(y1);
	}

}
