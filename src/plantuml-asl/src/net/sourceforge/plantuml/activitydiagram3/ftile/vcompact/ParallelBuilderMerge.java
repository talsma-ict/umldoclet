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
package net.sourceforge.plantuml.activitydiagram3.ftile.vcompact;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.UseStyle;
import net.sourceforge.plantuml.activitydiagram3.ftile.AbstractConnection;
import net.sourceforge.plantuml.activitydiagram3.ftile.Arrows;
import net.sourceforge.plantuml.activitydiagram3.ftile.Connection;
import net.sourceforge.plantuml.activitydiagram3.ftile.ConnectionTranslatable;
import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileAssemblySimple;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileGeometry;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileUtils;
import net.sourceforge.plantuml.activitydiagram3.ftile.Snake;
import net.sourceforge.plantuml.activitydiagram3.ftile.vertical.FtileBlackBlock;
import net.sourceforge.plantuml.activitydiagram3.ftile.vertical.FtileDiamond;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.Rainbow;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UPolygon;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class ParallelBuilderMerge extends AbstractParallelFtilesBuilder {

	public ParallelBuilderMerge(ISkinParam skinParam, StringBounder stringBounder, List<Ftile> all) {
		super(skinParam, stringBounder, all);
	}

	@Override
	protected Ftile doStep1(Ftile inner) {
		Ftile result = inner;
		final List<Connection> conns = new ArrayList<>();
		final HColor colorBar = getRose().getHtmlColor(skinParam(), ColorParam.activityBar);

		final Ftile black = new FtileBlackBlock(skinParam(), colorBar, list99.get(0).getSwimlaneIn());
		double x = 0;
		for (Ftile tmp : list99) {
			final Dimension2D dim = tmp.calculateDimension(getStringBounder());
			final Rainbow def;
			if (UseStyle.useBetaStyle()) {
				Style style = getDefaultStyleDefinition().getMergedStyle(skinParam().getCurrentStyleBuilder());
				def = Rainbow.build(style, skinParam().getIHtmlColorSet(), skinParam().getThemeStyle());
			} else {
				def = Rainbow.build(skinParam());
			}
			final Rainbow rainbow = tmp.getInLinkRendering().getRainbow(def);
			conns.add(new ConnectionIn(black, tmp, x, rainbow));
			x += dim.getWidth();
		}

		result = FtileUtils.addConnection(result, conns);
		((FtileBlackBlock) black).setBlackBlockDimension(result.calculateDimension(getStringBounder()).getWidth(),
				barHeight);

		return new FtileAssemblySimple(black, result);
	}

	@Override
	protected Ftile doStep2(Ftile inner, Ftile result) {
		final HColor borderColor = getRose().getHtmlColor(skinParam(), ColorParam.activityDiamondBorder);
		final HColor backColor = getRose().getHtmlColor(skinParam(), ColorParam.activityDiamondBackground);
		final Ftile out = new FtileDiamond(skinParam(), backColor, borderColor, swimlaneOutForStep2());
		result = new FtileAssemblySimple(result, out);
		final List<Connection> conns = new ArrayList<>();
		final UTranslate diamondTranslate = result.getTranslateFor(out, getStringBounder());

		double x = 0;
		for (Ftile tmp : list99) {
			final Dimension2D dim = tmp.calculateDimension(getStringBounder());
			final UTranslate translate0 = new UTranslate(x, barHeight);
			final Rainbow def;
			if (UseStyle.useBetaStyle()) {
				Style style = getDefaultStyleDefinition().getMergedStyle(skinParam().getCurrentStyleBuilder());
				def = Rainbow.build(style, skinParam().getIHtmlColorSet(), skinParam().getThemeStyle());
			} else {
				def = Rainbow.build(skinParam());
			}
			final Rainbow rainbow = tmp.getOutLinkRendering().getRainbow(def);
			if (tmp.calculateDimension(getStringBounder()).hasPointOut()) {
				conns.add(new ConnectionHorizontalThenVertical(tmp, out, rainbow, translate0, diamondTranslate));
			}
			x += dim.getWidth();

		}
		return FtileUtils.addConnection(result, conns);
	}

	class ConnectionHorizontalThenVertical extends AbstractConnection /* implements ConnectionTranslatable */ {

		private final Rainbow arrowColor;
		private final UTranslate diamondTranslate;
		private final UTranslate translate0;

		public ConnectionHorizontalThenVertical(Ftile tile, Ftile diamond, Rainbow arrowColor, UTranslate translate0,
				UTranslate diamondTranslate) {
			super(tile, diamond);
			this.arrowColor = arrowColor;
			this.diamondTranslate = diamondTranslate;
			this.translate0 = translate0;
		}

		public void drawU(UGraphic ug) {
			final StringBounder stringBounder = ug.getStringBounder();
			final Point2D p1 = getP1(stringBounder);
			final Point2D p2 = getP2(stringBounder, p1.getX());
			final double x1 = p1.getX();
			final double y1 = p1.getY();
			final double x2 = p2.getX();
			final double y2 = p2.getY();

			final UTranslate arrival = arrivalOnDiamond(stringBounder, p1.getX());
			final UPolygon endDecoration;
			if (arrival.getDx() < 0) {
				endDecoration = Arrows.asToRight();
			} else if (arrival.getDx() > 0) {
				endDecoration = Arrows.asToLeft();
			} else {
				endDecoration = Arrows.asToDown();
			}
			final Snake snake = Snake.create(arrowColor, endDecoration);
			snake.addPoint(x1, y1);
			snake.addPoint(x1, y2);
			snake.addPoint(x2, y2);

			ug.draw(snake);
		}

		private Point2D getP1(StringBounder stringBounder) {
			return translate0.getTranslated(getFtile1().calculateDimension(stringBounder).getPointOut());
		}

		private Point2D getP2(StringBounder stringBounder, double startX) {
			final UTranslate arrival = arrivalOnDiamond(stringBounder, startX);
			return arrival.getTranslated(getDiamondOut(stringBounder));
		}

		public Point2D getDiamondOut(StringBounder stringBounder) {
			return diamondTranslate.getTranslated(getFtile2().calculateDimension(stringBounder).getPointOut());
		}

		public UTranslate arrivalOnDiamond(StringBounder stringBounder, double startX) {
			final Point2D result = getDiamondOut(stringBounder);
			final Dimension2D dim = getFtile2().calculateDimension(stringBounder);
			final double a = result.getX() - dim.getWidth() / 2;
			final double b = result.getX() + dim.getWidth() / 2;

			final UTranslate arrival;
			if (startX < a) {
				arrival = new UTranslate(-dim.getWidth() / 2, -dim.getHeight() / 2);
			} else if (startX > b) {
				arrival = new UTranslate(dim.getWidth() / 2, -dim.getHeight() / 2);
			} else {
				arrival = new UTranslate(0, -dim.getHeight());
			}
			return arrival;
		}

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
			ug = ug.apply(UTranslate.dx(x));
			final FtileGeometry geo = getFtile2().calculateDimension(getStringBounder());
			Snake snake = Snake.create(arrowColor, Arrows.asToDown());
			if (Display.isNull(label) == false) {
				snake = snake.withLabel(getTextBlock(label), arrowHorizontalAlignment());
			}
			snake.addPoint(geo.getLeft(), 0);
			snake.addPoint(geo.getLeft(), geo.getInY());
			ug.draw(snake);
		}

		public void drawTranslate(UGraphic ug, UTranslate translate1, UTranslate translate2) {
			ug = ug.apply(UTranslate.dx(x));
			final FtileGeometry geo = getFtile2().calculateDimension(getStringBounder());
			final Point2D p1 = new Point2D.Double(geo.getLeft(), 0);
			final Point2D p2 = new Point2D.Double(geo.getLeft(), geo.getInY());

			Snake snake = Snake.create(arrowColor, Arrows.asToDown());
			if (Display.isNull(label) == false) {
				snake = snake.withLabel(getTextBlock(label), arrowHorizontalAlignment());
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

}
