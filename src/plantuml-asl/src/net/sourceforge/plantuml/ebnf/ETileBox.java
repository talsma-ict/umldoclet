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
package net.sourceforge.plantuml.ebnf;

import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UText;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColors;

public class ETileBox extends AbstractTextBlock implements ETile {

	private final String value;
	private final FontConfiguration fc;
	private final UText utext;

	public ETileBox(String value, FontConfiguration fc) {
		this.value = value;
		this.fc = fc;
		this.utext = new UText(value, fc);
	}

	@Override
	public XDimension2D calculateDimension(StringBounder stringBounder) {
		return XDimension2D.delta(getTextDim(stringBounder), 10);
	}

	private XDimension2D getTextDim(StringBounder stringBounder) {
		return stringBounder.calculateDimension(fc.getFont(), value);
	}

	@Override
	public void drawU(UGraphic ug) {
		final XDimension2D dim = calculateDimension(ug.getStringBounder());
		final XDimension2D dimText = getTextDim(ug.getStringBounder());
		final URectangle rect = new URectangle(dim).rounded(10);
		ug.apply(HColors.BLACK).apply(new UStroke(1.5)).draw(rect);
		ug.apply(new UTranslate(5, 5 + dimText.getHeight() - utext.getDescent(ug.getStringBounder()))).draw(utext);
	}

	@Override
	public double linePos(StringBounder stringBounder) {
		final double height = calculateDimension(stringBounder).getHeight();
		return height / 2;
	}

}
