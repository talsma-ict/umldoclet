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
package net.sourceforge.plantuml.wbs;

import net.sourceforge.plantuml.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.activitydiagram3.ftile.vertical.FtileBoxOld;
import net.sourceforge.plantuml.creole.CreoleMode;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.mindmap.IdeaShape;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.ugraphic.UGraphic;

class ITFLeaf extends AbstractTextBlock implements ITF {

	private final TextBlock box;

	public ITFLeaf(Style style, ISkinParam skinParam, Display label, IdeaShape shape) {
		if (shape == IdeaShape.BOX) {
			this.box = FtileBoxOld.createWbs(style, skinParam, label);
		} else {
			final TextBlock text = label.create0(
					style.getFontConfiguration(skinParam.getThemeStyle(), skinParam.getIHtmlColorSet()),
					style.getHorizontalAlignment(), skinParam, style.wrapWidth(), CreoleMode.FULL, null, null);
			this.box = TextBlockUtils.withMargin(text, 0, 3, 1, 1);
		}
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		return box.calculateDimension(stringBounder);
	}

	public void drawU(UGraphic ug) {
		box.drawU(ug);
	}

	public Point2D getT1(StringBounder stringBounder) {
		final Dimension2D dim = calculateDimension(stringBounder);
		return new Point2D.Double(dim.getWidth() / 2, 0);
	}

	public Point2D getT2(StringBounder stringBounder) {
		final Dimension2D dim = calculateDimension(stringBounder);
		return new Point2D.Double(dim.getWidth() / 2, dim.getHeight());
	}

	public Point2D getF1(StringBounder stringBounder) {
		final Dimension2D dim = calculateDimension(stringBounder);
		return new Point2D.Double(0, dim.getHeight() / 2);
	}

	public Point2D getF2(StringBounder stringBounder) {
		final Dimension2D dim = calculateDimension(stringBounder);
		return new Point2D.Double(dim.getWidth(), dim.getHeight() / 2);
	}

}
