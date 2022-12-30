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
package net.sourceforge.plantuml.gitlog;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class MagicBox {

	private final ISkinParam skinParam;
	private final GNode node;
	private final HColor fontColor;

	public MagicBox(ISkinParam skinParam, GNode node) {
		this.skinParam = skinParam;
		this.node = node;
		final Style style = StyleSignatureBasic.of(SName.root, SName.element, SName.gitDiagram)
				.getMergedStyle(skinParam.getCurrentStyleBuilder());
		this.fontColor = style.value(PName.FontColor).asColor(skinParam.getIHtmlColorSet());
	}

	private TextBlock getSmallBlock() {
		final FontConfiguration fc = FontConfiguration.create(UFont.monospaced(15).bold(), fontColor, fontColor, null);
		return node.getDisplay().create(fc, HorizontalAlignment.CENTER, skinParam);
	}

	private TextBlock getCommentBlock() {
		if (node.getComment() != null && node.isTop()) {
			final FontConfiguration tag = FontConfiguration.create(UFont.sansSerif(13), fontColor, fontColor, null);
			return Display.create(node.getComment()).create(tag, HorizontalAlignment.CENTER, skinParam);
		}
		return TextBlockUtils.empty(0, 0);

	}

	public XDimension2D getBigDim(StringBounder stringBounder) {
		final XDimension2D dimComment = getCommentBlock().calculateDimension(stringBounder);
		final XDimension2D dimSmall = getSmallBlock().calculateDimension(stringBounder);
		final XDimension2D mergeTB = dimComment.mergeTB(dimSmall);
		return mergeTB.delta(8, 2);
	}

	public void drawBorder(UGraphic ug, XDimension2D sizeInDot) {

		final TextBlock comment = getCommentBlock();
		final TextBlock small = getSmallBlock();

		final double moveY = comment.calculateDimension(ug.getStringBounder()).getHeight();

		final URectangle rect = new URectangle(sizeInDot.getWidth(), sizeInDot.getHeight() - moveY).rounded(8);
		ug.apply(new UStroke(1.5)).apply(UTranslate.dy(moveY)).draw(rect);

		comment.drawU(ug);

		final double deltaWidth = rect.getWidth() - small.calculateDimension(ug.getStringBounder()).getWidth();
		final double deltaHeight = rect.getHeight() - small.calculateDimension(ug.getStringBounder()).getHeight();

		small.drawU(ug.apply(new UTranslate(deltaWidth / 2, moveY + deltaHeight / 2)));

	}

}
