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
 * Contribution:  Miguel Esteves
 */
package net.sourceforge.plantuml.ugraphic;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.ugraphic.comp.CompressionMode;

public class URectangle extends AbstractShadowable implements Scalable, UShapeSized, UShapeIgnorableForCompression {

	private final double width;
	private final double height;
	private final double rx;
	private final double ry;
	private final String comment;
	private final String codeLine;
	private final boolean ignoreForCompressionOnX;
	private final boolean ignoreForCompressionOnY;

	public URectangle withHeight(double newHeight) {
		final URectangle result = new URectangle(width, newHeight, rx, ry, comment, ignoreForCompressionOnX,
				ignoreForCompressionOnY, codeLine);
		result.setDeltaShadow(this.getDeltaShadow());
		return result;
	}

	public URectangle withWidth(double newWidth) {
		final URectangle result = new URectangle(newWidth, height, rx, ry, comment, ignoreForCompressionOnX,
				ignoreForCompressionOnY, codeLine);
		result.setDeltaShadow(this.getDeltaShadow());
		return result;
	}

	public URectangle withCommentAndCodeLine(String comment, String codeLine) {
		return new URectangle(width, height, rx, ry, comment, ignoreForCompressionOnX, ignoreForCompressionOnY,
				codeLine);
	}

	public URectangle rounded(double round) {
		return new URectangle(width, height, round, round, comment, ignoreForCompressionOnX, ignoreForCompressionOnY,
				codeLine);
	}

	public Shadowable diagonalCorner(double diagonalCorner) {
		if (ignoreForCompressionOnX || ignoreForCompressionOnY) {
			throw new IllegalStateException();
		}
		if (diagonalCorner == 0) {
			return this;
		}
		final UPath result = new UPath();
		result.moveTo(diagonalCorner, 0);
		result.lineTo(width - diagonalCorner, 0);
		result.lineTo(width, diagonalCorner);
		result.lineTo(width, height - diagonalCorner);
		result.lineTo(width - diagonalCorner, height);
		result.lineTo(diagonalCorner, height);
		result.lineTo(0, height - diagonalCorner);
		result.lineTo(0, diagonalCorner);
		result.lineTo(diagonalCorner, 0);
		return result;
	}

	public final URectangle ignoreForCompressionOnX() {
		return new URectangle(width, height, rx, ry, comment, true, ignoreForCompressionOnY, codeLine);
	}

	public final URectangle ignoreForCompressionOnY() {
		return new URectangle(width, height, rx, ry, comment, ignoreForCompressionOnX, true, codeLine);
	}

	public UShape getScaled(double scale) {
		if (scale == 1) {
			return this;
		}
		final AbstractShadowable result = new URectangle(width * scale, height * scale, rx * scale, ry * scale, comment,
				ignoreForCompressionOnX, ignoreForCompressionOnY, codeLine);
		result.setDeltaShadow(this.getDeltaShadow());
		return result;
	}

	public URectangle(double width, double height) {
		this(width, height, 0, 0, null, false, false, null);
	}

	public URectangle(Dimension2D dim) {
		this(dim.getWidth(), dim.getHeight());
	}

	private URectangle(double width, double height, double rx, double ry, String comment,
			boolean ignoreForCompressionOnX, boolean ignoreForCompressionOnY, String codeLine) {
		if (height == 0) {
			throw new IllegalArgumentException("height=" + height);
		}
		if (width == 0) {
			throw new IllegalArgumentException("width=" + width);
		}
		this.ignoreForCompressionOnX = ignoreForCompressionOnX;
		this.ignoreForCompressionOnY = ignoreForCompressionOnY;
		this.comment = comment;
		this.codeLine = codeLine;
		this.width = width;
		this.height = height;
		this.rx = rx;
		this.ry = ry;
	}

	@Override
	public String toString() {
		return "width=" + width + " height=" + height;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	public double getRx() {
		return rx;
	}

	public double getRy() {
		return ry;
	}

	public URectangle clip(UClip clip) {
		return this;
	}

	public MinMax getMinMax() {
		return MinMax.fromMax(width, height);
	}

	public final String getComment() {
		return comment;
	}

	public String getCodeLine() {
		return codeLine;
	}

	public void drawWhenCompressed(UGraphic ug, CompressionMode mode) {
		if (mode == CompressionMode.ON_X) {
			ug.draw(new UEmpty(2, getHeight()));
			ug.apply(UTranslate.dx(getWidth() - 2)).draw(new UEmpty(2, getHeight()));
//			drawEmpty(x, y, new UEmpty(2, shape.getHeight()));
//			drawEmpty(x + shape.getWidth() - 2, y, new UEmpty(2, shape.getHeight()));
		}
		if (mode == CompressionMode.ON_Y) {
			ug.draw(new UEmpty(getWidth(), 2));
			ug.apply(UTranslate.dy(getHeight() - 2)).draw(new UEmpty(getWidth(), 2));
//			drawEmpty(x, y, new UEmpty(shape.getWidth(), 2));
//			drawEmpty(x, y + shape.getHeight() - 2, new UEmpty(shape.getWidth(), 2));
		}

	}

	public boolean isIgnoreForCompressionOn(CompressionMode mode) {
		if (mode == CompressionMode.ON_X) {
			return ignoreForCompressionOnX;
		}
		if (mode == CompressionMode.ON_Y) {
			return ignoreForCompressionOnY;
		}
		throw new IllegalArgumentException();
	}

}
