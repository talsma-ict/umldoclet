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
package net.sourceforge.plantuml.nwdiag;

import java.util.Set;

import net.sourceforge.plantuml.klimt.UPath;
import net.sourceforge.plantuml.klimt.color.HColors;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.shape.UDrawable;

public class VerticalLine implements UDrawable {

	private final double y1;
	private final double y2;
	private final Set<Double> skip;

	public VerticalLine(double y1, double y2, Set<Double> skip) {
		this.y1 = Math.min(y1, y2);
		this.y2 = Math.max(y1, y2);
		this.skip = skip;
	}

	public void drawU(UGraphic ug) {
		ug = ug.apply(HColors.none().bg());
		boolean drawn = false;
		double current = y1;
		UPath path = UPath.none();
		path.moveTo(0, current);
		for (Double step : skip) {
			if (step < y1) {
				continue;
			}
			assert step >= y1;
			drawn = true;
			if (step == y2) {
				path.lineTo(0, y2);
			} else {
				path.lineTo(0, Math.min(y2, step - 3));
				if (y2 > step) {
					path.arcTo(4, 4, 0, 0, 1, 0, step + 9);
					continue;
				}
			}
			ug.draw(path);
			path = UPath.none();
			current = step + 9;
			path.moveTo(0, current);
			if (current >= y2) {
				break;
			}
		}
		if (drawn == false) {
			path.lineTo(0, y2);
			ug.draw(path);
		}

	}

}
