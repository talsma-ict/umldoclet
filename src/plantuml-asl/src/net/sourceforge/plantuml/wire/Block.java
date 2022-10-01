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
package net.sourceforge.plantuml.wire;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.command.Position;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.ugraphic.MinMax;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColors;

public class Block extends AbstractTextBlock {

	static class Pos {
		final double x;
		final double y;

		public Pos(double x, double y) {
			this.x = x;
			this.y = y;
		}

		UGraphic move(UGraphic ug) {
			return ug.apply(new UTranslate(x, y));
		}
	}

	private final Map<Block, Pos> children = new LinkedHashMap<Block, Pos>();
	private final Display display;
	private final XDimension2D fixedDim;
	private final ISkinParam skinParam;

	private final List<String> left = new ArrayList<>();
	private final List<String> right = new ArrayList<>();
	private final List<String> top = new ArrayList<>();
	private final List<String> bottom = new ArrayList<>();

	private double x = 10;
	private double y = 10;

	private MinMax minMax = MinMax.getEmpty(true);
	private Block parent;

	public Block(ISkinParam skinParam) {
		this(skinParam, Display.empty(), null);
	}

	private Block(ISkinParam skinParam, Display display, XDimension2D fixedDim) {
		this.skinParam = skinParam;
		this.display = display;
		this.fixedDim = fixedDim;

	}

	private List<String> getPins(Position position) {
		switch (position) {
		case LEFT:
			return left;
		case RIGHT:
			return right;
		case TOP:
			return top;
		case BOTTOM:
			return bottom;
		}
		throw new IllegalArgumentException();
	}

	public XDimension2D calculateDimension(StringBounder stringBounder) {
		if (fixedDim == null)
			return minMax.getDimension();

		return fixedDim;
	}

	public void drawU(UGraphic ug) {
		ug = ug.apply(getBlack());
		if (children.size() == 0) {
			final TextBlock label = display.create(FontConfiguration.create(skinParam, FontParam.COMPONENT, null),
					HorizontalAlignment.CENTER, skinParam);
			label.drawU(ug.apply(new UTranslate(10, 10)));
		} else {
			for (Entry<Block, Pos> ent : children.entrySet()) {
				ent.getKey().drawU(ent.getValue().move(ug));
			}
		}
		ug.draw(new URectangle(calculateDimension(ug.getStringBounder())));

		drawPins(Position.BOTTOM, ug);
		drawPins(Position.TOP, ug);
		drawPins(Position.LEFT, ug);
		drawPins(Position.RIGHT, ug);

	}

	private HColor getBlack() {
		return HColors.BLACK.withDark(HColors.WHITE);
	}

	private void drawPins(Position pos, UGraphic ug) {
		double px = -2;
		double py = 10;
		if (pos == Position.RIGHT) {
			px = calculateDimension(ug.getStringBounder()).getWidth() - 2;
		}
		if (pos == Position.TOP) {
			px = 10;
			py = -2;
		}
		if (pos == Position.BOTTOM) {
			px = 10;
			py = calculateDimension(ug.getStringBounder()).getHeight() - 2;
		}
		for (String pin : getPins(pos)) {
			ug.apply(new UTranslate(px, py)).draw(new UEllipse(4, 4));
			if (pos == Position.LEFT || pos == Position.RIGHT) {
				py += 15;
			} else {
				px += 15;
			}
		}
	}

	public Block componentEnd() {
		parent.minMax = parent.minMax.addPoint(parent.x + this.minMax.getMaxX() + 10,
				parent.y + this.minMax.getMaxY() + 10);
		parent.x += this.minMax.getMaxX() + 10;
		return parent;
	}

	public Block addNewBlock(String name, int width, int height) {
		final XDimension2D dim = new XDimension2D(width, height);
		final Block child = new Block(skinParam, Display.create(name), dim);
		children.put(child, new Pos(x, y));
		y += dim.getHeight() + 10;
		minMax = minMax.addPoint(x + dim.getWidth() + 10, y);
		return child;
	}

	public Block createContainer(String name) {
		final Block result = new Block(skinParam);
		result.parent = this;
		children.put(result, new Pos(x, y));
		return result;
	}

	public void vspace(int vspace) {
		y += vspace - 10;
		minMax = minMax.addPoint(x, y);
	}

	public void newColumn() {
		this.x = minMax.getMaxX();
		this.y = 10;
	}

	public void addPin(Position position, String pin) {
		getPins(position).add(pin);
	}

}
