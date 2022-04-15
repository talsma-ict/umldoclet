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
package net.sourceforge.plantuml.ugraphic.txt;

import static java.nio.charset.StandardCharsets.UTF_8;

import net.sourceforge.plantuml.awt.geom.Dimension2D;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.asciiart.TextStringBounder;
import net.sourceforge.plantuml.asciiart.TranslatedCharArea;
import net.sourceforge.plantuml.asciiart.UmlCharArea;
import net.sourceforge.plantuml.asciiart.UmlCharAreaImpl;
import net.sourceforge.plantuml.graphic.FontStyle;
import net.sourceforge.plantuml.security.SecurityUtils;
import net.sourceforge.plantuml.ugraphic.AbstractCommonUGraphic;
import net.sourceforge.plantuml.ugraphic.ClipContainer;
import net.sourceforge.plantuml.ugraphic.UImage;
import net.sourceforge.plantuml.ugraphic.UShape;
import net.sourceforge.plantuml.ugraphic.UText;
import net.sourceforge.plantuml.ugraphic.color.ColorMapperIdentity;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;

public class UGraphicTxt extends AbstractCommonUGraphic implements ClipContainer {

	private final UmlCharArea charArea;

	@Override
	protected AbstractCommonUGraphic copyUGraphic() {
		return new UGraphicTxt(this);
	}

	private UGraphicTxt(UGraphicTxt other) {
		super(other);
		this.charArea = other.charArea;
	}

	public UGraphicTxt() {
		super(HColorUtils.BLACK, new ColorMapperIdentity(), new TextStringBounder());
		this.charArea = new UmlCharAreaImpl();
	}

	public void draw(UShape shape) {
		// final UClip clip = getClip();
		if (shape instanceof UText) {
			final UText txt = (UText) shape;
			final int y = ((int) (getTranslateY() + txt.getDescent(getStringBounder()))) / 10;
			if (txt.getFontConfiguration().containsStyle(FontStyle.WAVE)) {
				charArea.drawHLine('^', y, getDx(), txt.getText().length());
				charArea.drawStringLR(txt.getText(), getDx(), y + 1);
			} else {
				charArea.drawStringLR(txt.getText(), getDx(), y);
			}
			return;
		} else if (shape instanceof UImage) {
			return;
		}
		return;
		// throw new UnsupportedOperationException("cl=" + shape.getClass());
	}

	public final UmlCharArea getCharArea() {
		return new TranslatedCharArea(charArea, getDx(), getDy());
	}

	private int getDy() {
		return (int) getTranslateY();
	}

	private int getDx() {
		return (int) getTranslateX();
	}

	public Dimension2D getDimension() {
		return new Dimension2DDouble(0, 0);
	}

	@Override
	public void writeToStream(OutputStream os, String metadata, int dpi) throws IOException {
		final PrintStream ps = SecurityUtils.createPrintStream(os, true, UTF_8);
		getCharArea().print(ps);
	}

}
