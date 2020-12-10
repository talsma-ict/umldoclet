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
package net.sourceforge.plantuml.jsondiagram;

import java.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.creole.CreoleMode;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.json.JsonArray;
import net.sourceforge.plantuml.json.JsonObject;
import net.sourceforge.plantuml.json.JsonObject.Member;
import net.sourceforge.plantuml.json.JsonValue;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignature;
import net.sourceforge.plantuml.svek.TextBlockBackcolored;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;

//See TextBlockMap
public class TextBlockJson extends AbstractTextBlock implements TextBlockBackcolored {

	private final List<Line> lines = new ArrayList<Line>();

	private final ISkinParam skinParam;
	private double totalWidth;
	private final JsonValue root;

	static class Line {
		final TextBlock b1;
		final TextBlock b2;
		final boolean highlighted;

		Line(TextBlock b1, TextBlock b2, boolean highlighted) {
			this.b1 = b1;
			this.b2 = b2;
			this.highlighted = highlighted;
		}

		Line(TextBlock b1, boolean highlighted) {
			this(b1, null, highlighted);
		}

		double getHeightOfRow(StringBounder stringBounder) {
			final double height = b1.calculateDimension(stringBounder).getHeight();
			if (b2 == null) {
				return height;
			}
			return Math.max(height, b2.calculateDimension(stringBounder).getHeight());
		}

	}

	public TextBlockJson(ISkinParam skinParam, JsonValue root, List<String> highlighted) {
		this.skinParam = skinParam;
		this.root = root;
		if (root instanceof JsonObject)
			for (Member member : (JsonObject) root) {
				final String key = member.getName();
				final String value = getShortString(member.getValue());

				final TextBlock block1 = getTextBlock(key);
				final TextBlock block2 = getTextBlock(value);
				this.lines.add(new Line(block1, block2, isHighlighted(key, highlighted)));
			}
		if (root instanceof JsonArray) {
			int i = 0;
			for (JsonValue value : (JsonArray) root) {
				final TextBlock block2 = getTextBlock(getShortString(value));
				this.lines.add(new Line(block2, isHighlighted("" + i, highlighted)));
				i++;
			}
		}
	}

	private boolean isHighlighted(String key, List<String> highlighted) {
		for (String tmp : highlighted) {
			if (tmp.trim().equals("\"" + key + "\"")) {
				return true;
			}
		}
		return false;
	}

	public int size() {
		int size = 0;
		if (root instanceof JsonObject) {
			for (Member member : (JsonObject) root)
				size++;
		}
		if (root instanceof JsonArray) {
			for (JsonValue value : (JsonArray) root)
				size++;
		}
		return size;

	}

	private String getShortString(JsonValue value) {
		if (value.isString()) {
			return value.asString();
		}
		if (value.isNumber() || value.isBoolean()) {
			return value.toString();
		}
		return "   ";
	}

	public List<JsonValue> children() {
		final List<JsonValue> result = new ArrayList<JsonValue>();
		if (root instanceof JsonObject) {
			for (Member member : (JsonObject) root) {
				final JsonValue value = member.getValue();
				if (value instanceof JsonObject || value instanceof JsonArray) {
					result.add(value);
				} else {
					result.add(null);
				}
			}
		}
		if (root instanceof JsonArray) {
			for (JsonValue value : (JsonArray) root) {
				if (value instanceof JsonObject || value instanceof JsonArray) {
					result.add(value);
				} else {
					result.add(null);
				}
			}
		}
		return Collections.unmodifiableList(result);
	}

	public List<String> keys() {
		final List<String> result = new ArrayList<String>();
		if (root instanceof JsonObject) {
			for (Member member : (JsonObject) root) {
				final String key = member.getName();
				result.add(key);
			}
		}
		if (root instanceof JsonArray) {
			int i = 0;
			for (JsonValue value : (JsonArray) root) {
				result.add("" + i);
				i++;
			}
		}
		return Collections.unmodifiableList(result);
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		return new Dimension2DDouble(getWidthColA(stringBounder) + getWidthColB(stringBounder),
				getTotalHeight(stringBounder));
	}

	public double getWidthColA(StringBounder stringBounder) {
		double width = 0;
		for (Line line : lines) {
			width = Math.max(width, line.b1.calculateDimension(stringBounder).getWidth());
		}
		return width;
	}

	public double getWidthColB(StringBounder stringBounder) {
		double width = 0;
		for (Line line : lines) {
			if (line.b2 != null) {
				width = Math.max(width, line.b2.calculateDimension(stringBounder).getWidth());
			}
		}
		return width;
	}

	public void drawU(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();

		final Dimension2D fullDim = calculateDimension(stringBounder);
		double trueWidth = Math.max(fullDim.getWidth(), totalWidth);
		final double widthColA = getWidthColA(stringBounder);

		double y = 0;
		ug = getStyle().applyStrokeAndLineColor(ug, skinParam.getIHtmlColorSet());
		for (Line line : lines) {
			final UGraphic ugline = ug.apply(UTranslate.dy(y));
			final double heightOfRow = line.getHeightOfRow(stringBounder);
			if (line.highlighted) {
				final URectangle back = new URectangle(trueWidth - 2, heightOfRow).rounded(4);
				final HColor yellow = skinParam.getIHtmlColorSet().getColorIfValid("#ccff02");
				ugline.apply(yellow).apply(yellow.bg()).apply(new UTranslate(1.5, 0)).draw(back);
			}

			if (y > 0)
				ugline.draw(ULine.hline(trueWidth));

			final double posColA = (widthColA - line.b1.calculateDimension(stringBounder).getWidth()) / 2;
			line.b1.drawU(ugline.apply(UTranslate.dx(posColA)));

			if (line.b2 != null) {
				line.b2.drawU(ugline.apply(UTranslate.dx(widthColA)));
				ugline.apply(UTranslate.dx(widthColA)).draw(ULine.vline(heightOfRow));
			}

			y += heightOfRow;
		}

		if (y == 0)
			y = 15;
		if (trueWidth == 0)
			trueWidth = 30;

		final URectangle full = new URectangle(trueWidth, y).rounded(10);
		ug.apply(new UStroke(1.5)).draw(full);
	}

	private double getTotalHeight(StringBounder stringBounder) {
		double height = 0;
		for (Line line : lines) {
			height += line.getHeightOfRow(stringBounder);
		}
		return height;
	}

	private TextBlock getTextBlock(String key) {
		final Display display = Display.getWithNewlines(key);
		final FontConfiguration fontConfiguration = getStyle().getFontConfiguration(skinParam.getIHtmlColorSet());
		TextBlock result = display.create7(fontConfiguration, HorizontalAlignment.LEFT, skinParam,
				CreoleMode.NO_CREOLE);
		result = TextBlockUtils.withMargin(result, 5, 2);
		return result;
	}

	private Style getStyle() {
		return StyleSignature.of(SName.root, SName.element, SName.jsonDiagram)
				.getMergedStyle(skinParam.getCurrentStyleBuilder());
	}

	public void setTotalWidth(double totalWidth) {
		this.totalWidth = totalWidth;
	}

	public HColor getBackcolor() {
		return null;
	}

}
