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
package net.sourceforge.plantuml.sequencediagram.graphic;

import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.sequencediagram.InGroupable;
import net.sourceforge.plantuml.sequencediagram.InGroupableList;
import net.sourceforge.plantuml.skin.Area;
import net.sourceforge.plantuml.skin.Component;
import net.sourceforge.plantuml.skin.Context2D;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class GroupingGraphicalElementElse extends GroupingGraphicalElement implements InGroupable {

	private final Component compElse;
	private final Lazy afterY;
	private final boolean parallel;

	public GroupingGraphicalElementElse(double startingY, Component compElse, InGroupableList inGroupableList,
			boolean parallel, Lazy afterY) {
		super(startingY, inGroupableList);
		this.parallel = parallel;
		this.compElse = compElse;
		this.afterY = afterY;
	}

	@Override
	protected void drawInternalU(UGraphic ug, double maxX, Context2D context) {
		final StringBounder stringBounder = ug.getStringBounder();
		final double x1 = getInGroupableList().getMinX(stringBounder);
		final double x2 = getInGroupableList().getMaxX(stringBounder) - getInGroupableList().getHack2();
		ug = ug.apply(new UTranslate(x1, getStartingY()));

		final double height = afterY.getNow() - getStartingY();
		if (height <= 0) {
			return;
		}
		final XDimension2D dim = new XDimension2D(x2 - x1, height);

		if (parallel == false) {
			compElse.drawU(ug, new Area(dim), context);
		}
	}

	@Override
	public double getPreferredHeight(StringBounder stringBounder) {
		if (parallel) {
			return 0;
		}
		return compElse.getPreferredHeight(stringBounder);
	}

	@Override
	public double getPreferredWidth(StringBounder stringBounder) {
		return compElse.getPreferredWidth(stringBounder);
	}

	public double getMinX(StringBounder stringBounder) {
		return getStartingX(stringBounder);
	}

	public double getMaxX(StringBounder stringBounder) {
		return getMinX(stringBounder) + getPreferredWidth(stringBounder);
	}

	public String toString(StringBounder stringBounder) {
		return toString();
	}

}
