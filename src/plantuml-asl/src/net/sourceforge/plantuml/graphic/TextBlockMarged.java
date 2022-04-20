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

import net.sourceforge.plantuml.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.style.ClockwiseTopRightBottomLeft;
import net.sourceforge.plantuml.svek.Ports;
import net.sourceforge.plantuml.svek.WithPorts;
import net.sourceforge.plantuml.ugraphic.UEmpty;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

class TextBlockMarged extends AbstractTextBlock implements TextBlock, WithPorts {

	private final TextBlock textBlock;
	private final double top;
	private final double right;
	private final double bottom;
	private final double left;

	TextBlockMarged(TextBlock textBlock, double top, double right, double bottom, double left) {
		this.textBlock = textBlock;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		this.left = left;
	}

	TextBlockMarged(TextBlock textBlock, ClockwiseTopRightBottomLeft margins) {
		this.textBlock = textBlock;
		this.top = margins.getTop();
		this.right = margins.getRight();
		this.bottom = margins.getBottom();
		this.left = margins.getLeft();
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		final Dimension2D dim = textBlock.calculateDimension(stringBounder);
		return Dimension2DDouble.delta(dim, left + right, top + bottom);
	}

	public void drawU(UGraphic ug) {
		// ug.apply(HColorUtils.BLUE).draw(new
		// URectangle(calculateDimension(ug.getStringBounder())));
		final Dimension2D dim = calculateDimension(ug.getStringBounder());
		if (dim.getWidth() > 0) {
			ug.draw(new UEmpty(dim));
			final UTranslate translate = new UTranslate(left, top);
			textBlock.drawU(ug.apply(translate));
		}
	}

	@Override
	public Rectangle2D getInnerPosition(String member, StringBounder stringBounder, InnerStrategy strategy) {
		final Rectangle2D parent = textBlock.getInnerPosition(member, stringBounder, strategy);
		if (parent == null) {
			return null;
		}
		final UTranslate translate = new UTranslate(left, top);
		return translate.apply(parent);
	}

	@Override
	public Ports getPorts(StringBounder stringBounder) {
		return ((WithPorts) textBlock).getPorts(stringBounder).translateY(top);
	}

}
