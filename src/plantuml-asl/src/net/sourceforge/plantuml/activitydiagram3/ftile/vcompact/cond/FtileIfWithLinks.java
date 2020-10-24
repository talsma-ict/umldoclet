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
package net.sourceforge.plantuml.activitydiagram3.ftile.vcompact.cond;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.Direction;
import net.sourceforge.plantuml.activitydiagram3.Branch;
import net.sourceforge.plantuml.activitydiagram3.LinkRendering;
import net.sourceforge.plantuml.activitydiagram3.ftile.AbstractConnection;
import net.sourceforge.plantuml.activitydiagram3.ftile.Arrows;
import net.sourceforge.plantuml.activitydiagram3.ftile.Connection;
import net.sourceforge.plantuml.activitydiagram3.ftile.ConnectionTranslatable;
import net.sourceforge.plantuml.activitydiagram3.ftile.Diamond;
import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileGeometry;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileUtils;
import net.sourceforge.plantuml.activitydiagram3.ftile.MergeStrategy;
import net.sourceforge.plantuml.activitydiagram3.ftile.Snake;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.activitydiagram3.ftile.vcompact.UGraphicInterceptorOneSwimlane;
import net.sourceforge.plantuml.graphic.Rainbow;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.svek.ConditionEndStyle;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UPolygon;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class FtileIfWithLinks extends FtileIfWithDiamonds {

	private final ConditionEndStyle conditionEndStyle;
	private final Rainbow arrowColor;

	public FtileIfWithLinks(Ftile diamond1, Ftile tile1, Ftile tile2, Ftile diamond2, Swimlane in, Rainbow arrowColor,
			ConditionEndStyle conditionEndStyle, StringBounder stringBounder) {
		super(diamond1, tile1, tile2, diamond2, in, stringBounder);
		this.arrowColor = arrowColor;
		this.conditionEndStyle = conditionEndStyle;
		if (arrowColor.size() == 0) {
			throw new IllegalArgumentException();
		}
	}

	public static Rainbow getInColor(Branch branch, Rainbow arrowColor) {
		if (branch.isEmpty()) {
			return branch.getFtile().getOutLinkRendering().getRainbow(arrowColor);
		}
		final LinkRendering linkIn = branch.getFtile().getInLinkRendering();
		final Rainbow color = linkIn == null ? arrowColor : linkIn.getRainbow();
		if (color.size() == 0) {
			return arrowColor;
		}
		return color;
	}

	class ConnectionHorizontalThenVertical extends AbstractConnection implements ConnectionTranslatable {

		private final Rainbow color;
		private final UPolygon usingArrow;

		public ConnectionHorizontalThenVertical(Ftile tile, Branch branch) {
			super(diamond1, tile);
			color = getInColor(branch, arrowColor);
			if (color.size() == 0) {
				getInColor(branch, arrowColor);
				throw new IllegalArgumentException();
			}
			usingArrow = branch.isEmpty() ? null : Arrows.asToDown();
		}

		public void drawU(UGraphic ug) {
			final StringBounder stringBounder = ug.getStringBounder();
			final Point2D p1 = getP1(stringBounder);
			final Point2D p2 = getP2(stringBounder);
			final double x1 = p1.getX();
			final double y1 = p1.getY();
			final double x2 = p2.getX();
			final double y2 = p2.getY();

			final Snake snake = Snake.create(color, usingArrow);
			snake.addPoint(x1, y1);
			snake.addPoint(x2, y1);
			snake.addPoint(x2, y2);

			ug.draw(snake);
		}

		private Point2D getP1(StringBounder stringBounder) {
			final FtileGeometry dimDiamond1 = diamond1.calculateDimension(stringBounder);
			final Point2D pt;
			if (getFtile2() == tile1) {
				pt = dimDiamond1.getPointD();
			} else if (getFtile2() == tile2) {
				pt = dimDiamond1.getPointB();
			} else {
				throw new IllegalStateException();
			}
			return getTranslateDiamond1(stringBounder).getTranslated(pt);
		}

		private Point2D getP2(final StringBounder stringBounder) {
			return translate(stringBounder).getTranslated(getFtile2().calculateDimension(stringBounder).getPointIn());
		}

		private UTranslate translate(StringBounder stringBounder) {
			if (getFtile2() == tile1) {
				return getTranslate1(stringBounder);
			}
			if (getFtile2() == tile2) {
				return getTranslate2(stringBounder);
			}
			throw new IllegalStateException();
		}

		public void drawTranslate(UGraphic ug, UTranslate translate1, UTranslate translate2) {
			final StringBounder stringBounder = ug.getStringBounder();
			Point2D p1 = getP1(stringBounder);
			Point2D p2 = getP2(stringBounder);
			final Direction originalDirection = Direction.leftOrRight(p1, p2);
			p1 = translate1.getTranslated(p1);
			p2 = translate2.getTranslated(p2);
			final Direction newDirection = Direction.leftOrRight(p1, p2);
			if (originalDirection != newDirection) {
				final double delta = (originalDirection == Direction.RIGHT ? -1 : 1) * Diamond.diamondHalfSize;
				final Dimension2D dimDiamond1 = diamond1.calculateDimension(stringBounder);
				final Snake small = Snake.create(color);
				small.addPoint(p1);
				small.addPoint(p1.getX() + delta, p1.getY());
				small.addPoint(p1.getX() + delta, p1.getY() + dimDiamond1.getHeight() * .75);
				ug.draw(small);
				p1 = small.getLast();
			}
			final Snake snake = Snake.create(color, usingArrow)
					.withMerge(MergeStrategy.LIMITED);
			snake.addPoint(p1);
			snake.addPoint(p2.getX(), p1.getY());
			snake.addPoint(p2);
			ug.draw(snake);

		}

	}

	class ConnectionVerticalThenHorizontal extends AbstractConnection implements ConnectionTranslatable {
		private final Rainbow myArrowColor;
		private final boolean branchEmpty;

		public ConnectionVerticalThenHorizontal(Ftile tile, Rainbow myArrowColor, boolean branchEmpty) {
			super(tile, diamond2);
			this.myArrowColor = myArrowColor == null || myArrowColor.size() == 0 ? arrowColor : myArrowColor;
			this.branchEmpty = branchEmpty;
		}

		public void drawU(UGraphic ug) {
			final StringBounder stringBounder = ug.getStringBounder();

			final FtileGeometry geo = getFtile1().calculateDimension(stringBounder);
			if (geo.hasPointOut() == false) {
				return;
			}
			final Point2D p1 = geo.translate(translate(stringBounder)).getPointOut();
			final Point2D p2 = getP2(stringBounder);

			final double x1 = p1.getX();
			final double y1 = p1.getY();
			final double x2 = p2.getX();
			final double y2 = p2.getY();

			final UPolygon arrow = x2 > x1 ? Arrows.asToRight() : Arrows.asToLeft();
			Snake snake = Snake.create(myArrowColor, arrow);
			if (branchEmpty) {
				snake = snake.emphasizeDirection(Direction.DOWN);
			}
			snake.addPoint(x1, y1);
			snake.addPoint(x1, y2);
			snake.addPoint(x2, y2);

			ug.draw(snake);
		}

		private Point2D getP2(StringBounder stringBounder) {
			final FtileGeometry dimDiamond2 = diamond2.calculateDimension(stringBounder);
			final Point2D pt;
			if (getFtile1() == tile1) {
				pt = dimDiamond2.getPointD();
			} else if (getFtile1() == tile2) {
				pt = dimDiamond2.getPointB();
			} else {
				throw new IllegalStateException();
			}
			return getTranslateDiamond2(stringBounder).getTranslated(pt);
		}

		private UTranslate translate(StringBounder stringBounder) {
			if (getFtile1() == tile1) {
				return getTranslate1(stringBounder);
			}
			if (getFtile1() == tile2) {
				return getTranslate2(stringBounder);
			}
			throw new IllegalStateException();
		}

		public void drawTranslate(UGraphic ug, UTranslate translate1, UTranslate translate2) {
			final StringBounder stringBounder = ug.getStringBounder();
			final FtileGeometry geo = getFtile1().calculateDimension(stringBounder);
			if (geo.hasPointOut() == false) {
				return;
			}
			final Point2D p2 = getP2(stringBounder);
			final Point2D p1 = geo.translate(translate(stringBounder)).getPointOut();
			final Direction originalDirection = Direction.leftOrRight(p1, p2);

			final double x1 = p1.getX();
			final double x2 = p2.getX();
			final Point2D mp1a = translate1.getTranslated(p1);
			final Point2D mp2b = translate2.getTranslated(p2);
			final Direction newDirection = Direction.leftOrRight(mp1a, mp2b);
			final UPolygon arrow = x2 > x1 ? Arrows.asToRight() : Arrows.asToLeft();
			if (originalDirection == newDirection) {
				final double delta = (x2 > x1 ? -1 : 1) * 1.5 * Diamond.diamondHalfSize;
				final Point2D mp2bc = new Point2D.Double(mp2b.getX() + delta, mp2b.getY());
				final Snake snake = Snake.create(myArrowColor)
						.withMerge(MergeStrategy.LIMITED);
				final double middle = (mp1a.getY() + mp2b.getY()) / 2.0;
				snake.addPoint(mp1a);
				snake.addPoint(mp1a.getX(), middle);
				snake.addPoint(mp2bc.getX(), middle);
				snake.addPoint(mp2bc);
				ug.draw(snake);
				final Snake small = Snake.create(myArrowColor, arrow)
						.withMerge(MergeStrategy.LIMITED);
				small.addPoint(mp2bc);
				small.addPoint(mp2bc.getX(), mp2b.getY());
				small.addPoint(mp2b);
				ug.draw(small);
			} else {
				final double delta = (x2 > x1 ? -1 : 1) * 1.5 * Diamond.diamondHalfSize;
				final Point2D mp2bb = new Point2D.Double(mp2b.getX() + delta,
						mp2b.getY() - 1.5 * Diamond.diamondHalfSize);
				final Snake snake = Snake.create(myArrowColor)
						.withMerge(MergeStrategy.LIMITED);
				snake.addPoint(mp1a);
				snake.addPoint(mp1a.getX(), mp2bb.getY());
				snake.addPoint(mp2bb);
				ug.draw(snake);
				final Snake small = Snake.create(myArrowColor, arrow)
						.withMerge(MergeStrategy.LIMITED);
				small.addPoint(mp2bb);
				small.addPoint(mp2bb.getX(), mp2b.getY());
				small.addPoint(mp2b);
				ug.draw(small);

			}

		}
	}

	class ConnectionVerticalThenHorizontalDirect extends AbstractConnection implements ConnectionTranslatable {
		private final Rainbow myArrowColor;
		private final boolean branchEmpty;

		public ConnectionVerticalThenHorizontalDirect(Ftile tile, Rainbow myArrowColor, boolean branchEmpty) {
			super(tile, diamond2);
			this.myArrowColor = myArrowColor == null || myArrowColor.size() == 0 ? arrowColor : myArrowColor;
			this.branchEmpty = branchEmpty;
		}

		public void drawU(UGraphic ug) {

			final StringBounder stringBounder = ug.getStringBounder();
			final FtileGeometry dimTotal = calculateDimensionInternal(stringBounder);

			final FtileGeometry geo = getFtile1().calculateDimension(stringBounder);
			if (geo.hasPointOut() == false) {
				return;
			}
			final Point2D p1 = geo.translate(translate(stringBounder)).getPointOut();
			final Point2D p2 = new Point2D.Double(dimTotal.getLeft(), dimTotal.getHeight());

			final double x1 = p1.getX();
			final double y1 = p1.getY();
			final double x2 = p2.getX();
			final double y2 = p2.getY();

			Snake snake = Snake.create(myArrowColor);
			if (branchEmpty) {
				snake = snake.emphasizeDirection(Direction.DOWN);
			}
			snake.addPoint(x1, y1);
			snake.addPoint(x1, y2);
			snake.addPoint(x2, y2);
			snake.addPoint(x2, dimTotal.getHeight());

			ug.draw(snake);
		}

		public void drawTranslate(UGraphic ug, UTranslate translate1, UTranslate translate2) {
			final StringBounder stringBounder = ug.getStringBounder();
			final FtileGeometry dimTotal = calculateDimensionInternal(stringBounder);

			final FtileGeometry geo = getFtile1().calculateDimension(stringBounder);
			if (geo.hasPointOut() == false) {
				return;
			}
			final Point2D p1 = geo.translate(translate(stringBounder)).getPointOut();
			final Point2D p2 = new Point2D.Double(dimTotal.getLeft(), dimTotal.getHeight() - Diamond.diamondHalfSize);

			final Point2D mp1a = translate1.getTranslated(p1);
			final Point2D mp2b = translate2.getTranslated(p2);

			final Snake snake = Snake.create(myArrowColor).withMerge(MergeStrategy.LIMITED);
			// snake.emphasizeDirection(Direction.DOWN);

			final double x1 = mp1a.getX();
			final double x2 = mp2b.getX();
			final double y2 = mp2b.getY();

			snake.addPoint(mp1a);
			snake.addPoint(x1, y2);
			snake.addPoint(mp2b);
			snake.addPoint(x2, dimTotal.getHeight());

			ug.draw(snake);
		}

		private UTranslate translate(StringBounder stringBounder) {
			if (getFtile1() == tile1) {
				return getTranslate1(stringBounder);
			}
			if (getFtile1() == tile2) {
				return getTranslate2(stringBounder);
			}
			throw new IllegalStateException();
		}

	}

	// copied from FtileIfLongHorizontal to use with ConditionEndStyle.HLINE
	class ConnectionVerticalOut extends AbstractConnection {

		private final Rainbow color;
		private final TextBlock out2;

		public ConnectionVerticalOut(Ftile tile, Rainbow color, TextBlock out2) {
			super(tile, null);
			this.color = color;
			this.out2 = out2;
		}

		public void drawU(UGraphic ug) {
			final StringBounder stringBounder = ug.getStringBounder();

			final FtileGeometry geo = getFtile1().calculateDimension(stringBounder);
			if (geo.hasPointOut() == false) {
				return;
			}

			final Point2D p1 = geo.translate(translate(stringBounder)).getPointOut();

			final double totalHeight = calculateDimensionInternal(stringBounder).getHeight();
			// final Point2D p1 = getP1(stringBounder);
			if (p1 == null) {
				return;
			}
			final Point2D p2 = new Point2D.Double(p1.getX(), totalHeight);

			final Snake snake = Snake.create(color, Arrows.asToDown()).withLabel(out2, arrowHorizontalAlignment());
			snake.addPoint(p1);
			snake.addPoint(p2);
			ug.draw(snake);
		}

		private UTranslate translate(StringBounder stringBounder) {
			if (getFtile1() == tile1) {
				return getTranslate1(stringBounder);
			}
			if (getFtile1() == tile2) {
				return getTranslate2(stringBounder);
			}
			throw new IllegalStateException();
		}
		/*
		 * private Point2D getP1(StringBounder stringBounder) {
		 * 
		 * final FtileGeometry geo = getFtile1().calculateDimension(stringBounder); if
		 * (geo.hasPointOut() == false) { return null; } final Point2D p =
		 * geo.getPointOut(); return getTranslate1(stringBounder).getTranslated(p); }
		 */
	}

	// copied from FtileIfLongHorizontal to use with ConditionEndStyle.HLINE
	class ConnectionHline extends AbstractConnection {

		private final Rainbow arrowColor;

		public ConnectionHline(Rainbow arrowColor) {
			super(null, null);
			this.arrowColor = arrowColor;
		}

		public void drawU(UGraphic ug) {
			final StringBounder stringBounder = ug.getStringBounder();
			final Dimension2D totalDim = calculateDimensionInternal(stringBounder);

			final Swimlane intoSw;
			if (ug instanceof UGraphicInterceptorOneSwimlane) {
				intoSw = ((UGraphicInterceptorOneSwimlane) ug).getSwimlane();
			} else {
				intoSw = null;
			}

			final List<Ftile> all = new ArrayList<Ftile>();
			all.add(tile1);
			all.add(tile2);
			double minX = totalDim.getWidth() / 2;
			double maxX = totalDim.getWidth() / 2;
			boolean atLeastOne = false;
			for (Ftile tmp : all) {
				if (tmp.calculateDimension(stringBounder).hasPointOut() == false) {
					continue;
				}
				if (intoSw != null && tmp.getSwimlanes().contains(intoSw) == false) {
					continue;
				}
				if (intoSw != null && tmp.getSwimlaneOut() != intoSw) {
					continue;
				}
				atLeastOne = true;
				final UTranslate ut = getTranslateFor(tmp, stringBounder);
				final double out = tmp.calculateDimension(stringBounder).translate(ut).getLeft();
				minX = Math.min(minX, out);
				maxX = Math.max(maxX, out);
			}
			if (atLeastOne == false) {
				return;
			}

			final Snake s = Snake.create(arrowColor).withMerge(MergeStrategy.NONE);
			final double height = totalDim.getHeight();
			s.addPoint(minX, height);
			s.addPoint(maxX, height);
			ug.draw(s);
		}
	}

	public Ftile addLinks(Branch branch1, Branch branch2, StringBounder stringBounder) {
		final List<Connection> conns = new ArrayList<Connection>();
		conns.add(new ConnectionHorizontalThenVertical(tile1, branch1));
		conns.add(new ConnectionHorizontalThenVertical(tile2, branch2));
		final boolean hasPointOut1 = tile1.calculateDimension(stringBounder).hasPointOut();
		final boolean hasPointOut2 = tile2.calculateDimension(stringBounder).hasPointOut();
		if (conditionEndStyle == ConditionEndStyle.DIAMOND) {
			if (hasPointOut1 && hasPointOut2) {
				conns.add(new ConnectionVerticalThenHorizontal(tile1, branch1.getInlinkRenderingColorAndStyle(),
						branch1.isEmpty()));
				conns.add(new ConnectionVerticalThenHorizontal(tile2, branch2.getInlinkRenderingColorAndStyle(),
						branch2.isEmpty()));
			} else if (hasPointOut1 && hasPointOut2 == false) {
				conns.add(new ConnectionVerticalThenHorizontalDirect(tile1, branch1.getInlinkRenderingColorAndStyle(),
						branch1.isEmpty()));
			} else if (hasPointOut1 == false && hasPointOut2) {
				conns.add(new ConnectionVerticalThenHorizontalDirect(tile2, branch2.getInlinkRenderingColorAndStyle(),
						branch2.isEmpty()));
			}
		} else if (conditionEndStyle == ConditionEndStyle.HLINE) {
			if (hasPointOut1 && hasPointOut2) {
				conns.add(new ConnectionVerticalOut(tile1, arrowColor, null));
				conns.add(new ConnectionVerticalOut(tile2, arrowColor, null));
				conns.add(new ConnectionHline(arrowColor));
			} else if (hasPointOut1 && hasPointOut2 == false) {
				// this is called when the "else" has a break statement
				conns.add(new ConnectionVerticalThenHorizontalDirect(tile1, branch1.getInlinkRenderingColorAndStyle(),
						branch1.isEmpty()));
			} else if (hasPointOut1 == false && hasPointOut2) {
				// this is called when the "if" has a break statement
				conns.add(new ConnectionVerticalThenHorizontalDirect(tile2, branch2.getInlinkRenderingColorAndStyle(),
						branch2.isEmpty()));
			}
		}
		return FtileUtils.addConnection(this, conns);
	}

}
