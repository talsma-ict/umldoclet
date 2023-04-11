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
package net.sourceforge.plantuml.klimt.geom;

import java.util.LinkedHashMap;
import java.util.Map;

import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.shape.TextBlock;

public class PlacementStrategyY1Y2Center extends AbstractPlacementStrategy {
	// ::remove file when __HAXE__

	public PlacementStrategyY1Y2Center(StringBounder stringBounder) {
		super(stringBounder);
	}

	public Map<TextBlock, XPoint2D> getPositions(double width, double height) {
		final double usedHeight = getSumHeight();
		// double maxWidth = getMaxWidth();

		final double space = (height - usedHeight) / (getDimensions().size() + 1);
		final Map<TextBlock, XPoint2D> result = new LinkedHashMap<>();
		double y = space;
		for (Map.Entry<TextBlock, XDimension2D> ent : getDimensions().entrySet()) {
			final double x = (width - ent.getValue().getWidth()) / 2;
			result.put(ent.getKey(), new XPoint2D(x, y));
			y += ent.getValue().getHeight() + space;
		}
		return result;
	}

}
