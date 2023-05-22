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
package net.sourceforge.plantuml.sdot;

import h.ST_Agedge_s;
import h.ST_Agedgeinfo_t;
import h.ST_bezier;
import h.ST_pointf;
import h.ST_splines;
import h.ST_textlabel_t;
import net.sourceforge.plantuml.abel.Link;
import net.sourceforge.plantuml.cucadiagram.ICucaDiagram;
import net.sourceforge.plantuml.decoration.LinkType;
import net.sourceforge.plantuml.klimt.UStroke;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.ColorType;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.geom.RectangleArea;
import net.sourceforge.plantuml.klimt.geom.XPoint2D;
import net.sourceforge.plantuml.klimt.shape.DotPath;
import net.sourceforge.plantuml.klimt.shape.TextBlock;
import net.sourceforge.plantuml.klimt.shape.UDrawable;
import net.sourceforge.plantuml.klimt.shape.URectangle;
import net.sourceforge.plantuml.skin.LineParam;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.svek.Bibliotekon;
import net.sourceforge.plantuml.svek.Cluster;
import net.sourceforge.plantuml.svek.extremity.ExtremityFactory;
import net.sourceforge.plantuml.url.Url;

public class SmetanaPath implements UDrawable {

	private final Link link;
	private final ST_Agedge_s edge;
	private final YMirror ymirror;
	private final ICucaDiagram diagram;
	private final TextBlock label;
	private final TextBlock headLabel;
	private final TextBlock tailLabel;
	private final Bibliotekon bibliotekon;

	public SmetanaPath(Link link, ST_Agedge_s edge, YMirror ymirror, ICucaDiagram diagram, TextBlock label,
			TextBlock tailLabel, TextBlock headLabel, Bibliotekon bibliotekon) {
		this.bibliotekon = bibliotekon;
		this.link = link;
		this.edge = edge;
		this.ymirror = ymirror;
		this.diagram = diagram;
		this.label = label;
		this.tailLabel = tailLabel;
		this.headLabel = headLabel;
	}

	public void drawU(UGraphic ug) {

		if (link.isHidden())
			return;

		HColor color = getStyle().value(PName.LineColor).asColor(diagram.getSkinParam().getIHtmlColorSet());

		if (this.link.getColors() != null) {
			final HColor newColor = this.link.getColors().getColor(ColorType.ARROW, ColorType.LINE);
			if (newColor != null)
				color = newColor;
		} else if (this.link.getSpecificColor() != null)
			color = this.link.getSpecificColor();

		DotPath dotPath = getDotPathInternal();

		if (dotPath != null) {
			if (ymirror != null)
				dotPath = ymirror.getMirrored(dotPath);

			RectangleArea rectangleArea1 = null;
			RectangleArea rectangleArea2 = null;
			if (link.getEntity1().isGroup()) {
				final Cluster cluster1 = bibliotekon.getCluster(link.getEntity1());
				rectangleArea1 = cluster1.getRectangleArea();

			}
			if (link.getEntity2().isGroup()) {
				final Cluster cluster2 = bibliotekon.getCluster(link.getEntity2());
				rectangleArea2 = cluster2.getRectangleArea();
			}

			dotPath = dotPath.simulateCompound(rectangleArea2, rectangleArea1);

			final LinkType linkType = link.getType();
			UStroke stroke = linkType.getStroke3(diagram.getSkinParam().getThickness(LineParam.arrow, null));
			if (link.getColors() != null && link.getColors().getSpecificLineStroke() != null)
				stroke = link.getColors().getSpecificLineStroke();

			final Url url = link.getUrl();
			if (url != null)
				ug.startUrl(url);

			ug.apply(stroke).apply(color).draw(dotPath);
			printExtremityAtStart(dotPath, ug.apply(color));
			printExtremityAtEnd(dotPath, ug.apply(color));

			if (url != null)
				ug.closeUrl();

		}
		if (getLabelRectangleTranslate("label") != null)
			label.drawU(ug.apply(getLabelRectangleTranslate("label")));

		if (getLabelRectangleTranslate("head_label") != null)
			headLabel.drawU(ug.apply(getLabelRectangleTranslate("head_label")));

		if (getLabelRectangleTranslate("tail_label") != null)
			tailLabel.drawU(ug.apply(getLabelRectangleTranslate("tail_label")));

		// printDebug(ug);

	}

	private Style getStyle() {
		return StyleSignatureBasic
				.of(SName.root, SName.element, diagram.getUmlDiagramType().getStyleName(), SName.arrow)
				.getMergedStyle(diagram.getSkinParam().getCurrentStyleBuilder());
	}

	public XPoint2D getStartPoint() {
		final DotPath dotPath = getDotPathInternal();
		XPoint2D pt = dotPath.getStartPoint();
		if (ymirror != null)
			pt = ymirror.getMirrored(pt);

		return pt;
	}

