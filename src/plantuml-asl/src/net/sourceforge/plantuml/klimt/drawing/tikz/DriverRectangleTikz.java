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
package net.sourceforge.plantuml.klimt.drawing.tikz;

import net.sourceforge.plantuml.klimt.UParam;
import net.sourceforge.plantuml.klimt.color.ColorMapper;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.color.HColorGradient;
import net.sourceforge.plantuml.klimt.drawing.UDriver;
import net.sourceforge.plantuml.klimt.shape.URectangle;
import net.sourceforge.plantuml.tikz.TikzGraphics;
import net.sourceforge.plantuml.utils.MathUtils;

public class DriverRectangleTikz implements UDriver<URectangle, TikzGraphics> {

	public void draw(URectangle rect, double x, double y, ColorMapper mapper, UParam param, TikzGraphics tikz) {
		final double width = rect.getWidth();
		final double height = rect.getHeight();
		final double r = MathUtils.min(rect.getRx(), rect.getRy(), width / 2, height / 2);

		final HColor back = param.getBackcolor();
		if (back instanceof HColorGradient) {
			final HColorGradient gr = (HColorGradient) back;
			tikz.setGradientColor(gr.getColor1(), gr.getColor2(), gr.getPolicy());
		} else {
			tikz.setFillColor(back);
		}
		tikz.setStrokeColor(param.getColor());
		tikz.setStrokeWidth(param.getStroke().getThickness(), param.getStroke().getDashTikz());
		if (r == 0) {
			tikz.rectangle(x, y, width, height);
		} else {
			tikz.rectangleRound(x, y, width, height, r);
		}
	}

}
