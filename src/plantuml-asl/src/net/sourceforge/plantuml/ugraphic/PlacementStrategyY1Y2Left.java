/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
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

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;

public class PlacementStrategyY1Y2Left extends AbstractPlacementStrategy {

	public PlacementStrategyY1Y2Left(StringBounder stringBounder) {
		super(stringBounder);
	}

	public Map<TextBlock, Point2D> getPositions(double width, double height) {
		final double usedHeight = getSumHeight();
		//double maxWidth = getMaxWidth();

		final double space = (height - usedHeight) / (getDimensions().size() + 1);
		final Map<TextBlock, Point2D> result = new LinkedHashMap<TextBlock, Point2D>();
		double y = space;
		for (Map.Entry<TextBlock, Dimension2D> ent : getDimensions().entrySet()) {
			final double x = 0;
			result.put(ent.getKey(), new Point2D.Double(x, y));
			y += ent.getValue().getHeight() + space;
		}
		return result;
	}

}
