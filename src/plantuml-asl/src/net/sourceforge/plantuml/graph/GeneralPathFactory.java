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
package net.sourceforge.plantuml.graph;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import net.sourceforge.plantuml.cucadiagram.LinkType;
import net.sourceforge.plantuml.geom.Box;
import net.sourceforge.plantuml.geom.LineSegmentInt;
import net.sourceforge.plantuml.geom.PolylineBreakeable;

public class GeneralPathFactory {

	private final LinkType linkType;

	public GeneralPathFactory(LinkType linkType) {
		this.linkType = linkType;
	}

	public Shape getLink(PolylineBreakeable polyline, Box b1, Box b2) {
		final LineSegmentInt directSegment = new LineSegmentInt(b1.getCenterX(), b1.getCenterY(), b2.getCenterX(), b2
				.getCenterY());
		assert b1.intersect(directSegment).length == 1;
		assert b2.intersect(directSegment).length == 1;

		// final Point2D.Double start = polyline.clipStart(b1);
		// final Point2D.Double end = polyline.clipEnd(b2);
		final GeneralPath generalPath = polyline.asGeneralPath();
		// addSymbol(generalPath, start, polyline.getFirst(), end,
		// polyline.getLast());
		return generalPath;

	}

	private void addSymbol(GeneralPath generalPath, Point2D.Double firstPoint, LineSegmentInt firstSeg,
			Point2D.Double lastPoint, LineSegmentInt lastSeg) {
//		if (linkType.equals(LinkType.AGREGATION) || linkType.equals(LinkType.COMPOSITION)) {
//			addSymbolDiamond(generalPath, lastPoint, lastSeg);
//		} else if (linkType.equals(LinkType.AGREGATION_INV) || linkType.equals(LinkType.COMPOSITION_INV)) {
//			addSymbolDiamondInv(generalPath, firstPoint, firstSeg);
//		} else if (linkType.equals(LinkType.NAVASSOC) || linkType.equals(LinkType.NAVASSOC_DASHED)) {
//			addSymbolNavasoc(generalPath, lastPoint, lastSeg);
//		} else if (linkType.equals(LinkType.NAVASSOC_INV) || linkType.equals(LinkType.NAVASSOC_DASHED_INV)) {
//			addSymbolNavasocInv(generalPath, firstPoint, firstSeg);
//		} else if (linkType.equals(LinkType.EXTENDS_INV) || linkType.equals(LinkType.IMPLEMENTS_INV)) {
//			addSymbolExtends(generalPath, firstPoint, firstSeg);
//		} else if (linkType.equals(LinkType.EXTENDS) || linkType.equals(LinkType.IMPLEMENTS)) {
//			addSymbolExtendsInv(generalPath, lastPoint, lastSeg);
//		} else {
//			assert linkType.equals(LinkType.ASSOCIED) || linkType.equals(LinkType.ASSOCIED_DASHED);
//		}
	}

	private void addSymbolDiamond(GeneralPath generalPath, Point2D.Double point, LineSegmentInt seg) {
		final Polygon arrow = new Polygon();
		arrow.addPoint(0, 0);
		arrow.addPoint(-10, 6);
		arrow.addPoint(-20, 0);
		arrow.addPoint(-10, -6);

		appendAndRotate(generalPath, point, seg, arrow);
	}

	private void addSymbolDiamondInv(GeneralPath generalPath, Point2D.Double point, LineSegmentInt seg) {
		final Polygon arrow = new Polygon();
		arrow.addPoint(0, 0);
		arrow.addPoint(10, 6);
		arrow.addPoint(20, 0);
		arrow.addPoint(10, -6);

		appendAndRotate(generalPath, point, seg, arrow);
	}

	private void addSymbolNavasocInv(GeneralPath generalPath, Point2D.Double point, LineSegmentInt seg) {
		final Polygon arrow = new Polygon();
		arrow.addPoint(0, 0);
		arrow.addPoint(13, -8);
		arrow.addPoint(6, 0);
		arrow.addPoint(13, 8);

		appendAndRotate(generalPath, point, seg, arrow);
	}

	private void addSymbolNavasoc(GeneralPath generalPath, Point2D.Double point, LineSegmentInt seg) {
		final Polygon arrow = new Polygon();
		arrow.addPoint(0, 0);
		arrow.addPoint(-13, -8);
		arrow.addPoint(-6, 0);
		arrow.addPoint(-13, 8);

		appendAndRotate(generalPath, point, seg, arrow);
	}

	private void addSymbolExtends(GeneralPath generalPath, Point2D.Double point, LineSegmentInt seg) {
		final Polygon arrow = new Polygon();
		arrow.addPoint(0, 0);
		arrow.addPoint(25, 7);
		arrow.addPoint(25, -7);

		appendAndRotate(generalPath, point, seg, arrow);
	}

	private void addSymbolExtendsInv(GeneralPath generalPath, Point2D.Double point, LineSegmentInt seg) {
		final Polygon arrow = new Polygon();
		arrow.addPoint(0, 0);
		arrow.addPoint(-25, 7);
		arrow.addPoint(-25, -7);

		appendAndRotate(generalPath, point, seg, arrow);
	}

	private void appendAndRotate(GeneralPath generalPath, Point2D.Double point, LineSegmentInt seg, final Shape shape) {
		final AffineTransform at = AffineTransform.getTranslateInstance(point.x, point.y);
		final double theta = seg.getAngle();
		at.rotate(theta);

		final Shape r = at.createTransformedShape(shape);
		generalPath.append(r, false);
	}

}
