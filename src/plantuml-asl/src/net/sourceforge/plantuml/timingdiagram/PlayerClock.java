/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
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

import java.math.BigDecimal;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.command.Position;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.HtmlColorUtils;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.SymbolContext;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class PlayerClock extends ReallyAbstractPlayer implements Player {

	private final int period;
	private final int pulse;

	public PlayerClock(TitleStrategy titleStrategy, ISkinParam skinParam, TimingRuler ruler, int period, int pulse) {
		super(titleStrategy, "", skinParam, ruler);
		this.period = period;
		this.pulse = pulse;
	}

	public double getHeight(StringBounder striWngBounder) {
		return 30;
	}

	private SymbolContext getContext() {
		return new SymbolContext(HtmlColorUtils.COL_D7E0F2, HtmlColorUtils.COL_038048).withStroke(new UStroke(1.5));
	}

	public IntricatedPoint getTimeProjection(StringBounder stringBounder, TimeTick tick) {
		throw new UnsupportedOperationException();
	}

	public void addNote(TimeTick now, Display note, Position position) {
		throw new UnsupportedOperationException();
	}

	public void defineState(String stateCode, String label) {
		throw new UnsupportedOperationException();
	}

	public void setState(TimeTick now, String comment, Colors color, String... states) {
		throw new UnsupportedOperationException();
	}

	public void createConstraint(TimeTick tick1, TimeTick tick2, String message) {
		throw new UnsupportedOperationException();
	}

	private final double ymargin = 8;

	public void drawFrameTitle(UGraphic ug) {
	}

	public void drawContent(UGraphic ug) {
		ug = getContext().apply(ug);
		final ULine vline = new ULine(0, getHeight(ug.getStringBounder()) - 2 * ymargin);
		int i = 0;
		double lastx = -Double.MAX_VALUE;
		while (i < 1000) {
			final double x = ruler.getPosInPixel(new BigDecimal(i * period));
			if (x > ruler.getWidth()) {
				return;
			}
			i++;
			if (x > lastx) {
				final double dx = x - lastx;
				final ULine hline1 = new ULine(dx * getPulseCoef(), 0);
				final ULine hline2 = new ULine(dx * (1 - getPulseCoef()), 0);
				ug.apply(new UTranslate(lastx, ymargin)).draw(vline);
				ug.apply(new UTranslate(lastx, ymargin)).draw(hline1);
				final double x2 = lastx + dx * getPulseCoef();
				ug.apply(new UTranslate(x2, ymargin)).draw(vline);
				ug.apply(new UTranslate(x2, ymargin + vline.getDY())).draw(hline2);
			}
			lastx = x;
		}
	}

	private double getPulseCoef() {
		if (pulse == 0) {
			return 0.5;
		}
		return 1.0 * pulse / period;
	}

	public void drawLeftHeader(UGraphic ug) {

	}

	public double getWidthHeader(StringBounder stringBounder) {
		return 0;
	}

	public final int getPeriod() {
		return period;
	}

}
