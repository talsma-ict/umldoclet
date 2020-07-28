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
package net.sourceforge.plantuml.ugraphic;

import java.awt.geom.Dimension2D;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;

public abstract class AbstractPlacementStrategy implements PlacementStrategy {

	private final StringBounder stringBounder;
	private final Map<TextBlock, Dimension2D> dimensions = new LinkedHashMap<TextBlock, Dimension2D>();

	public AbstractPlacementStrategy(StringBounder stringBounder) {
		this.stringBounder = stringBounder;
	}

	public void add(TextBlock block) {
		this.dimensions.put(block, block.calculateDimension(stringBounder));
	}

	protected Map<TextBlock, Dimension2D> getDimensions() {
		return dimensions;
	}

	protected double getSumWidth() {
		return getSumWidth(dimensions.values().iterator());
	}

	protected double getSumHeight() {
		return getSumHeight(dimensions.values().iterator());
	}

	protected double getMaxHeight() {
		return getMaxHeight(dimensions.values().iterator());
	}

	protected double getMaxWidth() {
		return getMaxWidth(dimensions.values().iterator());
	}

	protected double getSumWidth(Iterator<Dimension2D> it) {
		double result = 0;
		while (it.hasNext()) {
			result += it.next().getWidth();
		}
		return result;
	}

	protected double getSumHeight(Iterator<Dimension2D> it) {
		double result = 0;
		while (it.hasNext()) {
			result += it.next().getHeight();
		}
		return result;
	}

	protected double getMaxWidth(Iterator<Dimension2D> it) {
		double result = 0;
		while (it.hasNext()) {
			result = Math.max(result, it.next().getWidth());
		}
		return result;
	}

	protected double getMaxHeight(Iterator<Dimension2D> it) {
		double result = 0;
		while (it.hasNext()) {
			result = Math.max(result, it.next().getHeight());
		}
		return result;
	}

	protected final StringBounder getStringBounder() {
		return stringBounder;
	}

}
