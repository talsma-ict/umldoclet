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
package net.sourceforge.plantuml.cute;

import java.awt.geom.Point2D;

import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UPath;

public class Cheese implements CuteShape {

	private final MyDouble radius;
	private final MyDouble startAngle;
	private final MyDouble endAngle;
	private final RotationZoom rotationZoom;

	public Cheese(VarArgs varArgs) {
		this.radius = varArgs.getAsMyDouble("radius");
		this.startAngle = varArgs.getAsMyDouble("start").toRadians();
		this.endAngle = varArgs.getAsMyDouble("end").toRadians();
		this.rotationZoom = RotationZoom.none();
	}

	public Cheese(MyDouble radius, MyDouble startAngle, MyDouble endAngle, RotationZoom rotation) {
		this.radius = radius;
		this.startAngle = startAngle;
		this.endAngle = endAngle;
		this.rotationZoom = rotation;
	}

	public void drawU(UGraphic ug) {
		final Balloon balloon = new Balloon(new Point2D.Double(), radius.getValue())
				.rotate(rotationZoom);

		final double angle1 = rotationZoom.applyRotation(startAngle.getValue());
		final double angle2 = rotationZoom.applyRotation(endAngle.getValue());

		final Point2D ptA = balloon.getPointOnCircle(angle1);
		final Point2D ptB = balloon.getPointOnCircle(angle2);

		final UPath path = new UPath();
		final Point2D ptA0;
		if (radius.hasCurvation()) {
			ptA0 = balloon.getSegmentCenterToPointOnCircle(angle1).getFromAtoB(radius.getCurvation(0));
			path.moveTo(ptA0);
		} else {
			ptA0 = null;
			path.moveTo(balloon.getCenter());
		}
		final Balloon insideA;
		if (startAngle.hasCurvation()) {
			insideA = balloon.getInsideTangentBalloon1(angle1, startAngle.getCurvation(0));
			final Point2D ptA1 = balloon.getSegmentCenterToPointOnCircle(angle1).getFromAtoB(
					radius.getValue() - startAngle.getCurvation(0));
			final Point2D ptA2 = balloon.getPointOnCirclePassingByThisPoint(insideA.getCenter());
			path.lineTo(ptA1);
			path.arcTo(ptA2, insideA.getRadius(), 0, 1);
		} else {
			insideA = null;
			path.lineTo(ptA);
		}
		final Balloon insideB;
		if (endAngle.hasCurvation()) {
			insideB = balloon.getInsideTangentBalloon2(angle2, endAngle.getCurvation(0));
			final Point2D ptB1 = balloon.getPointOnCirclePassingByThisPoint(insideB.getCenter());
			final Point2D ptB2 = balloon.getSegmentCenterToPointOnCircle(angle2).getFromAtoB(
					radius.getValue() - endAngle.getCurvation(0));

			path.arcTo(ptB1, balloon.getRadius(), 0, 1);
			path.arcTo(ptB2, insideB.getRadius(), 0, 1);
		} else {
			insideB = null;
			path.arcTo(ptB, balloon.getRadius(), 0, 1);
		}
		if (radius.hasCurvation()) {
			final Point2D ptB0 = balloon.getSegmentCenterToPointOnCircle(angle2).getFromAtoB(radius.getCurvation(0));
			path.lineTo(ptB0);
			path.arcTo(ptA0, radius.getCurvation(0), 0, 1);
		} else {
			path.lineTo(balloon.getCenter());
		}
		path.closePath();
		ug.draw(path);

	}

	public CuteShape rotateZoom(RotationZoom other) {
		return new Cheese(radius, startAngle, endAngle, rotationZoom.compose(other));
	}

}
