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
package net.sourceforge.plantuml.timingdiagram;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.UseStyle;
import net.sourceforge.plantuml.awt.geom.Dimension2D;
import net.sourceforge.plantuml.command.Position;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.StyleSignature;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.timingdiagram.graphic.IntricatedPoint;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class PlayerBinary extends Player {

	private static final String LOW_STRING = "0";
	private static final String HIGH_STRING = "1";

	private final List<TimeConstraint> constraints = new ArrayList<>();
	private final SortedMap<TimeTick, ChangeState> values = new TreeMap<>();
	private ChangeState initialState;

	public PlayerBinary(String code, ISkinParam skinParam, TimingRuler ruler, boolean compact, Stereotype stereotype) {
		super(code, skinParam, ruler, compact, stereotype);
		this.suggestedHeight = 30;
	}

	private double getHeightForConstraints(StringBounder stringBounder) {
		return TimeConstraint.getHeightForConstraints(stringBounder, constraints);
	}

	public double getFullHeight(StringBounder stringBounder) {
		return getHeightForConstraints(stringBounder) + suggestedHeight;
	}

	@Override
	protected StyleSignature getStyleSignature() {
		return StyleSignatureBasic.of(SName.root, SName.element, SName.timingDiagram, SName.binary).withTOBECHANGED(stereotype);
	}

	public IntricatedPoint getTimeProjection(StringBounder stringBounder, TimeTick tick) {
		final double x = ruler.getPosInPixel(tick);
		return new IntricatedPoint(new Point2D.Double(x, getYpos(stringBounder, HIGH_STRING)),
				new Point2D.Double(x, getYpos(stringBounder, HIGH_STRING)));
	}

	public void addNote(TimeTick now, Display note, Position position) {
		throw new UnsupportedOperationException();
	}

	public void defineState(String stateCode, String label) {
		throw new UnsupportedOperationException();
	}

	public void setState(TimeTick now, String comment, Colors color, String... states) {
		final ChangeState cs = new ChangeState(now, comment, color, convert(states[0]));
		if (now == null)
			this.initialState = cs;
		else
			this.values.put(now, cs);

	}

	private String[] convert(String value) {
		if ("1".equals(value) || "high".equalsIgnoreCase(value))
			return new String[] { HIGH_STRING };
		return new String[] { LOW_STRING };
	}

	public void createConstraint(TimeTick tick1, TimeTick tick2, String message) {
		this.constraints.add(new TimeConstraint(tick1, tick2, message, skinParam));
	}

	private final double ymargin = 8;

	private double getYpos(StringBounder stringBounder, String state) {
		if (state.equalsIgnoreCase(LOW_STRING))
			return getYlow(stringBounder);
		return getYhigh(stringBounder);
	}

	private double getYlow(StringBounder stringBounder) {
		return getFullHeight(stringBounder) - ymargin;
	}

	private double getYhigh(StringBounder stringBounder) {
		return ymargin + getHeightForConstraints(stringBounder);
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
				String lastValue = initialState == null ? LOW_STRING : initialState.getState();
				final StringBounder stringBounder = ug.getStringBounder();
				final ULine vline = ULine.vline(getYlow(stringBounder) - getYhigh(stringBounder));
				for (Map.Entry<TimeTick, ChangeState> ent : values.entrySet()) {
					final ChangeState value = ent.getValue();
					final double x = ruler.getPosInPixel(ent.getKey());

					ug.apply(new UTranslate(lastx, getYpos(stringBounder, lastValue))).draw(ULine.hline(x - lastx));
					if (lastValue.equalsIgnoreCase(value.getState()) == false)
						ug.apply(new UTranslate(x, getYhigh(stringBounder))).draw(vline);

					if (value.getComment() != null) {
						final TextBlock label = getTextBlock(value.getComment());
						// final Dimension2D dim = label.calculateDimension(ug.getStringBounder());
						label.drawU(ug.apply(new UTranslate(x + 2, getYhigh(stringBounder))));
					}

					lastx = x;
					lastValue = value.getState();
				}
				ug.apply(new UTranslate(lastx, getYpos(stringBounder, lastValue)))
						.draw(ULine.hline(ruler.getWidth() - lastx));

				drawConstraints(ug.apply(UTranslate.dy(getHeightForConstraints(ug.getStringBounder()))));

			}
		};
	}

	final protected FontConfiguration getCommentFontConfiguration() {
		if (UseStyle.useBetaStyle() == false)
			return FontConfiguration.create(skinParam, FontParam.TIMING, null);
		return FontConfiguration.create(skinParam,
				getStyleSignature().getMergedStyle(skinParam.getCurrentStyleBuilder()));
	}

	private TextBlock getTextBlock(String value) {
		final Display display = Display.getWithNewlines(value);
		return display.create(getCommentFontConfiguration(), HorizontalAlignment.LEFT, skinParam);
	}

	private void drawConstraints(final UGraphic ug) {
		for (TimeConstraint constraint : constraints) {
			constraint.drawU(ug, ruler);
		}
	}

}
