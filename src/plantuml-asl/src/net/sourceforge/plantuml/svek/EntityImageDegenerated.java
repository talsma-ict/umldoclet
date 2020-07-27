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
package net.sourceforge.plantuml.svek;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.SkinParam;
import net.sourceforge.plantuml.graphic.InnerStrategy;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.MinMax;
import net.sourceforge.plantuml.ugraphic.UEmpty;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class EntityImageDegenerated implements IEntityImage {

	private final IEntityImage orig;
	private final double delta = 7;
	private final HColor backcolor;

	public EntityImageDegenerated(IEntityImage orig, HColor backcolor) {
		this.orig = orig;
		this.backcolor = backcolor;
	}

	public boolean isHidden() {
		return orig.isHidden();
	}

	public HColor getBackcolor() {
		// return orig.getBackcolor();
		return backcolor;
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		return Dimension2DDouble.delta(orig.calculateDimension(stringBounder), delta * 2, delta * 2);
	}

	public MinMax getMinMax(StringBounder stringBounder) {
		return orig.getMinMax(stringBounder);
		// return orig.getMinMax(stringBounder).translate(new UTranslate(delta, delta));
		// return orig.getMinMax(stringBounder).appendToMax(delta, delta);
	}

	public Rectangle2D getInnerPosition(String member, StringBounder stringBounder, InnerStrategy strategy) {
		return orig.getInnerPosition(member, stringBounder, strategy);
	}

	public void drawU(UGraphic ug) {
		orig.drawU(ug.apply(new UTranslate(delta, delta)));
		if (SkinParam.USE_STYLES()) {
			final Dimension2D dim = calculateDimension(ug.getStringBounder());
			ug.apply(new UTranslate(dim.getWidth() - delta, dim.getHeight() - delta)).draw(new UEmpty(delta, delta));
		}

	}

	public ShapeType getShapeType() {
		return orig.getShapeType();
	}

	public Margins getShield(StringBounder stringBounder) {
		return orig.getShield(stringBounder);
	}

	public double getOverscanX(StringBounder stringBounder) {
		return orig.getOverscanX(stringBounder);
	}

}
