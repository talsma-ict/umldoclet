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
package net.sourceforge.plantuml.timingdiagram;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.command.Position;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.SymbolContext;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.timingdiagram.graphic.IntricatedPoint;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;

public class PlayerBinary extends Player {

	private static final int HEIGHT = 30;
	private final SortedMap<TimeTick, Boolean> values = new TreeMap<TimeTick, Boolean>();
	private Boolean initialState;

	public PlayerBinary(String code, ISkinParam skinParam, TimingRuler ruler, boolean compact) {
		super(code, skinParam, ruler, compact);
	}

	public double getFullHeight(StringBounder stringBounder) {
		return HEIGHT;
	}

	public void drawFrameTitle(UGraphic ug) {
	}

	private SymbolContext getContext() {
		return new SymbolContext(HColorUtils.COL_D7E0F2, HColorUtils.COL_038048).withStroke(new UStroke(1.5));
	}

	public IntricatedPoint getTimeProjection(StringBounder stringBounder, TimeTick tick) {
		final double x = ruler.getPosInPixel(tick);
		return new IntricatedPoint(new Point2D.Double(x, getYpos(false)), new Point2D.Double(x, getYpos(true)));
	}

	public void addNote(TimeTick now, Display note, Position position) {
		throw new UnsupportedOperationException();
	}

	public void defineState(String stateCode, String label) {
		throw new UnsupportedOperationException();
	}

	public void setState(TimeTick now, String comment, Colors color, String... states) {
		final boolean state = getState(states[0]);
		if (now == null) {
			this.initialState = state;
		} else {
			this.values.put(now, state);
		}
	}

	private boolean getState(String value) {
		return "1".equals(value) || "high".equalsIgnoreCase(value);
	}

	public void createConstraint(TimeTick tick1, TimeTick tick2, String message) {
		throw new UnsupportedOperationException();
	}

	private final double ymargin = 8;

	private double getYpos(boolean state) {
		return state ? ymargin : HEIGHT - ymargin;
	}

	public TextBlock getPart1(double fullAvailableWidth, double specialVSpace) {
		return new AbstractTextBlock() {

			public void drawU(UGraphic ug) {
				final StringBounder stringBounder = ug.getStringBounder();
				final TextBlock title = getTitle();
				final Dimension2D dim = title.calculateDimension(stringBounder);
				final double y = (getFullHeight(stringBounder) - dim.getHeight()) / 2;
				title.drawU(ug.apply(UTranslate.dy(y)));
			}

			public Dimension2D calculateDimension(StringBounder stringBounder) {
				final Dimension2D dim = getTitle().calculateDimension(stringBounder);
				return Dimension2DDouble.delta(dim, 5, 0);
			}
		};
	}

	public UDrawable getPart2() {
		return new UDrawable() {
			public void drawU(UGraphic ug) {
				ug = getContext().apply(ug);
				double lastx = 0;
				boolean lastValue = initialState == null ? false : initialState;
				for (Map.Entry<TimeTick, Boolean> ent : values.entrySet()) {
					final double x = ruler.getPosInPixel(ent.getKey());
					ug.apply(new UTranslate(lastx, getYpos(lastValue))).draw(ULine.hline(x - lastx));
					if (lastValue != ent.getValue()) {
						ug.apply(new UTranslate(x, ymargin)).draw(ULine.vline(HEIGHT - 2 * ymargin));
					}
					lastx = x;
					lastValue = ent.getValue();
				}
				ug.apply(new UTranslate(lastx, getYpos(lastValue))).draw(ULine.hline(ruler.getWidth() - lastx));
			}
		};
	}

}
