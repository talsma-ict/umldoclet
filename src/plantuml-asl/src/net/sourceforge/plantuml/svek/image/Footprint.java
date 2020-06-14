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
package net.sourceforge.plantuml.svek.image;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.ugraphic.ColorMapper;
import net.sourceforge.plantuml.ugraphic.ColorMapperIdentity;
import net.sourceforge.plantuml.ugraphic.UChange;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UHorizontalLine;
import net.sourceforge.plantuml.ugraphic.UImage;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UParam;
import net.sourceforge.plantuml.ugraphic.UParamNull;
import net.sourceforge.plantuml.ugraphic.UPath;
import net.sourceforge.plantuml.ugraphic.UShape;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UText;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class Footprint {

	private final StringBounder stringBounder;

	public Footprint(StringBounder stringBounder) {
		this.stringBounder = stringBounder;

	}

	class MyUGraphic implements UGraphic {

		private final UTranslate translate;
		private final List<Point2D.Double> all;

		public double dpiFactor() {
			return 1;
		}

		private MyUGraphic(List<Point2D.Double> all, UTranslate translate) {
			this.all = all;
			this.translate = translate;
		}

		public boolean matchesProperty(String propertyName) {
			return false;
		}

		public MyUGraphic() {
			this(new ArrayList<Point2D.Double>(), new UTranslate());
		}

		public UGraphic apply(UChange change) {
			if (change instanceof UTranslate) {
				return new MyUGraphic(all, translate.compose((UTranslate) change));
			} else if (change instanceof UStroke || change instanceof UChangeColor) {
				return new MyUGraphic(all, translate);
			}
			throw new UnsupportedOperationException();
		}

		public StringBounder getStringBounder() {
			return stringBounder;
		}

		public UParam getParam() {
			return new UParamNull();
		}

		public void draw(UShape shape) {
			final double x = translate.getDx();
			final double y = translate.getDy();
			if (shape instanceof UText) {
				drawText(x, y, (UText) shape);
			} else if (shape instanceof UHorizontalLine) {
				// Definitively a Horizontal line
//				line.drawTitleInternalForFootprint(this, x, y);
			} else if (shape instanceof ULine) {
				// Probably a Horizontal line
			} else if (shape instanceof UImage) {
				drawImage(x, y, (UImage) shape);
			} else if (shape instanceof UPath) {
				drawPath(x, y, (UPath) shape);
			} else {
				throw new UnsupportedOperationException(shape.getClass().toString());
			}
		}

		public ColorMapper getColorMapper() {
			return new ColorMapperIdentity();
		}

		public void startUrl(Url url) {
		}

		public void closeAction() {
		}

		private void addPoint(double x, double y) {
			all.add(new Point2D.Double(x, y));
		}

		private void drawText(double x, double y, UText text) {
			final Dimension2D dim = stringBounder.calculateDimension(text.getFontConfiguration().getFont(),
					text.getText());
			y -= dim.getHeight() - 1.5;
			addPoint(x, y);
			addPoint(x, y + dim.getHeight());
			addPoint(x + dim.getWidth(), y);
			addPoint(x + dim.getWidth(), y + dim.getHeight());
		}

		private void drawImage(double x, double y, UImage image) {
			addPoint(x, y);
			addPoint(x, y + image.getHeight());
			addPoint(x + image.getWidth(), y);
			addPoint(x + image.getWidth(), y + image.getHeight());
		}

		private void drawPath(double x, double y, UPath path) {
			addPoint(x + path.getMinX(), y + path.getMinY());
			addPoint(x + path.getMaxX(), y + path.getMaxY());
		}

		public void flushUg() {
		}

	}

	public ContainingEllipse getEllipse(UDrawable drawable, double alpha) {
		final MyUGraphic ug = new MyUGraphic();
		drawable.drawU(ug);
		final List<Point2D.Double> all = ug.all;
		final ContainingEllipse circle = new ContainingEllipse(alpha);
		for (Point2D pt : all) {
			circle.append(pt);
		}
		return circle;
	}

	// public void drawDebug(UGraphic ug, double dx, double dy, TextBlock text) {
	// final MyUGraphic mug = new MyUGraphic();
	// text.drawU(mug, dx, dy);
	// for (Point2D pt : mug.all) {
	// ug.draw(pt.getX(), pt.getY(), new URectangle(1, 1));
	// }
	//
	// }

}
