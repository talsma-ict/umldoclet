/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
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
package net.sourceforge.plantuml.ugraphic.svg;

import java.awt.font.TextLayout;

import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.graphic.UnusedSpace;
import net.sourceforge.plantuml.svg.SvgGraphics;
import net.sourceforge.plantuml.ugraphic.UCenteredCharacter;
import net.sourceforge.plantuml.ugraphic.UDriver;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UParam;
import net.sourceforge.plantuml.ugraphic.UShape;
import net.sourceforge.plantuml.ugraphic.color.ColorMapper;

public class DriverCenteredCharacterSvg implements UDriver<SvgGraphics> {

	public void draw(UShape ushape, double x, double y, ColorMapper mapper, UParam param, SvgGraphics svg) {
		final UCenteredCharacter characterCircled = (UCenteredCharacter) ushape;
		final char c = characterCircled.getChar();
		final UFont font = characterCircled.getFont();
		final UnusedSpace unusedSpace = UnusedSpace.getUnusedSpace(font, c);

		final double xpos = x - unusedSpace.getCenterX() - 0.5;
		final double ypos = y - unusedSpace.getCenterY() - 0.5;

		final TextLayout t = new TextLayout("" + c, font.getUnderlayingFont(), TextBlockUtils.getFontRenderContext());
		svg.setFillColor(mapper.toRGB(param.getColor()));
		svg.drawPathIterator(xpos, ypos, t.getOutline(null).getPathIterator(null));

	}
}
