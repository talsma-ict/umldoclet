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

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.activitydiagram3.ftile.vertical.FtileBoxOld;
import net.sourceforge.plantuml.awt.geom.XPoint2D;
import net.sourceforge.plantuml.creole.CreoleMode;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.mindmap.IdeaShape;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleBuilder;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UTranslate;

abstract class WBSTextBlock extends AbstractTextBlock {

	protected final ISkinParam skinParam;
	private final StyleBuilder styleBuilder;
	private final int level;

	public WBSTextBlock(ISkinParam skinParam, StyleBuilder styleBuilder, int level) {
		this.skinParam = skinParam;
		this.styleBuilder = styleBuilder;
		this.level = level;
	}

	final protected void drawLine(UGraphic ug, XPoint2D p1, XPoint2D p2) {
		final ULine line = new ULine(p1, p2);
		getStyleUsed().applyStrokeAndLineColor(ug.apply(new UTranslate(p1)), skinParam.getIHtmlColorSet()).draw(line);
	}

	private Style getStyleUsed() {
		return getDefaultStyleDefinitionArrow().getMergedStyle(styleBuilder);
	}

	final protected void drawLine(UGraphic ug, double x1, double y1, double x2, double y2) {
		drawLine(ug, new XPoint2D(Math.min(x1, x2), y1), new XPoint2D(Math.max(x1, x2), y2));
	}

	final public StyleSignatureBasic getDefaultStyleDefinitionArrow() {
		return StyleSignatureBasic.of(SName.root, SName.element, SName.wbsDiagram, SName.arrow).add(SName.depth(level));
	}

	final protected TextBlock buildMain(WElement idea) {
		final Display label = idea.getLabel();
		final Style style = idea.getStyle();
		if (idea.getShape() == IdeaShape.BOX)
			return FtileBoxOld.createWbs(style, idea.withBackColor(skinParam), label);

		final TextBlock text = label.create0(
				style.getFontConfiguration(skinParam.getIHtmlColorSet()),
				style.getHorizontalAlignment(), skinParam, style.wrapWidth(), CreoleMode.FULL, null, null);
		return TextBlockUtils.withMargin(text, 0, 3, 1, 1);
	}

}
