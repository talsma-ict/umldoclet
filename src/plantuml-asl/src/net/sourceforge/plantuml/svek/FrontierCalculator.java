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

import java.awt.geom.Point2D;
import java.util.Collection;

import net.sourceforge.plantuml.cucadiagram.EntityPosition;

public class FrontierCalculator {

	private static final double DELTA = 3 * EntityPosition.RADIUS;
	private ClusterPosition core;
	private final ClusterPosition initial;

	public FrontierCalculator(final ClusterPosition initial, Collection<ClusterPosition> insides,
			Collection<Point2D> points) {
		this.initial = initial;
		for (ClusterPosition in : insides) {
			if (core == null) {
				core = in;
			} else {
				core = core.merge(in);
			}
		}
		if (core == null) {
			final Point2D center = initial.getPointCenter();
			core = new ClusterPosition(center.getX() - 1, center.getY() - 1, center.getX() + 1, center.getY() + 1);
		}
		for (Point2D p : points) {
			core = core.merge(p);
		}
		boolean touchMinX = false;
		boolean touchMaxX = false;
		boolean touchMinY = false;
		boolean touchMaxY = false;
		for (Point2D p : points) {
			if (p.getX() == core.getMinX()) {
				touchMinX = true;
			}
			if (p.getX() == core.getMaxX()) {
				touchMaxX = true;
			}
			if (p.getY() == core.getMinY()) {
				touchMinY = true;
			}
			if (p.getY() == core.getMaxY()) {
				touchMaxY = true;
			}
		}
		if (touchMinX == false) {
			core = core.withMinX(initial.getMinX());
		}
		if (touchMaxX == false) {
			core = core.withMaxX(initial.getMaxX());
		}
		if (touchMinY == false) {
			core = core.withMinY(initial.getMinY());
		}
		if (touchMaxY == false) {
			core = core.withMaxY(initial.getMaxY());
		}
		boolean pushMinX = false;
		boolean pushMaxX = false;
		boolean pushMinY = false;
		boolean pushMaxY = false;
		for (Point2D p : points) {
			if (p.getY() == core.getMinY() || p.getY() == core.getMaxY()) {
				if (Math.abs(p.getX() - core.getMaxX()) < DELTA) {
					pushMaxX = true;
				}
				if (Math.abs(p.getX() - core.getMinX()) < DELTA) {
					pushMinX = true;
				}
			}
			if (p.getX() == core.getMinX() || p.getX() == core.getMaxX()) {
				if (Math.abs(p.getY() - core.getMaxY()) < DELTA) {
					pushMaxY = true;
				}
				if (Math.abs(p.getY() - core.getMinY()) < DELTA) {
					pushMinY = true;
				}
			}
		}
		for (Point2D p : points) {
//			if (p.getX() == core.getMinX() && (p.getY() == core.getMinY() || p.getY() == core.getMaxY())) {
//				pushMinX = false;
//			}
//			if (p.getX() == core.getMaxX() && (p.getY() == core.getMinY() || p.getY() == core.getMaxY())) {
//				pushMaxX = false;
//			}
			if (p.getY() == core.getMinY() && (p.getX() == core.getMinX() || p.getX() == core.getMaxX())) {
				pushMinY = false;
			}
			if (p.getY() == core.getMaxY() && (p.getX() == core.getMinX() || p.getX() == core.getMaxX())) {
				pushMaxY = false;
			}
		}
		if (pushMaxX) {
			core = core.addMaxX(DELTA);
		}
		if (pushMinX) {
			core = core.addMinX(-DELTA);
		}
		if (pushMaxY) {
			core = core.addMaxY(DELTA);
		}
		if (pushMinY) {
			core = core.addMinY(-DELTA);
		}
	}

	public ClusterPosition getSuggestedPosition() {
		return core;
	}

	public void ensureMinWidth(double minWidth) {
		final double delta = core.getMaxX() - core.getMinX() - minWidth;
		if (delta < 0) {
			double newMinX = core.getMinX() + delta / 2;
			double newMaxX = core.getMaxX() - delta / 2;
			final double error = newMinX - initial.getMinX();
			if (error < 0) {
				newMinX -= error;
				newMaxX -= error;
			}
			core = core.withMinX(newMinX);
			core = core.withMaxX(newMaxX);
		}
	}

}
