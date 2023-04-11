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
package net.sourceforge.plantuml.wbs;

import net.sourceforge.plantuml.activitydiagram3.ftile.vertical.FtileBoxOld;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.creole.CreoleMode;
import net.sourceforge.plantuml.klimt.creole.Display;
import net.sourceforge.plantuml.klimt.drawing.AbstractCommonUGraphic;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.klimt.geom.XPoint2D;
import net.sourceforge.plantuml.klimt.shape.AbstractTextBlock;
import net.sourceforge.plantuml.klimt.shape.TextBlock;
import net.sourceforge.plantuml.klimt.shape.TextBlockUtils;
import net.sourceforge.plantuml.mindmap.IdeaShape;
import net.sourceforge.plantuml.style.ISkinParam;
import net.sourceforge.plantuml.style.Style;

class ITFLeaf extends AbstractTextBlock implements ITF {

	private final TextBlock box;
	private final WElement idea;

	public ITFLeaf(WElement idea, ISkinParam skinParam) {
		final IdeaShape shape = idea.getShape();
		final Style style = idea.getStyle();
		final Display label = idea.getLabel();
		this.idea = idea;
		if (shape == IdeaShape.BOX) {
			this.box = FtileBoxOld.createWbs(style, skinParam, label);
		} else {
			final TextBlock text = label.create0(style.getFontConfiguration(skinParam.getIHtmlColorSet()),
					style.getHorizontalAlignment(), skinParam, style.wrapWidth(), CreoleMode.FULL, null, null);
			this.box = TextBlockUtils.withMargin(text, 0, 3, 1, 1);
		}
	}

	public XDimension2D calculateDimension(StringBounder stringBounder) {
		return box.calculateDimension(stringBounder);
	}

	public void drawU(UGraphic ug) {
		if (ug instanceof AbstractCommonUGraphic) {
			final UTranslate translate = ((AbstractCommonUGraphic) ug).getTranslate();
			idea.setGeometry(translate, calculateDimension(ug.getStringBounder()));
		}
		box.drawU(ug);
	}

	public XPoint2D getT1(StringBounder stringBounder) {
		final XDimension2D dim = calculateDimension(stringBounder);
		return new XPoint2D(dim.getWidth() / 2, 0);
	}

	public XPoint2D getT2(StringBounder stringBounder) {
		final XDimension2D dim = calculateDimension(stringBounder);
		return new XPoint2D(dim.getWidth() / 2, dim.getHeight());
	}

	public XPoint2D getF1(StringBounder stringBounder) {
		final XDimension2D dim = calculateDimension(stringBounder);
		return new XPoint2D(0, dim.getHeight() / 2);
	}

	public XPoint2D getF2(StringBounder stringBounder) {
		final XDimension2D dim = calculateDimension(stringBounder);
		return new XPoint2D(dim.getWidth(), dim.getHeight() / 2);
	}

}
