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
package net.sourceforge.plantuml.skin.rose;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.LineBreakStrategy;
import net.sourceforge.plantuml.UseStyle;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.skin.AbstractTextualComponent;
import net.sourceforge.plantuml.skin.ArrowComponent;
import net.sourceforge.plantuml.skin.ArrowConfiguration;
import net.sourceforge.plantuml.skin.Padder;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public abstract class AbstractComponentRoseArrow extends AbstractTextualComponent implements ArrowComponent {

	private final int arrowDeltaX = 10;
	private final int arrowDeltaY = 4;
	private final HColor foregroundColor;
	private final ArrowConfiguration arrowConfiguration;

	public AbstractComponentRoseArrow(Style style, HColor foregroundColor, FontConfiguration font,
			Display stringsToDisplay, ArrowConfiguration arrowConfiguration, ISkinSimple spriteContainer,
			HorizontalAlignment textHorizontalAlignment, LineBreakStrategy maxMessageSize) {
		super(style, maxMessageSize, stringsToDisplay, font, textHorizontalAlignment, 7, 7, 1, spriteContainer, false,
				null, null);
		if (UseStyle.useBetaStyle()) {
			this.foregroundColor = style.value(PName.LineColor).asColor(spriteContainer.getThemeStyle(),
					getIHtmlColorSet());
			final UStroke stroke = style.getStroke();
			this.arrowConfiguration = arrowConfiguration.withThickness(stroke.getThickness());
		} else {
			this.foregroundColor = foregroundColor;
			this.arrowConfiguration = arrowConfiguration;
		}
	}

	@Override
	final protected TextBlock getTextBlock() {
		final Padder padder = getISkinSimple() instanceof ISkinParam
				? ((ISkinParam) getISkinSimple()).sequenceDiagramPadder()
				: Padder.NONE;

		return padder.apply(super.getTextBlock());
	}

	abstract public double getYPoint(StringBounder stringBounder);

	protected final HColor getForegroundColor() {
		return foregroundColor;
	}

	final protected int getArrowDeltaX() {
		return arrowDeltaX;
	}

	final protected int getArrowDeltaY() {
		return arrowDeltaY;
	}

	@Override
	public final double getPaddingY() {
		return 4;
	}

	public final ArrowConfiguration getArrowConfiguration() {
		return arrowConfiguration;
	}

}
