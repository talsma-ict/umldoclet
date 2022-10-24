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
package net.sourceforge.plantuml.cucadiagram;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.LineBreakStrategy;
import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.creole.CreoleMode;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.json.JsonArray;
import net.sourceforge.plantuml.json.JsonObject;
import net.sourceforge.plantuml.json.JsonValue;
import net.sourceforge.plantuml.svek.Ports;
import net.sourceforge.plantuml.svek.WithPorts;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class TextBlockCucaJSon extends AbstractTextBlock implements WithPorts {

	private final ISkinParam skinParam;
	private final FontConfiguration fontConfiguration;
	private final JsonValue json;
	private TextBlockJson jsonTextBlock;
	private double mainTotalWidth;
	private final LineBreakStrategy wordWrap;

	public TextBlockCucaJSon(FontConfiguration fontConfiguration, ISkinParam skinParam, JsonValue json,
			LineBreakStrategy wordWrap) {
		this.skinParam = skinParam;
		this.json = json;
		this.fontConfiguration = fontConfiguration;
		this.wordWrap = wordWrap;
	}

	private TextBlockJson getJsonTextBlock() {
		if (jsonTextBlock == null)
			this.jsonTextBlock = new TextBlockJson(json, 0);

		jsonTextBlock.jsonTotalWidth = mainTotalWidth;
		return jsonTextBlock;
	}

	@Override
	public Ports getPorts(StringBounder stringBounder) {
		return new Ports();
	}

	public XDimension2D calculateDimension(StringBounder stringBounder) {
		return getJsonTextBlock().calculateDimension(stringBounder);
	}

	public void drawU(UGraphic ug) {
		getJsonTextBlock().drawU(ug);
	}

	class TextBlockJson extends AbstractTextBlock {

		private final JsonObject obj;
		private double jsonTotalWidth;

		public TextBlockJson(JsonValue json, double totalWidth) {
			this.obj = json.asObject();
			this.jsonTotalWidth = totalWidth;
		}

		@Override
		public XDimension2D calculateDimension(StringBounder stringBounder) {
			return new XDimension2D(getWidth1(stringBounder) + getWidth2(stringBounder), getHeight(stringBounder));
		}

		private double getWidth1(StringBounder stringBounder) {
			double result = 0;
			for (JsonObject.Member s : obj) {
				final TextBlock tb1 = getTextBlock(s.getName());
				result = Math.max(result, tb1.calculateDimension(stringBounder).getWidth());
			}
			return result;
		}

		private double getWidth2(StringBounder stringBounder) {
			double result = 0;
			for (JsonObject.Member s : obj) {
				final TextBlock tb2 = getTextBlockValue(s.getValue(), jsonTotalWidth);
				result = Math.max(result, tb2.calculateDimension(stringBounder).getWidth());
			}
			return result;
		}

		private double getHeight(StringBounder stringBounder) {
			double result = 0;
			for (JsonObject.Member s : obj) {
				final TextBlock tb1 = getTextBlock(s.getName());
				final TextBlock tb2 = getTextBlockValue(s.getValue(), jsonTotalWidth);
				final XDimension2D dim1 = tb1.calculateDimension(stringBounder);
				final XDimension2D dim2 = tb2.calculateDimension(stringBounder);
				result += Math.max(dim1.getHeight(), dim2.getHeight());
			}
			return result;
		}

		@Override
		public void drawU(UGraphic ug) {
			final StringBounder stringBounder = ug.getStringBounder();
			final double width1 = getWidth1(stringBounder);
			final double height = getHeight(stringBounder);
			ug.apply(UTranslate.dx(width1)).draw(ULine.vline(height));
			final ULine hline = ULine.hline(this.jsonTotalWidth);
			for (JsonObject.Member s : obj) {
				final TextBlock tb1 = getTextBlock(s.getName());
				final TextBlock tb2 = getTextBlockValue(s.getValue(), this.jsonTotalWidth - width1);
				final XDimension2D dim1 = tb1.calculateDimension(stringBounder);
				final XDimension2D dim2 = tb2.calculateDimension(stringBounder);
				ug.draw(hline);
				tb1.drawU(ug);
				tb2.drawU(ug.apply(UTranslate.dx(width1)));
				ug = ug.apply(UTranslate.dy(Math.max(dim1.getHeight(), dim2.getHeight())));
			}

		}

	}

	private TextBlock getTextBlockValue(JsonValue value, double width2) {
		if (value.isString() || value.isNull() || value.isTrue() || value.isFalse() || value.isNumber()) {
			final String tmp = value.isString() ? value.asString() : value.toString();
			return getTextBlock(tmp);
		}
		if (value.isArray())
			return new TextBlockArray(value.asArray(), width2);
		if (value.isObject())
			return new TextBlockJson(value, width2);

		final String tmp = value.getClass().getSimpleName();
		return getTextBlock(tmp);
	}

	private TextBlock getTextBlock(String key) {
		final Display display = Display.getWithNewlines(key);
		TextBlock result = display.create0(getFontConfiguration(), HorizontalAlignment.LEFT, skinParam, wordWrap,
				CreoleMode.FULL, null, null);
		result = TextBlockUtils.withMargin(result, 5, 2);
		return result;
	}

	class TextBlockArray extends AbstractTextBlock {

		private final JsonArray array;
		private final double arrayTotalWidth;

		public TextBlockArray(JsonArray array, double totalWidth) {
			this.array = array;
			this.arrayTotalWidth = totalWidth;
		}

		@Override
		public XDimension2D calculateDimension(StringBounder stringBounder) {
			XDimension2D result = new XDimension2D(0, 0);
			for (JsonValue element : array) {
				final TextBlock tb = getTextBlockValue(element, arrayTotalWidth);
				final XDimension2D dim = tb.calculateDimension(stringBounder);
				result = XDimension2D.mergeTB(result, dim);
			}
			return result;
		}

		@Override
		public void drawU(UGraphic ug) {
			final ULine hline = ULine.hline(this.arrayTotalWidth);
			int nb = 0;
			for (JsonValue element : array) {
				final TextBlock tb = getTextBlockValue(element, arrayTotalWidth);
				if (nb > 0)
					ug.draw(hline);
				nb++;
				tb.drawU(ug);
				ug = ug.apply(UTranslate.dy(tb.calculateDimension(ug.getStringBounder()).getHeight()));
			}
		}

	}

	private FontConfiguration getFontConfiguration() {
		return fontConfiguration;
	}

	public void setTotalWidth(double totalWidth) {
		this.mainTotalWidth = totalWidth;
	}

}
