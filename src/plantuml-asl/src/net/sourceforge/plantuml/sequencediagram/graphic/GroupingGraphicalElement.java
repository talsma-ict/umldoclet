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
package net.sourceforge.plantuml.sequencediagram.graphic;

import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.sequencediagram.InGroupableList;

abstract class GroupingGraphicalElement extends GraphicalElement {

	private final InGroupableList inGroupableList;

	public GroupingGraphicalElement(double currentY, InGroupableList inGroupableList) {
		super(currentY);
		this.inGroupableList = inGroupableList;
		if (inGroupableList == null) {
			throw new IllegalArgumentException();
		}
	}

	final public double getActualWidth(StringBounder stringBounder) {
		return Math.max(getPreferredWidth(stringBounder),
				inGroupableList.getMaxX(stringBounder) - inGroupableList.getMinX(stringBounder) + 2
						* InGroupableList.MARGIN10);
	}

	@Override
	final public double getStartingX(StringBounder stringBounder) {
		return inGroupableList.getMinX(stringBounder) - InGroupableList.MARGIN10;
	}

	protected final InGroupableList getInGroupableList() {
		return inGroupableList;
	}

}
