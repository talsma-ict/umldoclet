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
package net.sourceforge.plantuml.ugraphic;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class UClip implements UChange {

	private final double x;
	private final double y;
	private final double width;
	private final double height;

	public UClip(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	@Override
	public String toString() {
		return "CLIP x=" + x + " y=" + y + " w=" + width + " h=" + height;
	}

	public UClip translate(double dx, double dy) {
		return new UClip(x + dx, y + dy, width, height);
	}

	public UClip translate(UTranslate translate) {
		return translate(translate.getDx(), translate.getDy());
	}

	public final double getX() {
		return x;
	}

	public final double getY() {
		return y;
	}

	public final double getWidth() {
		return width;
	}

	public final double getHeight() {
		return height;
	}

	public boolean isInside(Point2D pt) {
		return isInside(pt.getX(), pt.getY());
	}

	public boolean isInside(double xp, double yp) {
		if (xp < x) {
			assert getClippedX(xp) != xp;
			return false;
		}
		if (xp > x + width) {
			assert getClippedX(xp) != xp;
			return false;
		}
		if (yp < y) {
			assert getClippedY(yp) != yp;
			return false;
		}
		if (yp > y + height) {
			assert getClippedY(yp) != yp;
			return false;
		}
		assert getClippedX(xp) == xp;
		assert getClippedY(yp) == yp;
		return true;
	}

	public Rectangle2D.Double getClippedRectangle(Rectangle2D.Double r) {
		return (Rectangle2D.Double) r.createIntersection(new Rectangle2D.Double(x, y, width, height));
	}

	public Line2D.Double getClippedLine(Line2D.Double line) {
		if (isInside(line.x1, line.y1) && isInside(line.x2, line.y2)) {
			return line;
		}
		if (isInside(line.x1, line.y1) == false && isInside(line.x2, line.y2) == false) {
			if (line.x1 == line.x2) {
				final double newy1 = getClippedY(line.y1);
				final double newy2 = getClippedY(line.y2);
				if (newy1 != newy2) {
					return new Line2D.Double(line.x1, newy1, line.x2, newy2);
				}
			}
			return null;
		}
		if (line.x1 != line.x2 && line.y1 != line.y2) {
			return null;
		}
		assert line.x1 == line.x2 || line.y1 == line.y2;
		if (line.y1 == line.y2) {
			final double newx1 = getClippedX(line.x1);
			final double newx2 = getClippedX(line.x2);
			return new Line2D.Double(newx1, line.y1, newx2, line.y2);
		}
		if (line.x1 == line.x2) {
			final double newy1 = getClippedY(line.y1);
			final double newy2 = getClippedY(line.y2);
			return new Line2D.Double(line.x1, newy1, line.x2, newy2);
		}
		throw new IllegalStateException();
	}

	private double getClippedX(double xp) {
		if (xp < x) {
			return x;
		}
		if (xp > x + width) {
			return x + width;
		}
		return xp;
	}

	private double getClippedY(double yp) {
		if (yp < y) {
			return y;
		}
		if (yp > y + height) {
			return y + height;
		}
		return yp;
	}

	public boolean isInside(double x, double y, UPath shape) {
		return isInside(x + shape.getMinX(), y + shape.getMinY()) && isInside(x + shape.getMaxX(), y + shape.getMaxY());
	}

}
