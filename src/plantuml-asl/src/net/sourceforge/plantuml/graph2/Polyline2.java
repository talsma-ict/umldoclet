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
package net.sourceforge.plantuml.graph2;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.List;

public class Polyline2 {

	private final List<Line2D.Double> lines = new ArrayList<Line2D.Double>();
	private Point2D lastCurrent;
	private final Point2D end;

	public Polyline2(Point2D start, Point2D end) {
		lastCurrent = start;
		this.end = end;
	}

	public void addLine(Line2D.Double newLine) {
		// Log.println("# Polyline2::addLine " +
		// GeomUtils.toString(newLine));
		if (lastCurrent.equals(newLine.getP1()) == false) {
			lines.add(new Line2D.Double(lastCurrent, newLine.getP1()));
		}
		lines.add(newLine);
		lastCurrent = newLine.getP2();
	}

	private boolean debug = false;

	public void draw(Graphics2D g2d) {
		close();
		if (debug) {
			g2d.setColor(Color.GREEN);
			drawDebug(g2d);
		}
		g2d.setColor(Color.BLUE);
		final List<Point2D.Double> centers = new ArrayList<Point2D.Double>();
		for (Line2D.Double l : lines) {
			centers.add(GeomUtils.getCenter(l));
		}
		g2d.draw(new Line2D.Double(lines.get(0).getP1(), centers.get(0)));
		g2d.draw(new Line2D.Double(centers.get(centers.size() - 1), end));
		for (int i = 0; i < lines.size() - 1; i++) {
			final Point2D c1 = centers.get(i);
			final Point2D c2 = centers.get(i + 1);
			final Point2D ctrl = lines.get(i).getP2();
			assert ctrl.equals(lines.get(i + 1).getP1());
			final QuadCurve2D.Double quad = new QuadCurve2D.Double(c1.getX(), c1.getY(), ctrl.getX(), ctrl.getY(), c2
					.getX(), c2.getY());
			g2d.draw(quad);
		}
		if (debug) {
			for (Point2D.Double c : centers) {
				GeomUtils.fillPoint2D(g2d, c);
			}
		}
	}

	private void drawDebug(Graphics2D g2d) {
		for (Line2D.Double l : lines) {
			g2d.draw(l);
			GeomUtils.fillPoint2D(g2d, l.getP1());
			GeomUtils.fillPoint2D(g2d, l.getP2());
		}
	}

	private void close() {
		if (lastCurrent.equals(end) == false) {
			lines.add(new Line2D.Double(lastCurrent, end));
		}
	}
}
