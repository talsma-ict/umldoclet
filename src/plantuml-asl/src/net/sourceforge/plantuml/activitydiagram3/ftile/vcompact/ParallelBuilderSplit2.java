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
package net.sourceforge.plantuml.activitydiagram3.ftile.vcompact;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.activitydiagram3.ftile.AbstractConnection;
import net.sourceforge.plantuml.activitydiagram3.ftile.Arrows;
import net.sourceforge.plantuml.activitydiagram3.ftile.Connection;
import net.sourceforge.plantuml.activitydiagram3.ftile.ConnectionTranslatable;
import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileAssemblySimple;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileGeometry;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileKilled;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileUtils;
import net.sourceforge.plantuml.activitydiagram3.ftile.Snake;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.activitydiagram3.ftile.vertical.FtileThinSplit;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.HtmlColorAndStyle;
import net.sourceforge.plantuml.graphic.Rainbow;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class ParallelBuilderSplit2 extends ParallelFtilesBuilder {

	public ParallelBuilderSplit2(ISkinParam skinParam, StringBounder stringBounder, final List<Ftile> list,
			Ftile inner, Swimlane swimlane) {
		super(skinParam, stringBounder, list, inner, swimlane);
	}

	@Override
	protected Ftile doStep1() {
		Ftile result = getMiddle();
		final List<Connection> conns = new ArrayList<Connection>();
		final Rainbow thinColor = result.getInLinkRendering().getRainbow(HtmlColorAndStyle.build(skinParam()));
		final Ftile thin = new FtileThinSplit(skinParam(), getThin1Color(thinColor), getList().get(0).getSwimlaneIn());
		double x = 0;
		double first = 0;
		double last = 0;
		for (Ftile tmp : getList()) {
			final FtileGeometry dim = tmp.calculateDimension(getStringBounder());
			if (first == 0) {
				first = x + dim.getLeft();
			}
			last = x + dim.getLeft();
			final Rainbow rainbow = tmp.getInLinkRendering().getRainbow(HtmlColorAndStyle.build(skinParam()));
			conns.add(new ConnectionIn(thin, tmp, x, rainbow));
			x += dim.getWidth();
		}

		result = FtileUtils.addConnection(result, conns);
		final FtileGeometry geom = result.calculateDimension(getStringBounder());
		if (last < geom.getLeft()) {
			last = geom.getLeft();
		}
		if (first > geom.getLeft()) {
			first = geom.getLeft();
		}
		((FtileThinSplit) thin).setGeom(first, last, result.calculateDimension(getStringBounder()).getWidth());

		return new FtileAssemblySimple(thin, result);
	}

	private HtmlColor getThin1Color(final Rainbow thinColor) {
		for (Ftile tmp : getList()) {
			final Rainbow rainbow = tmp.getInLinkRendering().getRainbow(HtmlColorAndStyle.build(skinParam()));
			if (rainbow.isInvisible() == false) {
				return thinColor.getColor();
			}
		}
		return null;
	}

	private boolean hasOut() {
		for (Ftile tmp : getList()) {
			final boolean hasOutTmp = tmp.calculateDimension(getStringBounder()).hasPointOut();
			if (hasOutTmp) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected Ftile doStep2(Ftile result) {

		final FtileGeometry geom = result.calculateDimension(getStringBounder());
		if (hasOut() == false) {
			return new FtileKilled(result);
		}

		final Rainbow thinColor = result.getInLinkRendering().getRainbow(HtmlColorAndStyle.build(skinParam()));
		// final Ftile out = new FtileThinSplit(skinParam(), thinColor.getColor(), getList().get(0).getSwimlaneIn());
		final Ftile out = new FtileThinSplit(skinParam(), thinColor.getColor(), swimlane());
		result = new FtileAssemblySimple(result, out);
		final List<Connection> conns = new ArrayList<Connection>();
		double x = 0;
		double first = 0;
		double last = 0;
		for (Ftile tmp : getList()) {
			final UTranslate translate0 = new UTranslate(0, 1.5);
			final FtileGeometry dim = tmp.calculateDimension(getStringBounder());
			if (dim.hasPointOut()) {
				if (first == 0) {
					first = x + dim.getLeft();
				}
				last = x + dim.getLeft();
			}
			conns.add(new ConnectionOut(translate0, tmp, out, x, tmp.getOutLinkRendering().getRainbow(
					HtmlColorAndStyle.build(skinParam())), getHeightOfMiddle()));
			x += dim.getWidth();
		}
		if (last < geom.getLeft()) {
			last = geom.getLeft();
		}
		if (first > geom.getLeft()) {
			first = geom.getLeft();
		}
		((FtileThinSplit) out).setGeom(first, last, geom.getWidth());
		result = FtileUtils.addConnection(result, conns);
		return result;
	}

	class ConnectionIn extends AbstractConnection implements ConnectionTranslatable {

		private final double x;
		private final Rainbow arrowColor;
		private final Display label;

		public ConnectionIn(Ftile ftile1, Ftile ftile2, double x, Rainbow arrowColor) {
			super(ftile1, ftile2);
			label = ftile2.getInLinkRendering().getDisplay();
			this.x = x;
			this.arrowColor = arrowColor;
		}

		public void drawU(UGraphic ug) {
			ug = ug.apply(new UTranslate(x, 0));
			final FtileGeometry geo = getFtile2().calculateDimension(getStringBounder());
			final Snake snake = new Snake(arrowHorizontalAlignment(), arrowColor, Arrows.asToDown());
			if (Display.isNull(label) == false) {
				snake.setLabel(getTextBlock(label));
			}
			snake.addPoint(geo.getLeft(), 0);
			snake.addPoint(geo.getLeft(), geo.getInY());
			ug.draw(snake);
		}

		public void drawTranslate(UGraphic ug, UTranslate translate1, UTranslate translate2) {
			ug = ug.apply(new UTranslate(x, 0));
			final FtileGeometry geo = getFtile2().calculateDimension(getStringBounder());
			final Point2D p1 = new Point2D.Double(geo.getLeft(), 0);
			final Point2D p2 = new Point2D.Double(geo.getLeft(), geo.getInY());

			final Snake snake = new Snake(arrowHorizontalAlignment(), arrowColor, Arrows.asToDown());
			if (Display.isNull(label) == false) {
				snake.setLabel(getTextBlock(label));
			}
			final Point2D mp1a = translate1.getTranslated(p1);
			final Point2D mp2b = translate2.getTranslated(p2);
			final double middle = mp1a.getY() + 4;
			snake.addPoint(mp1a);
			snake.addPoint(mp1a.getX(), middle);
			snake.addPoint(mp2b.getX(), middle);
			snake.addPoint(mp2b);
			ug.draw(snake);
		}
	}

	class ConnectionOut extends AbstractConnection implements ConnectionTranslatable {

		private final double x;
		private final Rainbow arrowColor;
		private final double height;
		private final Display label;
		private final UTranslate translate0;

		public ConnectionOut(UTranslate translate0, Ftile ftile1, Ftile ftile2, double x, Rainbow arrowColor,
				double height) {
			super(ftile1, ftile2);
			this.translate0 = translate0;
			this.label = ftile1.getOutLinkRendering().getDisplay();
			this.x = x;
			this.arrowColor = arrowColor;
			this.height = height;
		}

		public void drawU(UGraphic ug) {
			ug = ug.apply(new UTranslate(x, 0));
			final FtileGeometry geo = getFtile1().calculateDimension(getStringBounder());
			if (geo.hasPointOut() == false) {
				return;
			}
			final Snake snake = new Snake(arrowHorizontalAlignment(), arrowColor, Arrows.asToDown());
			if (Display.isNull(label) == false) {
				snake.setLabel(getTextBlock(label));
			}
			final Point2D p1 = translate0.getTranslated(new Point2D.Double(geo.getLeft(), geo.getOutY()));
			final Point2D p2 = translate0.getTranslated(new Point2D.Double(geo.getLeft(), height));
			snake.addPoint(p1);
			snake.addPoint(p2);
			ug.draw(snake);
		}

		public void drawTranslate(UGraphic ug, UTranslate translate1, UTranslate translate2) {
			ug = ug.apply(new UTranslate(x, 0));
			final FtileGeometry geo = getFtile1().calculateDimension(getStringBounder());
			if (geo.hasPointOut() == false) {
				return;
			}
			final Point2D p1 = translate0.getTranslated(new Point2D.Double(geo.getLeft(), geo.getOutY()));
			final Point2D p2 = translate0.getTranslated(new Point2D.Double(geo.getLeft(), height));

			final Snake snake = new Snake(arrowHorizontalAlignment(), arrowColor, Arrows.asToDown());
			if (Display.isNull(label) == false) {
				snake.setLabel(getTextBlock(label));
			}
			final Point2D mp1a = translate1.getTranslated(p1);
			final Point2D mp2b = translate2.getTranslated(p2);
			final double middle = mp2b.getY() - 14;
			snake.addPoint(mp1a);
			snake.addPoint(mp1a.getX(), middle);
			snake.addPoint(mp2b.getX(), middle);
			snake.addPoint(mp2b);
			ug.draw(snake);
		}

	}

}
