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
package net.sourceforge.plantuml.svek.image;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.SkinParamUtils;
import net.sourceforge.plantuml.cucadiagram.ILeaf;
import net.sourceforge.plantuml.cucadiagram.Rankdir;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.svek.AbstractEntityImage;
import net.sourceforge.plantuml.svek.ShapeType;
import net.sourceforge.plantuml.ugraphic.Shadowable;
import net.sourceforge.plantuml.ugraphic.UChangeBackColor;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.URectangle;

public class EntityImageSynchroBar extends AbstractEntityImage {

	public EntityImageSynchroBar(ILeaf entity, ISkinParam skinParam) {
		super(entity, skinParam);
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		if (getSkinParam().getRankdir() == Rankdir.LEFT_TO_RIGHT) {
			return new Dimension2DDouble(8, 80);
		}
		return new Dimension2DDouble(80, 8);
	}

	final public void drawU(UGraphic ug) {
		final Dimension2D dim = calculateDimension(ug.getStringBounder());
		final Shadowable rect = new URectangle(dim.getWidth(), dim.getHeight());
		if (getSkinParam().shadowing(getEntity().getStereotype())) {
			rect.setDeltaShadow(4);
		}
		ug.apply(new UChangeColor(null))
				.apply(new UChangeBackColor(SkinParamUtils
						.getColor(getSkinParam(), getStereo(), ColorParam.activityBar))).draw(rect);
	}

	public ShapeType getShapeType() {
		return ShapeType.RECTANGLE;
	}

}
