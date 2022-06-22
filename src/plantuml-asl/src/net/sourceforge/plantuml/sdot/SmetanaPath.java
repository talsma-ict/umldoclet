/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2023, Arnaud Roques
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

import java.awt.geom.Point2D;

import h.ST_Agedge_s;
import h.ST_Agedgeinfo_t;
import h.ST_bezier;
import h.ST_pointf;
import h.ST_splines;
import h.ST_textlabel_t;
import net.sourceforge.plantuml.LineParam;
import net.sourceforge.plantuml.cucadiagram.CucaDiagram;
import net.sourceforge.plantuml.cucadiagram.Link;
import net.sourceforge.plantuml.cucadiagram.LinkType;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.posimo.DotPath;
import net.sourceforge.plantuml.skin.rose.Rose;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.svek.extremity.ExtremityFactory;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorNone;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;
import smetana.core.Macro;

public class SmetanaPath implements UDrawable {

	private final Link link;
	private final ST_Agedge_s edge;
	private final YMirror ymirror;
	private final CucaDiagram diagram;
	private final TextBlock label;
	private final TextBlock headLabel;
	private final TextBlock tailLabel;
	private final Rose rose = new Rose();

	public SmetanaPath(Link link, ST_Agedge_s edge, YMirror ymirror, CucaDiagram diagram, TextBlock label,
			TextBlock tailLabel, TextBlock headLabel) {
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

		HColor color = StyleSignatureBasic
				.of(SName.root, SName.element, diagram.getUmlDiagramType().getStyleName(), SName.arrow)
				.getMergedStyle(diagram.getSkinParam().getCurrentStyleBuilder()).value(PName.LineColor)
				.asColor(diagram.getSkinParam().getThemeStyle(), diagram.getSkinParam().getIHtmlColorSet());

		if (this.link.getColors() != null) {
			final HColor newColor = this.link.getColors().getColor(ColorType.ARROW, ColorType.LINE);
			if (newColor != null)
				color = newColor;
		} else if (this.link.getSpecificColor() != null)
			color = this.link.getSpecificColor();

		DotPath dotPath = getDotPath(edge);
		if (ymirror != null && dotPath != null)
			dotPath = ymirror.getMirrored(dotPath);

		if (dotPath != null) {
			final LinkType linkType = link.getType();
			UStroke stroke = linkType.getStroke3(diagram.getSkinParam().getThickness(LineParam.arrow, null));
			if (link.getColors() != null && link.getColors().getSpecificLineStroke() != null)
				stroke = link.getColors().getSpecificLineStroke();

			ug.apply(stroke).apply(color).draw(dotPath);
			printExtremityAtStart(ug.apply(color));
			printExtremityAtEnd(ug.apply(color));
		}
		if (getLabelRectangleTranslate("label") != null)
			label.drawU(ug.apply(getLabelRectangleTranslate("label")));

		if (getLabelRectangleTranslate("head_label") != null)
			headLabel.drawU(ug.apply(getLabelRectangleTranslate("head_label")));

		if (getLabelRectangleTranslate("tail_label") != null)
			tailLabel.drawU(ug.apply(getLabelRectangleTranslate("tail_label")));

		// printDebug(ug);

	}

	private void printExtremityAtStart(UGraphic ug) {
		final ExtremityFactory extremityFactory2 = link.getType().getDecor2()
				.getExtremityFactoryComplete(diagram.getSkinParam().getBackgroundColor());
		if (extremityFactory2 == null)
			return;

		final ST_splines splines = getSplines(edge);
		DotPath s = getDotPath(splines);
		Point2D p0 = s.getStartPoint();
		double startAngle = s.getStartAngle();
		if (ymirror != null) {
			p0 = ymirror.getMirrored(p0);
			startAngle = -startAngle + Math.PI;
		}
		try {
			final UDrawable extremity2 = extremityFactory2.createUDrawable(p0, startAngle, null);
			if (extremity2 != null)
				extremity2.drawU(ug);

		} catch (UnsupportedOperationException e) {
			System.err.println("CANNOT DRAW printExtremityAtStart");
		}
	}

