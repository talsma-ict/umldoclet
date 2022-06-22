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
package net.sourceforge.plantuml.style;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.LineBreakStrategy;
import net.sourceforge.plantuml.api.ThemeStyle;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.SymbolContext;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorNone;
import net.sourceforge.plantuml.ugraphic.color.HColorSet;

public class Style {

	private final Map<PName, Value> map;
	private final StyleSignatureBasic signature;

	public Style(StyleSignatureBasic signature, Map<PName, Value> map) {
		this.map = map;
		this.signature = signature;
	}

	public Style deltaPriority(int delta) {
		if (signature.isStarred() == false)
			throw new UnsupportedOperationException();

		final EnumMap<PName, Value> copy = new EnumMap<PName, Value>(PName.class);
		for (Entry<PName, Value> ent : this.map.entrySet())
			copy.put(ent.getKey(), ((ValueImpl) ent.getValue()).addPriority(delta));

		return new Style(this.signature, copy);

	}

	public void printMe() {
		if (map.size() == 0)
			return;

		System.err.println(signature + " {");
		for (Entry<PName, Value> ent : map.entrySet())
			System.err.println("  " + ent.getKey() + ": " + ent.getValue().asString());

		System.err.println("}");

	}

	@Override
	public String toString() {
		return signature + " " + map;
	}

	public Value value(PName name) {
		final Value result = map.get(name);
		if (result == null)
			return ValueNull.NULL;

		return result;
	}

	public boolean hasValue(PName name) {
		return map.containsKey(name);
	}

	public Style mergeWith(Style other, MergeStrategy strategy) {
		if (other == null)
			return this;

		final EnumMap<PName, Value> both = new EnumMap<PName, Value>(this.map);
		for (Entry<PName, Value> ent : other.map.entrySet()) {
			final Value previous = this.map.get(ent.getKey());
			if (previous != null && previous.getPriority() > StyleLoader.DELTA_PRIORITY_FOR_STEREOTYPE
					&& strategy == MergeStrategy.KEEP_EXISTING_VALUE_OF_STEREOTYPE)
				continue;
			final PName key = ent.getKey();
			both.put(key, ((ValueImpl) ent.getValue()).mergeWith(previous));
		}
		return new Style(this.signature.mergeWith(other.getSignature()), both);
	}

	public Style eventuallyOverride(PName param, HColor color) {
		if (color == null)
			return this;

		final EnumMap<PName, Value> result = new EnumMap<PName, Value>(this.map);
		final Value old = result.get(param);
		result.put(param, new ValueColor(color, old.getPriority()));
		return new Style(this.signature, result);
	}

	public Style eventuallyOverride(PName param, double value) {
		return eventuallyOverride(param, "" + value);
	}

	public Style eventuallyOverride(PName param, String value) {
		final EnumMap<PName, Value> result = new EnumMap<PName, Value>(this.map);
		result.put(param, ValueImpl.regular(value, Integer.MAX_VALUE));
		return new Style(this.signature, result);
	}

	public Style eventuallyOverride(Colors colors) {
		Style result = this;
		if (colors != null) {
			final HColor back = colors.getColor(ColorType.BACK);
			if (back != null)
				result = result.eventuallyOverride(PName.BackGroundColor, back);

			final HColor line = colors.getColor(ColorType.LINE);
			if (line != null)
				result = result.eventuallyOverride(PName.LineColor, line);

			final HColor text = colors.getColor(ColorType.TEXT);
			if (text != null)
				result = result.eventuallyOverride(PName.FontColor, text);

		}
		return result;
	}

	public Style eventuallyOverride(SymbolContext symbolContext) {
		Style result = this;
		if (symbolContext != null) {
			final HColor back = symbolContext.getBackColor();
			if (back != null)
				result = result.eventuallyOverride(PName.BackGroundColor, back);

		}
		return result;
	}

	public StyleSignatureBasic getSignature() {
		return signature;
	}

	public UFont getUFont() {
		final String family = value(PName.FontName).asString();
		final int fontStyle = value(PName.FontStyle).asFontStyle();
		final int size = value(PName.FontSize).asInt();
		return new UFont(family, fontStyle, size);
	}

	public FontConfiguration getFontConfiguration(ThemeStyle themeStyle, HColorSet set) {
		return getFontConfiguration(themeStyle, set, null);
	}

