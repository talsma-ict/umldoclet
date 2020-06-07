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
package net.sourceforge.plantuml.activitydiagram3.ftile;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.MinMax;
import net.sourceforge.plantuml.ugraphic.UBackground;
import net.sourceforge.plantuml.ugraphic.UChange;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UGraphicNo;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UParam;
import net.sourceforge.plantuml.ugraphic.UParamNull;
import net.sourceforge.plantuml.ugraphic.UPolygon;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UShape;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.ColorMapper;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;

public class CollisionDetector extends UGraphicNo implements UGraphic {

	public UGraphic apply(UChange change) {
		if (change instanceof UTranslate) {
			return new CollisionDetector(stringBounder, translate.compose((UTranslate) change), this.context);
		} else if (change instanceof UStroke) {
			return new CollisionDetector(this);
		} else if (change instanceof UBackground) {
			return new CollisionDetector(this);
		} else if (change instanceof HColor) {
			return new CollisionDetector(this);
		}
		throw new UnsupportedOperationException();
	}

	private final StringBounder stringBounder;
	private final UTranslate translate;
	private final Context context;

	static class Context {
		private final List<MinMax> rectangles = new ArrayList<MinMax>();
		private final List<Snake> snakes = new ArrayList<Snake>();
		private boolean manageSnakes;

		public void drawDebug(UGraphic ug) {
			for (MinMax minmax : rectangles) {
				if (collision(minmax)) {
					minmax.drawGrey(ug);
				}
			}
			final HColor color = HColorUtils.BLACK;
			ug = ug.apply(color).apply(new UStroke(5));
			for (Snake snake : snakes) {
				for (Line2D line : snake.getHorizontalLines()) {
					if (collision(line)) {
						drawLine(ug, line);
					}
				}
			}
		}

		private void drawLine(UGraphic ug, Line2D line) {
			ug = ug.apply(new UTranslate(line.getX1(), line.getY1()));
			ug.draw(new ULine(line.getX2() - line.getX1(), line.getY2() - line.getY1()));
		}

		private boolean collision(Line2D hline) {
			for (MinMax r : rectangles) {
				if (collisionCheck(r, hline)) {
					return true;
				}
			}
			return false;
		}

		private boolean collision(MinMax r) {
			for (Snake snake : snakes) {
				for (Line2D hline : snake.getHorizontalLines()) {
					if (collisionCheck(r, hline)) {
						return true;
					}
				}
			}
			return false;
		}

	}

	private static boolean collisionCheck(MinMax rect, Line2D hline) {
		if (hline.getY1() != hline.getY2()) {
			throw new IllegalArgumentException();
		}
		if (hline.getY1() < rect.getMinY()) {
			return false;
		}
		if (hline.getY1() > rect.getMaxY()) {
			return false;
		}
		final double x1 = Math.min(hline.getX1(), hline.getX2());
		final double x2 = Math.max(hline.getX1(), hline.getX2());
		if (x2 < rect.getMinX()) {
			return false;
		}
		if (x1 > rect.getMaxX()) {
			return false;
		}
		return true;
	}

	public CollisionDetector(StringBounder stringBounder) {
		this(stringBounder, new UTranslate(), new Context());
	}

	private CollisionDetector(StringBounder stringBounder, UTranslate translate, Context context) {
		this.stringBounder = stringBounder;
		this.translate = translate;
		this.context = context;
	}

	private CollisionDetector(CollisionDetector other) {
		this(other.stringBounder, other.translate, other.context);
	}

	public StringBounder getStringBounder() {
		return stringBounder;
	}

	public UParam getParam() {
		return new UParamNull();
	}

	public void draw(UShape shape) {
		if (shape instanceof UPolygon) {
			drawPolygone((UPolygon) shape);
		} else if (shape instanceof URectangle) {
			drawRectangle((URectangle) shape);
		} else if (shape instanceof Snake) {
			drawSnake((Snake) shape);
		} /*
		 * else { System.err.println("shape=" + shape.getClass() + " " + shape); }
		 */
	}

	private void drawSnake(Snake shape) {
		if (context.manageSnakes) {
			context.snakes.add(shape.translate(translate));
		}

	}

	private void drawRectangle(URectangle shape) {
		context.rectangles.add(shape.getMinMax().translate(translate));
	}

	private void drawPolygone(UPolygon shape) {
		context.rectangles.add(shape.getMinMax().translate(translate));
	}

	public ColorMapper getColorMapper() {
		throw new UnsupportedOperationException();
	}

	public void flushUg() {
	}

	public void drawDebug(UGraphic ug) {
		context.drawDebug(ug);
	}

	public final void setManageSnakes(boolean manageSnakes) {
		this.context.manageSnakes = manageSnakes;
	}

	public boolean matchesProperty(String propertyName) {
		return false;
	}

	public double dpiFactor() {
		return 1;
	}

}