	public XPoint2D getEndPoint() {
		final DotPath dotPath = getDotPathInternal();
		XPoint2D pt = dotPath.getEndPoint();
		if (ymirror != null)
			pt = ymirror.getMirrored(pt);

		return pt;
	}

	private void printExtremityAtStart(DotPath dotPath, UGraphic ug) {
		final ExtremityFactory extremityFactory2 = link.getType().getDecor2()
				.getExtremityFactoryComplete(diagram.getSkinParam().getBackgroundColor());
		if (extremityFactory2 == null)
			return;

		final XPoint2D p0 = dotPath.getStartPoint();
		final double startAngle = dotPath.getStartAngle() + Math.PI;

		try {
			final UDrawable extremity2 = extremityFactory2.createUDrawable(p0, startAngle, null);
			if (extremity2 != null)
				extremity2.drawU(ug);

		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			System.err.println("CANNOT DRAW printExtremityAtStart");
		}
	}

	private void printExtremityAtEnd(DotPath dotPath, UGraphic ug) {
		final ExtremityFactory extremityFactory1 = link.getType().getDecor1()
				.getExtremityFactoryComplete(diagram.getSkinParam().getBackgroundColor());
		if (extremityFactory1 == null)
			return;

		final XPoint2D p0 = dotPath.getEndPoint();
		final double endAngle = dotPath.getEndAngle();

		try {
			final UDrawable extremity1 = extremityFactory1.createUDrawable(p0, endAngle, null);
			if (extremity1 != null)
				extremity1.drawU(ug);

		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			System.err.println("CANNOT DRAW printExtremityAtEnd");
		}
	}

//	private void printDebug(UGraphic ug) {
//		ug = ug.apply(HColors.BLUE).apply(HColors.BLUE.bg());
//		final ST_splines splines = getSplines(edge);
//		final ST_bezier beziers = splines.list.get__(0);
//		for (int i = 0; i < beziers.size; i++) {
//			XPoint2D pt = getPoint(splines, i);
//			if (ymirror != null)
//				pt = ymirror.getMirrored(pt);
//
//			ug.apply(UTranslate.point(pt).compose(new UTranslate(-1, -1))).draw(UEllipse.build(3, 3));
//		}
//		if (getLabelRectangleTranslate("label") != null && getLabelURectangle() != null) {
//			ug = ug.apply(HColors.BLUE).apply(HColors.none().bg());
//			ug.apply(getLabelRectangleTranslate("label")).draw(getLabelURectangle());
//		}
//
//	}

	private URectangle getLabelURectangle() {
		final ST_Agedgeinfo_t data = (ST_Agedgeinfo_t) edge.data.castTo(ST_Agedgeinfo_t.class);
		ST_textlabel_t label = (ST_textlabel_t) data.label;
		if (label == null)
			return null;

		final BoxInfo boxInfo = BoxInfo.fromTextlabel(label);
		return URectangle.build(boxInfo.getDimension());
	}

	private UTranslate getLabelRectangleTranslate(String fieldName) {
		// final String fieldName = "label";
		final ST_Agedgeinfo_t data = (ST_Agedgeinfo_t) edge.data;
		ST_textlabel_t label = null;
		if (fieldName.equals("label"))
			label = data.label;
		else if (fieldName.equals("head_label"))
			label = data.head_label;
		else if (fieldName.equals("tail_label"))
			label = data.tail_label;

		if (label == null)
			return null;

		final BoxInfo boxInfo = BoxInfo.fromTextlabel(label);

		if (ymirror == null)
			return new UTranslate(boxInfo.getLowerLeft().getX(), boxInfo.getUpperRight().getY());

		return ymirror.getMirrored(UTranslate.point(boxInfo.getLowerLeft()));
	}

	private DotPath dotPath;

	private DotPath getDotPathInternal() {
		if (dotPath != null)
			return dotPath;

		final ST_Agedgeinfo_t data = (ST_Agedgeinfo_t) edge.data;
		final ST_splines splines = data.spl;

		dotPath = new DotPath();
		final ST_bezier beziers = (ST_bezier) splines.list.get__(0);
		final XPoint2D pt1 = getPoint(splines, 0);
		final XPoint2D pt2 = getPoint(splines, 1);
		final XPoint2D pt3 = getPoint(splines, 2);
		final XPoint2D pt4 = getPoint(splines, 3);
		dotPath = dotPath.addCurve(pt1, pt2, pt3, pt4);
		final int n = beziers.size;
		for (int i = 4; i < n; i += 3) {
			final XPoint2D ppt2 = getPoint(splines, i);
			final XPoint2D ppt3 = getPoint(splines, i + 1);
			final XPoint2D ppt4 = getPoint(splines, i + 2);
			dotPath = dotPath.addCurve(ppt2, ppt3, ppt4);
		}

		return dotPath;
	}

	private XPoint2D getPoint(ST_splines splines, int i) {
		final ST_bezier beziers = (ST_bezier) splines.list.get__(0);
		final ST_pointf pt = beziers.list.get__(i);
		return new XPoint2D(pt.x, pt.y);
	}

}
