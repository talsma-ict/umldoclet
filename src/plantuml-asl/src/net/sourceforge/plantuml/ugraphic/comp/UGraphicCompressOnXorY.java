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
package net.sourceforge.plantuml.ugraphic.comp;

import net.sourceforge.plantuml.graphic.UGraphicDelegator;
import net.sourceforge.plantuml.ugraphic.UChange;
import net.sourceforge.plantuml.ugraphic.UChangeBackColor;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UShape;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class UGraphicCompressOnXorY extends UGraphicDelegator {

	public UGraphic apply(UChange change) {
		if (change instanceof UTranslate) {
			return new UGraphicCompressOnXorY(mode, getUg(), compressionTransform, translate.compose((UTranslate) change));
		} else if (change instanceof UStroke || change instanceof UChangeBackColor || change instanceof UChangeColor) {
			return new UGraphicCompressOnXorY(mode, getUg().apply(change), compressionTransform, translate);
		}
		throw new UnsupportedOperationException();
	}

	private final CompressionMode mode;
	private final CompressionTransform compressionTransform;
	private final UTranslate translate;

	public UGraphicCompressOnXorY(CompressionMode mode, UGraphic ug, CompressionTransform compressionTransform) {
		this(mode, ug, compressionTransform, new UTranslate());
	}

	private UGraphicCompressOnXorY(CompressionMode mode, UGraphic ug, CompressionTransform compressionTransform,
			UTranslate translate) {
		super(ug);
		this.mode = mode;
		this.compressionTransform = compressionTransform;
		this.translate = translate;
	}

	public void draw(UShape shape) {
		final double x = translate.getDx();
		final double y = translate.getDy();
		if (shape instanceof URectangle) {
			final URectangle rect = (URectangle) shape;
			if (rect.isIgnoreForCompression()) {
				if (mode == CompressionMode.ON_X) {
					final double x2 = ct(x + rect.getWidth());
					shape = rect.withWidth(x2 - ct(x));
				} else {
					final double y2 = ct(y + rect.getHeight());
					shape = rect.withHeight(y2 - ct(y));
				}
			}
		}
		if (shape instanceof ULine) {
			drawLine(x, y, (ULine) shape);
		} else {
			if (mode == CompressionMode.ON_X) {
				getUg().apply(new UTranslate(ct(x), y)).draw(shape);
			} else {
				getUg().apply(new UTranslate(x, ct(y))).draw(shape);
			}
		}
	}

	private void drawLine(double x, double y, ULine shape) {
		if (mode == CompressionMode.ON_X) {
			drawLine(ct(x), y, ct(x + shape.getDX()), y + shape.getDY());
		} else {
			drawLine(x, ct(y), x + shape.getDX(), ct(y + shape.getDY()));
		}
	}

	private double ct(double v) {
		return compressionTransform.transform(v);
	}

	private void drawLine(double x1, double y1, double x2, double y2) {
		if (y1 > y2) {
			drawLine(x2, y2, x1, y1);
			return;
		}
		assert y1 <= y2;
		getUg().apply(new UTranslate(x1, y1)).draw(new ULine(x2 - x1, y2 - y1));
	}

}
