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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.awt.geom.XPoint2D;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;

public class PlacementStrategyVisibility extends AbstractPlacementStrategy {

	private final int col2;

	public PlacementStrategyVisibility(StringBounder stringBounder, int col2) {
		super(stringBounder);
		this.col2 = col2;
	}

	public Map<TextBlock, XPoint2D> getPositions(double width, double height) {
		final Map<TextBlock, XPoint2D> result = new LinkedHashMap<>();
		double y = 0;
		for (final Iterator<Map.Entry<TextBlock, XDimension2D>> it = getDimensions().entrySet().iterator(); it
				.hasNext();) {
			final Map.Entry<TextBlock, XDimension2D> ent1 = it.next();
			final Map.Entry<TextBlock, XDimension2D> ent2 = it.next();

			final double height1 = ent1.getValue().getHeight();
			final double height2 = ent2.getValue().getHeight();
			final double maxHeight12 = Math.max(height1, height2);

			result.put(ent1.getKey(), new XPoint2D(0, 2 + y + (maxHeight12 - height1) / 2));
			result.put(ent2.getKey(), new XPoint2D(col2, y + (maxHeight12 - height2) / 2));
			y += maxHeight12;
		}
		return result;
	}

}
