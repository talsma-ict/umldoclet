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
package net.sourceforge.plantuml.emoji;

import java.awt.geom.AffineTransform;

import net.sourceforge.plantuml.ugraphic.UChange;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UShape;

public class UGraphicWithScale {

	final private UGraphic ug;
	final private AffineTransform at;

	public UGraphicWithScale(UGraphic ug, double scale) {
		this(ug, AffineTransform.getScaleInstance(scale, scale));
	}

	private UGraphicWithScale(UGraphic ug, AffineTransform at) {
		this.ug = ug;
		this.at = at;
	}

	public UGraphic getUg() {
		return ug;
	}

	public UGraphicWithScale apply(UChange change) {
		return new UGraphicWithScale(ug.apply(change), at);
	}

	public UGraphicWithScale applyScale(double changex, double changey) {
		final AffineTransform copy = new AffineTransform(at);
		copy.scale(changex, changey);
		return new UGraphicWithScale(ug, copy);
	}

	public void draw(UShape shape) {
		ug.draw(shape);
	}

	public UGraphicWithScale applyRotate(double angle, double x, double y) {
		final AffineTransform copy = new AffineTransform(at);
		copy.rotate(angle * Math.PI / 180, x, y);
		return new UGraphicWithScale(ug, copy);
	}

	public UGraphicWithScale applyTranslate(double x, double y) {
		final AffineTransform copy = new AffineTransform(at);
		copy.translate(x, y);
		return new UGraphicWithScale(ug, copy);
	}

	public AffineTransform getAffineTransform() {
		return at;
	}

	public UGraphicWithScale applyMatrix(double v1, double v2, double v3, double v4, double v5, double v6) {
		final AffineTransform copy = new AffineTransform(at);
		copy.concatenate(new AffineTransform(new double[] { v1, v2, v3, v4, v5, v6 }));
		return new UGraphicWithScale(ug, copy);
	}

}
