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
package net.sourceforge.plantuml.skin;

import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.color.HColors;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.klimt.shape.AbstractTextBlock;
import net.sourceforge.plantuml.klimt.shape.TextBlock;
import net.sourceforge.plantuml.klimt.shape.URectangle;

public class Padder {

	private final double margin;
	private final double padding;
	private final HColor backgroundColor;
	private final HColor borderColor;
	private final double roundCorner;

	public static final Padder NONE = new Padder(0, 0, null, null, 0);

	@Override
	public String toString() {
		return "" + margin + "/" + padding + "/" + borderColor + "/" + backgroundColor;
	}

	private Padder(double margin, double padding, HColor backgroundColor, HColor borderColor, double roundCorner) {
		this.margin = margin;
		this.padding = padding;
		this.borderColor = borderColor;
		this.backgroundColor = backgroundColor;
		this.roundCorner = roundCorner;
	}

	public Padder withMargin(double margin) {
		return new Padder(margin, padding, backgroundColor, borderColor, roundCorner);
	}

	public Padder withPadding(double padding) {
		return new Padder(margin, padding, backgroundColor, borderColor, roundCorner);
	}

	public Padder withBackgroundColor(HColor backgroundColor) {
		return new Padder(margin, padding, backgroundColor, borderColor, roundCorner);
	}

	public Padder withBorderColor(HColor borderColor) {
		return new Padder(margin, padding, backgroundColor, borderColor, roundCorner);
	}

	public Padder withRoundCorner(double roundCorner) {
		return new Padder(margin, padding, backgroundColor, borderColor, roundCorner);
	}

	public final double getMargin() {
		return margin;
	}

	public final double getPadding() {
		return padding;
	}

	public final HColor getBackgroundColor() {
		return backgroundColor;
	}

	public final HColor getBorderColor() {
		return borderColor;
	}

	public TextBlock apply(final TextBlock orig) {
		if (this == NONE) {
			return orig;
		}
		return new AbstractTextBlock() {
			public XDimension2D calculateDimension(StringBounder stringBounder) {
				return orig.calculateDimension(stringBounder).delta(2 * (margin + padding));
			}

			public void drawU(UGraphic ug) {
				ug = ug.apply(new UTranslate(margin, margin));
				UGraphic ug2 = ug;
				if (borderColor == null) {
					ug2 = ug2.apply(HColors.none());
				} else {
					ug2 = ug2.apply(borderColor);
				}
				if (backgroundColor == null) {
					ug2 = ug2.apply(HColors.none().bg());
				} else {
					ug2 = ug2.apply(backgroundColor.bg());
				}
				final XDimension2D originalDim = orig.calculateDimension(ug.getStringBounder());
				final URectangle rect = URectangle.build(originalDim.delta(2 * padding)).rounded(roundCorner);
				ug2.draw(rect);
				orig.drawU(ug.apply(new UTranslate(padding, padding)));
			}
		};
	}
}
