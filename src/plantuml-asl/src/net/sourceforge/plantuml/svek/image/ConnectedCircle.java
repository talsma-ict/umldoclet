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
package net.sourceforge.plantuml.svek.image;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.HColors;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.geom.XPoint2D;
import net.sourceforge.plantuml.klimt.shape.UDrawable;
import net.sourceforge.plantuml.klimt.shape.UEllipse;

public class ConnectedCircle implements UDrawable {

	private final double radius;
	private final List<Double> angles = new ArrayList<>();
	private final List<XPoint2D> points = new ArrayList<>();

	public ConnectedCircle(double radius) {
		this.radius = radius;
	}

	public void drawU(UGraphic ug) {
		final UEllipse circle = UEllipse.build(2 * radius, 2 * radius);
		// ug.draw(circle);
		for (Double angle : angles) {
			final double delta = 30;
			final UEllipse part = new UEllipse(2 * radius, 2 * radius, angle - delta, 2 * delta);
			ug.draw(part);
		}
		ug = ug.apply(HColors.GREEN).apply(HColors.GREEN.bg());
		for (XPoint2D pt : points) {
			final UTranslate tr = UTranslate.point(pt);
			// ug.apply(tr).draw(UEllipse.build(2, 2));
		}

	}

	public void addSecondaryConnection(XPoint2D pt) {
		points.add(pt);
		// double angle = Math.atan2(pt.getY() - radius, pt.getX() - radius);
		// double angle = Math.atan2(pt.getX() - radius, pt.getY() - radius);
		double angle = Math.atan2(radius - pt.getY(), pt.getX() - radius);
		angle = angle * 180.0 / Math.PI;
		System.err.println("pt1=" + pt + " " + angle);
		angles.add(angle);

	}

}
