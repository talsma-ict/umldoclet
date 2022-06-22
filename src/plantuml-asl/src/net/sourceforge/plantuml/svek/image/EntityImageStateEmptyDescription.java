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
package net.sourceforge.plantuml.svek.image;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.awt.geom.Dimension2D;
import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class EntityImageStateEmptyDescription extends EntityImageStateCommon {

	final private static int MIN_WIDTH = 50;
	final private static int MIN_HEIGHT = 40;

	public EntityImageStateEmptyDescription(IEntity entity, ISkinParam skinParam) {
		super(entity, skinParam);

	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		final Dimension2D dim = desc.calculateDimension(stringBounder);
		final Dimension2D result = Dimension2DDouble.delta(dim, MARGIN * 2);
		return Dimension2DDouble.atLeast(result, MIN_WIDTH, MIN_HEIGHT);
	}

	final public void drawU(UGraphic ug) {
		if (url != null)
			ug.startUrl(url);

		final StringBounder stringBounder = ug.getStringBounder();
		final Dimension2D dimTotal = calculateDimension(stringBounder);
		final Dimension2D dimDesc = desc.calculateDimension(stringBounder);

		final UStroke stroke = getStyleState().getStroke();

		ug = applyColor(ug);
		ug = ug.apply(stroke);

		ug.draw(getShape(dimTotal));

		final double xDesc = (dimTotal.getWidth() - dimDesc.getWidth()) / 2;
		final double yDesc = (dimTotal.getHeight() - dimDesc.getHeight()) / 2;
		desc.drawU(ug.apply(new UTranslate(xDesc, yDesc)));

		if (url != null)
			ug.closeUrl();

	}

}
