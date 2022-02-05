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

import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public final class InnerActivity extends AbstractTextBlock implements IEntityImage {

	private final IEntityImage im;
	private final HColor borderColor;
	private final double shadowing;
	private final HColor backColor;

	public InnerActivity(final IEntityImage im, HColor borderColor, HColor backColor, double shadowing) {
		this.im = im;
		this.backColor = backColor;
		this.borderColor = borderColor;
		this.shadowing = shadowing;
	}

	public final static double THICKNESS_BORDER = 1.5;

	public void drawU(UGraphic ug) {
		final Dimension2D total = calculateDimension(ug.getStringBounder());

		ug = ug.apply(backColor.bg()).apply(borderColor).apply(new UStroke(THICKNESS_BORDER));
		final URectangle rect = new URectangle(total.getWidth(), total.getHeight()).rounded(IEntityImage.CORNER);
		rect.setDeltaShadow(shadowing);
		ug.draw(rect);
		ug = ug.apply(new UStroke());
		im.drawU(ug);
	}

	public HColor getBackcolor() {
		return im.getBackcolor();
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		final Dimension2D img = im.calculateDimension(stringBounder);
		return img;
	}

	public ShapeType getShapeType() {
		return ShapeType.ROUND_RECTANGLE;
	}

	public Margins getShield(StringBounder stringBounder) {
		return Margins.NONE;
	}

	public boolean isHidden() {
		return im.isHidden();
	}

	public double getOverscanX(StringBounder stringBounder) {
		return 0;
	}

}
