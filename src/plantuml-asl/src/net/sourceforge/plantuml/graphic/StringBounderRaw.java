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
package net.sourceforge.plantuml.graphic;

import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.awt.geom.Dimension2D;
import net.sourceforge.plantuml.text.RichText;
import net.sourceforge.plantuml.text.StyledString;
import net.sourceforge.plantuml.ugraphic.UFont;

public abstract class StringBounderRaw implements StringBounder {

	public final Dimension2D calculateDimension(UFont font, String text) {
		if (RichText.isRich(text)) {
			double width = 0;
			double height = 0;
			for (StyledString s : StyledString.build(text)) {
				final UFont newFont = s.getStyle().mutateFont(font);
				final Dimension2D rect = calculateDimensionInternal(newFont, s.getText());
				width += rect.getWidth();
				height = Math.max(height, rect.getHeight());
			}
			return new Dimension2DDouble(width, height);
		}
		return calculateDimensionInternal(font, text);
	}

	protected abstract Dimension2D calculateDimensionInternal(UFont font, String text);

	public double getDescent(UFont font, String text) {
		final FontRenderContext frc = FileFormat.gg.getFontRenderContext();
		final LineMetrics lineMetrics = font.getUnderlayingFont().getLineMetrics(text, frc);
		final double descent = lineMetrics.getDescent();
		return descent;
	}

}
