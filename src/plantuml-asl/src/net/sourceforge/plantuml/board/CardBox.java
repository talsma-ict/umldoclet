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
package net.sourceforge.plantuml.board;

import net.sourceforge.plantuml.awt.geom.Dimension2D;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;

public class CardBox extends AbstractTextBlock {

	private final Display label;
	private final ISkinParam skinParam;

	public CardBox(Display label, ISkinParam skinParam) {
		this.label = label;
		this.skinParam = skinParam;
	}

//	private StyleSignature getDefaultStyleDefinitionNode() {
//		return StyleSignature.of(SName.root, SName.element, SName.mindmapDiagram, SName.node);
//	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		return new Dimension2DDouble(150, 70);
	}

	public void drawU(UGraphic ug) {
		final URectangle rect = new URectangle(calculateDimension(ug.getStringBounder()));
		rect.setDeltaShadow(1);

		ug.apply(HColorUtils.BLACK).apply(HColorUtils.LIGHT_GRAY.bg()).draw(rect);

		label.create(FontConfiguration.blackBlueTrue(UFont.sansSerif(14)), HorizontalAlignment.LEFT, skinParam)
				.drawU(ug.apply(new UTranslate(3, 3)));

	}

}
