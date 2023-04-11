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

import net.sourceforge.plantuml.klimt.geom.XPoint2D;
import net.sourceforge.plantuml.klimt.shape.UEllipse;

public class ContainingEllipse {

	private final SmallestEnclosingCircle sec = new SmallestEnclosingCircle();
	private final YTransformer ytransformer;

	@Override
	public String toString() {
		return "ContainingEllipse " + getWidth() + " " + getHeight();
	}

	public ContainingEllipse(double coefY) {
		ytransformer = new YTransformer(coefY);
	}

	public void append(XPoint2D pt) {
		pt = ytransformer.getReversePoint2D(pt);
		sec.append(pt);
	}

	public void append(double x, double y) {
		append(new XPoint2D(x, y));
	}

	public double getWidth() {
		return 2 * sec.getCircle().getRadius();
	}

	public double getHeight() {
		return 2 * sec.getCircle().getRadius() * ytransformer.getAlpha();
	}

	public XPoint2D getCenter() {
		return ytransformer.getPoint2D(sec.getCircle().getCenter());
	}

	public UEllipse asUEllipse() {
		final UEllipse ellipse = UEllipse.build(getWidth(), getHeight());
		ellipse.setDeltaShadow(deltaShadow);
		return ellipse;
	}

	private double deltaShadow;

	public void setDeltaShadow(double deltaShadow) {
		this.deltaShadow = deltaShadow;
	}

}
