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

import java.awt.geom.Dimension2D;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColorSetSimple;
import net.sourceforge.plantuml.graphic.HtmlColorUtils;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class TimingRuler {

	private final SortedSet<TimeTick> times = new TreeSet<TimeTick>();
	private long highestCommonFactor = -1;
	private final ISkinParam skinParam;

	private long tickIntervalInPixels = 50;
	private long tickUnitary;

	public TimingRuler(ISkinParam skinParam) {
		this.skinParam = skinParam;
		this.times.add(new TimeTick(BigDecimal.ZERO));
	}

	public void scaleInPixels(long tick, long pixel) {
		this.tickIntervalInPixels = pixel;
		this.tickUnitary = tick;
	}

	private long tickUnitary() {
		if (tickUnitary == 0) {
			return highestCommonFactor;
		}
		return tickUnitary;

	}

	private int getNbTick() {
		if (times.size() == 0) {
			return 1;
		}
		final long delta = getMax().getTime().longValue() - getMin().getTime().longValue();
		return (int) (1 + delta / tickUnitary());
	}

	public double getWidth() {
		return getPosInPixel(new BigDecimal((getNbTick()) * tickUnitary()));
	}

	private double getPosInPixel(double time) {
		time -= getMin().getTime().doubleValue();
		return time / tickUnitary() * tickIntervalInPixels;
	}

	public void addTime(TimeTick time) {
		final boolean added = times.add(time);
		if (added) {
			long tick = time.getTime().longValue();
			if (tick > 0) {
				if (highestCommonFactor == -1) {
					highestCommonFactor = time.getTime().longValue();
				} else {
					highestCommonFactor = computeHighestCommonFactor(highestCommonFactor,
							Math.abs(time.getTime().longValue()));
				}
			}
		}
	}

	private FontConfiguration getFontConfiguration() {
		return new FontConfiguration(skinParam, FontParam.TIMING, null);
	}

	private TextBlock getTimeTextBlock(long time) {
		final Display display = Display.getWithNewlines("" + time);
		return display.create(getFontConfiguration(), HorizontalAlignment.LEFT, skinParam);
	}

	public void drawTimeAxis(UGraphic ug) {
		ug = ug.apply(new UStroke(2.0)).apply(new UChangeColor(HtmlColorUtils.BLACK));
		final double tickHeight = 5;
		final ULine line = new ULine(0, tickHeight);
		final int nb = getNbTick();
		for (int i = 0; i <= nb; i++) {
			ug.apply(new UTranslate(tickIntervalInPixels * i, 0)).draw(line);
		}
		ug.draw(new ULine(nb * tickIntervalInPixels, 0));

		for (long round : roundValues()) {
			final TextBlock text = getTimeTextBlock(round);
			final Dimension2D dim = text.calculateDimension(ug.getStringBounder());
			text.drawU(ug.apply(new UTranslate(getPosInPixel(round) - dim.getWidth() / 2, tickHeight + 1)));
		}
	}

	public void draw0(UGraphic ug, double height) {
		ug = ug.apply(new UStroke(3, 5, 0.5)).apply(new UChangeColor(new HtmlColorSetSimple().getColorIfValid("#AAA")));
		final ULine line = new ULine(0, height);
		final int nb = getNbTick();
		for (int i = 0; i <= nb; i++) {
			ug.apply(new UTranslate(tickIntervalInPixels * i, 0)).draw(line);
		}
	}

	public double getHeight(StringBounder stringBounder) {
		return getTimeTextBlock(0).calculateDimension(stringBounder).getHeight();
	}

	private Collection<Long> roundValues() {
		final Set<Long> result = new TreeSet<Long>();
		if (tickUnitary == 0) {
			for (TimeTick tick : times) {
				final long round = tick.getTime().longValue();
				result.add(round);
			}
		} else {
			final int nb = getNbTick();
			for (int i = 0; i <= nb; i++) {
				final long round = tickUnitary * i;
				result.add(round);
			}
		}
		return result;
	}

	private TimeTick getMax() {
		// if (times.size() == 0) {
		// throw new IllegalStateException("Empty list!");
		// }
		return times.last();
	}

	private TimeTick getMin() {
		// if (times.size() == 0) {
		// throw new IllegalStateException("Empty list!");
		// }
		return times.first();
	}

	private static long computeHighestCommonFactor(long a, long b) {
		long r = a;
		while (r != 0) {
			r = a % b;
			a = b;
			b = r;
		}
		return (Math.abs(a));
	}

	public final double getPosInPixel(BigDecimal time) {
		return getPosInPixel(time.doubleValue());
	}

	public final double getPosInPixel(TimeTick when) {
		return getPosInPixel(when.getTime());
	}

	public final double getMaxPosInPixel() {
		return getPosInPixel(getMax());
	}

}
