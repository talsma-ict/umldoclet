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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import net.sourceforge.plantuml.skin.VisibilityModifier;
import net.sourceforge.plantuml.svek.Ports;
import net.sourceforge.plantuml.svek.WithPorts;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UShape;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class TextBlockMap extends AbstractTextBlock implements WithPorts {

	private final ISkinParam skinParam;
	private final FontConfiguration fontConfiguration;
	private final Map<TextBlock, TextBlock> blocksMap = new LinkedHashMap<>();
	private final List<String> keys = new ArrayList<>();
	private double totalWidth;

	public TextBlockMap(FontConfiguration fontConfiguration, ISkinParam skinParam, Map<String, String> map,
			LineBreakStrategy wordWrap) {
		this.skinParam = skinParam;
		this.fontConfiguration = fontConfiguration;
		for (Map.Entry<String, String> ent : map.entrySet()) {
			String key = ent.getKey();
			if (VisibilityModifier.isVisibilityCharacter(key))
				key = key.substring(1);
			this.keys.add(key);
			final String value = ent.getValue();
			final TextBlock block1 = getTextBlock(key, wordWrap);
			final TextBlock block2 = getTextBlock(value, wordWrap);
			this.blocksMap.put(block1, block2);
		}
	}

	@Override
	public Ports getPorts(StringBounder stringBounder) {
		final Ports ports = new Ports();
		int i = 0;
		double y = 0;
		for (Map.Entry<TextBlock, TextBlock> ent : blocksMap.entrySet()) {
			final TextBlock key = ent.getKey();
			final TextBlock value = ent.getValue();
			final double height = getHeightOfRow(stringBounder, key, value);
			ports.add(keys.get(i), 100, y, height);
			y += height;
			i++;
		}
		return ports;
	}

	public XDimension2D calculateDimension(StringBounder stringBounder) {
		return new XDimension2D(getWidthColA(stringBounder) + getWidthColB(stringBounder),
				getTotalHeight(stringBounder));
	}

	private double getWidthColA(StringBounder stringBounder) {
		return getMaxWidth(stringBounder, blocksMap.keySet());
	}

	private double getWidthColB(StringBounder stringBounder) {
		return getMaxWidth(stringBounder, blocksMap.values());
	}

	private double getMaxWidth(StringBounder stringBounder, Collection<TextBlock> blocks) {
		double width = 0;
		for (TextBlock block : blocks)
			width = Math.max(width, block.calculateDimension(stringBounder).getWidth());

		return width;
	}

	public void drawU(final UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		final XDimension2D fullDim = calculateDimension(stringBounder);
		final double trueWidth = Math.max(fullDim.getWidth(), totalWidth);
		final double widthColA = getWidthColA(stringBounder);
		final double widthColB = getWidthColB(stringBounder);
		double y = 0;
		for (Map.Entry<TextBlock, TextBlock> ent : blocksMap.entrySet()) {
			final TextBlock key = ent.getKey();
			final TextBlock value = ent.getValue();
			final UGraphic ugline = ug.apply(UTranslate.dy(y));
			ugline.draw(ULine.hline(trueWidth));
			final double heightOfRow = getHeightOfRow(stringBounder, key, value);
			if (value instanceof Point) {
				final double posColA = (trueWidth - key.calculateDimension(stringBounder).getWidth()) / 2;
				key.drawU(ugline.apply(UTranslate.dx(posColA)));
			} else {
				final double posColA = (widthColA - key.calculateDimension(stringBounder).getWidth()) / 2;
				key.drawU(ugline.apply(UTranslate.dx(posColA)));
				value.drawU(ugline.apply(UTranslate.dx(widthColA)));
				ugline.apply(UTranslate.dx(widthColA)).draw(ULine.vline(heightOfRow));
			}
			y += heightOfRow;
		}
	}

	private double getTotalHeight(StringBounder stringBounder) {
		double height = 0;
		for (Map.Entry<TextBlock, TextBlock> ent : blocksMap.entrySet()) {
			final TextBlock key = ent.getKey();
			final TextBlock value = ent.getValue();
			height += getHeightOfRow(stringBounder, key, value);
		}
		return height;
	}

	private double getHeightOfRow(StringBounder stringBounder, TextBlock key, TextBlock value) {
		return Math.max(key.calculateDimension(stringBounder).getHeight(),
				value.calculateDimension(stringBounder).getHeight());
	}

	private TextBlock getTextBlock(String key, LineBreakStrategy wordWrap) {
		if (key.equals("\0"))
			return new Point(fontConfiguration.getColor());

		final Display display = Display.getWithNewlines(key);
		TextBlock result = display.create0(fontConfiguration, HorizontalAlignment.LEFT, skinParam, wordWrap,
				CreoleMode.FULL, null, null);
		result = TextBlockUtils.withMargin(result, 5, 2);
		return result;
	}

	static class Point extends AbstractTextBlock {

		private final HColor color;

		public Point(HColor color) {
			this.color = color;
		}

		public XDimension2D calculateDimension(StringBounder stringBounder) {
			return new XDimension2D(getDiameter(), getDiameter());
		}

		public void drawU(UGraphic ug) {
			final UShape point = new UEllipse(getDiameter(), getDiameter());
			ug = ug.apply(color).apply(color.bg());
			ug.draw(point);
		}

		private double getDiameter() {
			return 7;
		}

	}

	public void setTotalWidth(double totalWidth) {
		this.totalWidth = totalWidth;
	}

}
