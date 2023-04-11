/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
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
package net.sourceforge.plantuml.klimt.font;

import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;

import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.text.RichText;
import net.sourceforge.plantuml.text.StyledString;

public abstract class StringBounderRaw implements StringBounder {
	// ::remove file when __HAXE__

	private final FontRenderContext frc;

	protected StringBounderRaw(FontRenderContext frc) {
		this.frc = frc;
	}

	public final XDimension2D calculateDimension(UFont font, String text) {
		if (RichText.isRich(text)) {
			double width = 0;
			double height = 0;
			for (StyledString s : StyledString.build(text)) {
				final UFont newFont = s.getStyle().mutateFont(font);
				final XDimension2D rect = calculateDimensionInternal(newFont, s.getText());
				width += rect.getWidth();
				height = Math.max(height, rect.getHeight());
			}
			return new XDimension2D(width, height);
		}
		return calculateDimensionInternal(font, text);
	}

	protected abstract XDimension2D calculateDimensionInternal(UFont font, String text);

	public double getDescent(UFont font, String text) {
		final LineMetrics lineMetrics = font.getUnderlayingFont(UFontContext.G2D).getLineMetrics(text, frc);
		final double descent = lineMetrics.getDescent();
		return descent;
	}

}
