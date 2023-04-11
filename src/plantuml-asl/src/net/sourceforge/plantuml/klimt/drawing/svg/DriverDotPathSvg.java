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
package net.sourceforge.plantuml.klimt.drawing.svg;

import net.sourceforge.plantuml.klimt.UParam;
import net.sourceforge.plantuml.klimt.color.ColorMapper;
import net.sourceforge.plantuml.klimt.drawing.UDriver;
import net.sourceforge.plantuml.klimt.shape.DotPath;

public class DriverDotPathSvg implements UDriver<DotPath, SvgGraphics> {
    // ::remove file when __HAXE__

	public void draw(DotPath shape, double x, double y, ColorMapper mapper, UParam param, SvgGraphics svg) {

		if (param.getColor().isTransparent() == false) {
			DriverRectangleSvg.applyStrokeColor(svg, mapper, param);

			svg.setFillColor(null);
			svg.setStrokeWidth(param.getStroke().getThickness(), param.getStroke().getDasharraySvg());

			svg.svgPath(x, y, shape.toUPath(), 0);
		}
	}
}
