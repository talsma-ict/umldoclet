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
package net.sourceforge.plantuml.ugraphic;

import java.util.LinkedHashMap;
import java.util.Map;

import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.awt.geom.XPoint2D;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;

public class PlacementStrategyX1X2 extends AbstractPlacementStrategy {

	public PlacementStrategyX1X2(StringBounder stringBounder) {
		super(stringBounder);
	}

	public Map<TextBlock, XPoint2D> getPositions(double width, double height) {
		final double usedWidth = getSumWidth();
		//double maxHeight = getMaxHeight();

		final double space = (width - usedWidth) / (getDimensions().size() + 1);
		final Map<TextBlock, XPoint2D> result = new LinkedHashMap<>();
		double x = space;
		for (Map.Entry<TextBlock, XDimension2D> ent : getDimensions().entrySet()) {
			final double y = (height - ent.getValue().getHeight()) / 2;
			result.put(ent.getKey(), new XPoint2D(x, y));
			x += ent.getValue().getWidth() + space;
		}
		return result;
	}

}