	public FontConfiguration getFontConfiguration(ThemeStyle themeStyle, HColorSet set, Colors colors) {
		final UFont font = getUFont();
		HColor color = colors == null ? null : colors.getColor(ColorType.TEXT);
		if (color == null)
			color = value(PName.FontColor).asColor(themeStyle, set);

		final HColor hyperlinkColor = value(PName.HyperLinkColor).asColor(themeStyle, set);
		return FontConfiguration.create(font, color, hyperlinkColor, true);
	}

	public SymbolContext getSymbolContext(ThemeStyle themeStyle, HColorSet set) {
		final HColor backColor = value(PName.BackGroundColor).asColor(themeStyle, set);
		final HColor foreColor = value(PName.LineColor).asColor(themeStyle, set);
		final double deltaShadowing = value(PName.Shadowing).asDouble();
		return new SymbolContext(backColor, foreColor).withStroke(getStroke()).withDeltaShadow(deltaShadowing);
	}

	public Style eventuallyOverride(UStroke stroke) {
		if (stroke == null)
			return this;

		Style result = this.eventuallyOverride(PName.LineThickness, stroke.getThickness());
		final double space = stroke.getDashSpace();
		final double visible = stroke.getDashVisible();
		result = result.eventuallyOverride(PName.LineStyle, "" + visible + ";" + space);
		return result;
	}

	public UStroke getStroke() {
		final double thickness = value(PName.LineThickness).asDouble();
		final String dash = value(PName.LineStyle).asString();
		if (dash.length() == 0)
			return new UStroke(thickness);

		try {
			final StringTokenizer st = new StringTokenizer(dash, "-;,");
			final double dashVisible = Double.parseDouble(st.nextToken().trim());
			double dashSpace = dashVisible;
			if (st.hasMoreTokens())
				dashSpace = Double.parseDouble(st.nextToken().trim());

			return new UStroke(dashVisible, dashSpace, thickness);
		} catch (Exception e) {
			return new UStroke(thickness);
		}
	}

	public UStroke getStroke(Colors colors) {
		final UStroke stroke = colors.getSpecificLineStroke();
		if (stroke == null)
			return getStroke();

		return stroke;
	}

	public LineBreakStrategy wrapWidth() {
		final String value = value(PName.MaximumWidth).asString();
		return new LineBreakStrategy(value);
	}

	public ClockwiseTopRightBottomLeft getPadding() {
		final String padding = value(PName.Padding).asString();
		return ClockwiseTopRightBottomLeft.read(padding);
	}

	public ClockwiseTopRightBottomLeft getMargin() {
		final String margin = value(PName.Margin).asString();
		return ClockwiseTopRightBottomLeft.read(margin);
	}

	public HorizontalAlignment getHorizontalAlignment() {
		return value(PName.HorizontalAlignment).asHorizontalAlignment();
	}

	private TextBlock createTextBlockInternal(Display display, HColorSet set, ISkinSimple spriteContainer,
			HorizontalAlignment alignment) {
		final FontConfiguration fc = getFontConfiguration(spriteContainer.getThemeStyle(), set);
		return display.create(fc, alignment, spriteContainer);
	}

	public TextBlock createTextBlockBordered(Display note, HColorSet set, ISkinSimple spriteContainer) {
		final HorizontalAlignment alignment = this.getHorizontalAlignment();
		final TextBlock textBlock = this.createTextBlockInternal(note, set, spriteContainer, alignment);

		final HColor backgroundColor = this.value(PName.BackGroundColor).asColor(spriteContainer.getThemeStyle(), set);
		final HColor lineColor = this.value(PName.LineColor).asColor(spriteContainer.getThemeStyle(), set);
		final UStroke stroke = this.getStroke();
		final int cornersize = this.value(PName.RoundCorner).asInt();
		final ClockwiseTopRightBottomLeft margin = this.getMargin();
		final ClockwiseTopRightBottomLeft padding = this.getPadding();
		final TextBlock result = TextBlockUtils.bordered(textBlock, stroke, lineColor, backgroundColor, cornersize,
				padding);
		return TextBlockUtils.withMargin(result, margin);
	}

	public UGraphic applyStrokeAndLineColor(UGraphic ug, HColorSet colorSet, ThemeStyle themeStyle) {
		final HColor color = value(PName.LineColor).asColor(themeStyle, colorSet);
		if (color == null)
			ug = ug.apply(new HColorNone());
		else
			ug = ug.apply(color);

		ug = ug.apply(getStroke());
		return ug;
	}

}
