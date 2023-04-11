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
package net.sourceforge.plantuml.wire;

import net.sourceforge.plantuml.klimt.UChange;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.color.HColors;
import net.sourceforge.plantuml.klimt.creole.Display;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.font.FontConfiguration;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.font.UFont;
import net.sourceforge.plantuml.klimt.geom.HorizontalAlignment;
import net.sourceforge.plantuml.klimt.shape.TextBlock;
import net.sourceforge.plantuml.style.ISkinParam;

public class WPrint {

	private final UTranslate position;
	private final HColor color;
	private final Display label;
	private final ISkinParam skinParam;

	public WPrint(ISkinParam skinParam, UTranslate position, HColor color, Display label) {
		this.position = position;
		this.skinParam = skinParam;
		this.label = label;
		this.color = color == null ? getBlack() : color;
	}

	private HColor getBlack() {
		return HColors.BLACK.withDark(HColors.WHITE);
	}

	private TextBlock getTextBlock() {
		final FontConfiguration fontConfiguration = FontConfiguration.blackBlueTrue(UFont.sansSerif(10))
				.changeColor(color);
		return label.create(fontConfiguration, HorizontalAlignment.LEFT, skinParam);
	}

	public UChange getPosition() {
		return position;
	}

	public void drawMe(UGraphic ug) {
		getTextBlock().drawU(ug);
	}

	public double getHeight(StringBounder stringBounder) {
		return getTextBlock().calculateDimension(stringBounder).getHeight();
	}

}
