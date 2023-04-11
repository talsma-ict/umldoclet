/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
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
package net.sourceforge.plantuml.klimt.drawing.g2d;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import net.sourceforge.plantuml.klimt.UParam;
import net.sourceforge.plantuml.klimt.color.ColorMapper;
import net.sourceforge.plantuml.klimt.drawing.UDriver;
import net.sourceforge.plantuml.klimt.geom.EnsureVisible;
import net.sourceforge.plantuml.klimt.shape.UImage;

public class DriverImageG2d implements UDriver<UImage, Graphics2D> {

	private final EnsureVisible visible;

	private final double dpiFactor;

	public DriverImageG2d(double dpiFactor, EnsureVisible visible) {
		this.visible = visible;
		this.dpiFactor = dpiFactor;
	}

	public void draw(UImage shape, double x, double y, ColorMapper mapper, UParam param, Graphics2D g2d) {
		visible.ensureVisible(x, y);
		visible.ensureVisible(x + shape.getWidth(), y + shape.getHeight());
		if (dpiFactor == 1) {
			g2d.drawImage(shape.getImage(1), (int) (x), (int) (y), null);
		} else {
			final AffineTransform back = g2d.getTransform();
			g2d.scale(1 / dpiFactor, 1 / dpiFactor);
			g2d.drawImage(shape.getImage(dpiFactor), (int) (x * dpiFactor), (int) (y * dpiFactor), null);
			g2d.setTransform(back);
		}
	}

}
