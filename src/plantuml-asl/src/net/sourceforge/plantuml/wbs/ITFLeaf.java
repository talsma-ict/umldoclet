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
package net.sourceforge.plantuml.wbs;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.activitydiagram3.ftile.BoxStyle;
import net.sourceforge.plantuml.activitydiagram3.ftile.vertical.FtileBox;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.mindmap.IdeaShape;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;

public class ITFLeaf extends AbstractTextBlock implements ITF {

	private final ISkinParam skinParam;
	private final TextBlock box;

	public ITFLeaf(ISkinParam skinParam, Display label, IdeaShape shape) {
		this.skinParam = skinParam;
		final UFont font = skinParam.getFont(null, false, FontParam.ACTIVITY);

		if (shape == IdeaShape.BOX) {
			this.box = new FtileBox(Colors.empty().mute(skinParam), label, font, null, BoxStyle.SDL_TASK);
		} else {
			final TextBlock text = label.create(FontConfiguration.blackBlueTrue(font), HorizontalAlignment.LEFT,
					skinParam);
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
