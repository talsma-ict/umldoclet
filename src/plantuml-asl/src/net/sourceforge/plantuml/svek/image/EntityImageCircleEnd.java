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
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.svek.AbstractEntityImage;
import net.sourceforge.plantuml.svek.ShapeType;
import net.sourceforge.plantuml.ugraphic.UChangeBackColor;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UShape;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class EntityImageCircleEnd extends AbstractEntityImage {

	private static final int SIZE = 20;
	private final ColorParam param;

	public EntityImageCircleEnd(ILeaf entity, ISkinParam skinParam, ColorParam param) {
		super(entity, skinParam);
		this.param = param;
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		return new Dimension2DDouble(SIZE, SIZE);
	}

	final public void drawU(UGraphic ug) {
		final UEllipse circle = new UEllipse(SIZE, SIZE);
		if (getSkinParam().shadowing(getEntity().getStereotype())) {
			circle.setDeltaShadow(3);
		}
		ug.apply(new UChangeBackColor(null))
				.apply(new UChangeColor(SkinParamUtils.getColor(getSkinParam(), getStereo(), param)))
				.draw(circle);

		final double delta = 4;
		final UShape circleSmall = new UEllipse(SIZE - delta * 2, SIZE - delta * 2);
		ug.apply(new UChangeBackColor(SkinParamUtils.getColor(getSkinParam(), getStereo(), param)))
		.apply(new UChangeColor(null)).apply(new UTranslate(delta + 0.5, delta + 0.5)).draw(circleSmall);
	}

	public ShapeType getShapeType() {
		return ShapeType.CIRCLE;
	}

}