	private void printExtremityAtEnd(UGraphic ug) {
		final ExtremityFactory extremityFactory1 = link.getType().getDecor1()
				.getExtremityFactoryComplete(diagram.getSkinParam().getBackgroundColor());
		if (extremityFactory1 == null)
			return;

		final ST_splines splines = getSplines(edge);
		DotPath s = getDotPath(splines);
		Point2D p0 = s.getEndPoint();
		double endAngle = s.getEndAngle();
		if (ymirror != null) {
			p0 = ymirror.getMirrored(p0);
			endAngle = -endAngle;
		}
		try {
			final UDrawable extremity1 = extremityFactory1.createUDrawable(p0, endAngle, null);
			if (extremity1 != null)
				extremity1.drawU(ug);

		} catch (UnsupportedOperationException e) {
			System.err.println("CANNOT DRAW printExtremityAtEnd");
		}
	}

	private void printDebug(UGraphic ug) {
		ug = ug.apply(HColorUtils.BLUE).apply(HColorUtils.BLUE.bg());
		final ST_splines splines = getSplines(edge);
		final ST_bezier beziers = splines.list.get__(0);
		for (int i = 0; i < beziers.size; i++) {
			Point2D pt = getPoint(splines, i);
			if (ymirror != null)
				pt = ymirror.getMirrored(pt);

			ug.apply(new UTranslate(pt).compose(new UTranslate(-1, -1))).draw(new UEllipse(3, 3));
		}
		if (getLabelRectangleTranslate("label") != null && getLabelURectangle() != null) {
			ug = ug.apply(HColorUtils.BLUE).apply(new HColorNone().bg());
			ug.apply(getLabelRectangleTranslate("label")).draw(getLabelURectangle());
		}

	}

	private URectangle getLabelURectangle() {
		final ST_Agedgeinfo_t data = (ST_Agedgeinfo_t) Macro.AGDATA(edge).castTo(ST_Agedgeinfo_t.class);
		ST_textlabel_t label = (ST_textlabel_t) data.label;
		if (label == null)
			return null;

		final ST_pointf dimen = (ST_pointf) label.dimen;
		final ST_pointf space = (ST_pointf) label.space;
		final ST_pointf pos = (ST_pointf) label.pos;
		final double x = pos.x;
		final double y = pos.y;
		final double width = dimen.x;
		final double height = dimen.y;
		return new URectangle(width, height);
	}

	private UTranslate getLabelRectangleTranslate(String fieldName) {
		// final String fieldName = "label";
		final ST_Agedgeinfo_t data = (ST_Agedgeinfo_t) Macro.AGDATA(edge);
		ST_textlabel_t label = null;
		if (fieldName.equals("label"))
			label = data.label;
		else if (fieldName.equals("head_label"))
			label = data.head_label;
		else if (fieldName.equals("tail_label"))
			label = data.tail_label;

		if (label == null)
			return null;

		final ST_pointf dimen = (ST_pointf) label.dimen;
		final ST_pointf space = (ST_pointf) label.space;
		final ST_pointf pos = (ST_pointf) label.pos;
		final double x = pos.x;
		final double y = pos.y;
		final double width = dimen.x;
		final double height = dimen.y;

		if (ymirror == null)
			return new UTranslate(x - width / 2, y - height / 2);

		return ymirror.getMirrored(new UTranslate(x - width / 2, y + height / 2));
	}

	public DotPath getDotPath(ST_Agedge_s e) {
		final ST_splines splines = getSplines(e);
		return getDotPath(splines);
	}

	private ST_splines getSplines(ST_Agedge_s e) {
		final ST_Agedgeinfo_t data = (ST_Agedgeinfo_t) Macro.AGDATA(e);
		final ST_splines splines = (ST_splines) data.spl;
		return splines;
	}

	private DotPath getDotPath(ST_splines splines) {
		if (splines == null) {
			System.err.println("ERROR, no splines for getDotPath");
			return null;
		}
		DotPath result = new DotPath();
		final ST_bezier beziers = (ST_bezier) splines.list.get__(0);
		final Point2D pt1 = getPoint(splines, 0);
		final Point2D pt2 = getPoint(splines, 1);
		final Point2D pt3 = getPoint(splines, 2);
		final Point2D pt4 = getPoint(splines, 3);
		result = result.addCurve(pt1, pt2, pt3, pt4);
		final int n = beziers.size;
		for (int i = 4; i < n; i += 3) {
			final Point2D ppt2 = getPoint(splines, i);
			final Point2D ppt3 = getPoint(splines, i + 1);
			final Point2D ppt4 = getPoint(splines, i + 2);
			result = result.addCurve(ppt2, ppt3, ppt4);
		}
		return result;
	}

	private Point2D getPoint(ST_splines splines, int i) {
		final ST_bezier beziers = (ST_bezier) splines.list.get__(0);
		final ST_pointf pt = beziers.list.get__(i);
		return new Point2D.Double(pt.x, pt.y);
	}

}
