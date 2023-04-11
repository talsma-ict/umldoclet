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

import net.sourceforge.plantuml.cucadiagram.BodyFactory;
import net.sourceforge.plantuml.klimt.LineBreakStrategy;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.color.HColorSet;
import net.sourceforge.plantuml.klimt.creole.CreoleMode;
import net.sourceforge.plantuml.klimt.creole.Display;
import net.sourceforge.plantuml.klimt.font.FontConfiguration;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.font.UFont;
import net.sourceforge.plantuml.klimt.geom.HorizontalAlignment;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.klimt.shape.TextBlock;
import net.sourceforge.plantuml.klimt.shape.TextBlockEmpty;
import net.sourceforge.plantuml.style.ISkinParam;
import net.sourceforge.plantuml.style.ISkinSimple;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.Style;

public abstract class AbstractTextualComponent extends AbstractComponent {

	private final int marginX1;
	private final int marginX2;
	private final int marginY;

	private final TextBlock textBlock;
	private final ISkinSimple spriteContainer;

	private final UFont font;
	private final HColor fontColor;
	private final HorizontalAlignment alignment;

	public AbstractTextualComponent(Style style, LineBreakStrategy maxMessageSize, int marginX1, int marginX2,
			int marginY, ISkinSimple spriteContainer, CharSequence label) {
		this(style, style, maxMessageSize, marginX1, marginX2, marginY, spriteContainer,
				Display.getWithNewlines(label == null ? "" : label.toString()), false);
	}

	public AbstractTextualComponent(Style style, LineBreakStrategy maxMessageSize, int marginX1, int marginX2,
			int marginY, ISkinSimple spriteContainer, Display display, boolean enhanced) {
		this(style, style, maxMessageSize, marginX1, marginX2, marginY, spriteContainer, display, enhanced);
	}

	public AbstractTextualComponent(Style style, Style stereo, LineBreakStrategy maxMessageSize, int marginX1,
			int marginX2, int marginY, ISkinSimple spriteContainer, Display display, boolean enhanced) {
		super(style);
		this.spriteContainer = spriteContainer;

		final FontConfiguration fc = style.getFontConfiguration(getIHtmlColorSet());
		this.font = style.getUFont();
		this.fontColor = style.value(PName.FontColor).asColor(getIHtmlColorSet());
		final HorizontalAlignment horizontalAlignment = style.getHorizontalAlignment();
		final UFont fontForStereotype = stereo.getUFont();
		final HColor htmlColorForStereotype = stereo.value(PName.FontColor).asColor(getIHtmlColorSet());
		display = display.withoutStereotypeIfNeeded(style);

		this.marginX1 = marginX1;
		this.marginX2 = marginX2;
		this.marginY = marginY;

		if (display.size() == 1 && display.get(0).length() == 0)
			textBlock = new TextBlockEmpty();
		else if (enhanced)
			textBlock = BodyFactory.create3(display, spriteContainer, horizontalAlignment, fc, maxMessageSize, style);
		else
			textBlock = display.create0(fc, horizontalAlignment, spriteContainer, maxMessageSize, CreoleMode.FULL,
					fontForStereotype, htmlColorForStereotype, marginX1, marginX2);

		this.alignment = horizontalAlignment;
	}

	protected HColorSet getIHtmlColorSet() {
		return ((ISkinParam) spriteContainer).getIHtmlColorSet();
	}

	protected TextBlock getTextBlock() {
		return textBlock;
	}

	protected double getPureTextWidth(StringBounder stringBounder) {
		final TextBlock textBlock = getTextBlock();
		final XDimension2D size = textBlock.calculateDimension(stringBounder);
		return size.getWidth();
	}

	final public double getTextWidth(StringBounder stringBounder) {
		return getPureTextWidth(stringBounder) + marginX1 + marginX2;
	}

	final protected double getTextHeight(StringBounder stringBounder) {
		final TextBlock textBlock = getTextBlock();
		final XDimension2D size = textBlock.calculateDimension(stringBounder);
		return size.getHeight() + 2 * marginY;
	}

	final protected int getMarginX1() {
		return marginX1;
	}

	final protected int getMarginX2() {
		return marginX2;
	}

	final protected int getMarginY() {
		return marginY;
	}

	final protected UFont getFont() {
		return font;
	}

	protected HColor getFontColor() {
		return fontColor;
	}

	protected final ISkinSimple getISkinSimple() {
		return spriteContainer;
	}

	public final HorizontalAlignment getHorizontalAlignment() {
		return alignment;
	}

}
