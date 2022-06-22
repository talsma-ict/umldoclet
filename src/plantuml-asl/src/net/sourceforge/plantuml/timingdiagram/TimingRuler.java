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

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.awt.geom.Dimension2D;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;

public class TimingRuler {

	private final SortedSet<TimeTick> times = new TreeSet<>();

	private final ISkinParam skinParam;

	private long tickIntervalInPixels = 50;
	private long tickUnitary;

	private TimingFormat format = TimingFormat.DECIMAL;

	static UGraphic applyForVLines(UGraphic ug, Style style, ISkinParam skinParam) {
		final UStroke stroke = new UStroke(3, 5, 0.5);
		final HColor color = style.value(PName.LineColor).asColor(skinParam.getThemeStyle(),
				skinParam.getIHtmlColorSet());

		return ug.apply(stroke).apply(color);
	}

	public void ensureNotEmpty() {
		if (times.size() == 0)
			this.times.add(new TimeTick(BigDecimal.ZERO, TimingFormat.DECIMAL));

		if (getMax().getTime().signum() > 0 && getMin().getTime().signum() < 0)
			this.times.add(new TimeTick(BigDecimal.ZERO, TimingFormat.DECIMAL));

	}

	public TimingRuler(ISkinParam skinParam) {
		this.skinParam = skinParam;
	}

	public void scaleInPixels(long tick, long pixel) {
		if (pixel <= 0 || tick <= 0)
			throw new IllegalArgumentException();
		this.tickIntervalInPixels = pixel;
		this.tickUnitary = tick;
	}

	private long tickUnitary() {
		if (tickUnitary == 0)
			return highestCommonFactor();

		return tickUnitary;

	}

	private long highestCommonFactorInternal = -1;

	private long highestCommonFactor() {
		if (highestCommonFactorInternal == -1) {
			for (long tick : getAbsolutesTicks()) {
				if (highestCommonFactorInternal == -1) {
					highestCommonFactorInternal = tick;
				} else {
					final long candidate = computeHighestCommonFactor(highestCommonFactorInternal, tick);
					final double size = (getMax().getTime().doubleValue() - getMin().getTime().doubleValue())
							/ candidate;
					if (size > 200)
						return highestCommonFactorInternal;

					highestCommonFactorInternal = candidate;
				}
			}
		}
		return highestCommonFactorInternal;
	}

	private Set<Long> getAbsolutesTicks() {
		final Set<Long> result = new TreeSet<>(new Comparator<Long>() {
			public int compare(Long o1, Long o2) {
				return o2.compareTo(o1);
			}
		});
		for (TimeTick time : times) {
			final long value = Math.abs(time.getTime().longValue());
			if (value > 0)
				result.add(value);

		}
		return result;
	}

	private int getNbTick() {
		if (times.size() == 0)
			return 1;

		final long delta = getMax().getTime().longValue() - getMin().getTime().longValue();
		return Math.min(1000, (int) (1 + delta / tickUnitary()));
	}

	public double getWidth() {
		final double delta = getMax().getTime().doubleValue() - getMin().getTime().doubleValue();

		return (delta / tickUnitary() + 1) * tickIntervalInPixels;
	}

	public final double getPosInPixel(TimeTick when) {
		return getPosInPixelInternal(when.getTime().doubleValue());
	}

	private double getPosInPixelInternal(double time) {
		time -= getMin().getTime().doubleValue();
		return time / tickUnitary() * tickIntervalInPixels;
	}

	private long tickToTime(int i) {
		return tickUnitary * i + getMin().getTime().longValue();
	}

	public void addTime(TimeTick time) {
		this.highestCommonFactorInternal = -1;
		times.add(time);
		if (time.getFormat() != TimingFormat.DECIMAL)
			this.format = time.getFormat();

	}

	private FontConfiguration getFontConfiguration() {
		return FontConfiguration.create(skinParam, getStyle());
	}

	private Style getStyle() {
		return StyleSignatureBasic.of(SName.root, SName.element, SName.timingDiagram, SName.timeline)
				.getMergedStyle(skinParam.getCurrentStyleBuilder());
	}

	private TextBlock getTimeTextBlock(long time) {
		return getTimeTextBlock(format.formatTime(time));
	}

	private TextBlock getTimeTextBlock(String string) {
		final Display display = Display.getWithNewlines(string);
		return display.create(getFontConfiguration(), HorizontalAlignment.LEFT, skinParam);
	}

	public void drawTimeAxis(UGraphic ug, TimeAxisStategy timeAxisStategy, Map<String, TimeTick> codes) {
		if (timeAxisStategy == TimeAxisStategy.HIDDEN)
			return;

		final Style style = StyleSignatureBasic.of(SName.root, SName.timingDiagram, SName.timeline)
				.getMergedStyle(skinParam.getCurrentStyleBuilder());

		final HColor color = style.value(PName.LineColor).asColor(skinParam.getThemeStyle(),
				skinParam.getIHtmlColorSet());
		final UStroke stroke = style.getStroke();

		ug = ug.apply(stroke).apply(color);

		if (timeAxisStategy == TimeAxisStategy.AUTOMATIC)
			drawTimeAxisAutomatic(ug);
		else
			drawTimeAxisManual(ug, codes);

	}

	private void drawTimeAxisManual(UGraphic ug, Map<String, TimeTick> codes) {
		final double tickHeight = 5;
		final ULine line = ULine.vline(tickHeight);
		final double firstTickPosition = getPosInPixelInternal(getFirstPositiveOrZeroValue().doubleValue());
		int nb = 0;
		while (firstTickPosition + nb * tickIntervalInPixels <= getWidth())
			nb++;

		ug.apply(UTranslate.dx(firstTickPosition)).draw(ULine.hline((nb - 1) * tickIntervalInPixels));

		for (TimeTick tick : times) {
			ug.apply(UTranslate.dx(getPosInPixel(tick))).draw(line);
			final String label = getLabel(tick, codes);
			if (label.length() == 0)
				continue;
			final TextBlock text = getTimeTextBlock(label);
			final Dimension2D dim = text.calculateDimension(ug.getStringBounder());
			text.drawU(ug.apply(new UTranslate(getPosInPixel(tick) - dim.getWidth() / 2, tickHeight + 1)));

		}
	}

	private void drawTimeAxisAutomatic(UGraphic ug) {
		final double tickHeight = 5;
		final ULine line = ULine.vline(tickHeight);
		final double firstTickPosition = getPosInPixelInternal(getFirstPositiveOrZeroValue().doubleValue());
		int nb = 0;
		while (firstTickPosition + nb * tickIntervalInPixels <= getWidth()) {
			ug.apply(UTranslate.dx(firstTickPosition + nb * tickIntervalInPixels)).draw(line);
			nb++;
		}
		ug.apply(UTranslate.dx(firstTickPosition)).draw(ULine.hline((nb - 1) * tickIntervalInPixels));

		for (long round : roundValues()) {
			final TextBlock text = getTimeTextBlock(round);
			final Dimension2D dim = text.calculateDimension(ug.getStringBounder());
			text.drawU(ug.apply(new UTranslate(getPosInPixelInternal(round) - dim.getWidth() / 2, tickHeight + 1)));
		}
	}

	private String getLabel(TimeTick tick, Map<String, TimeTick> codes) {
		for (Entry<String, TimeTick> ent : codes.entrySet())
			if (tick.equals(ent.getValue()))
				return ent.getKey();

		return format.formatTime(tick.getTime());
	}

	private BigDecimal getFirstPositiveOrZeroValue() {
		for (TimeTick time : times)
			if (time.getTime().signum() >= 0)
				return time.getTime();

		throw new IllegalStateException();
	}

	private Collection<Long> roundValues() {
		final SortedSet<Long> result = new TreeSet<>();
		if (tickUnitary == 0) {
			for (TimeTick tick : times) {
				final long round = tick.getTime().longValue();
				result.add(round);
			}
		} else {
			final int nb = getNbTick();
			for (int i = 0; i <= nb; i++) {
				final long round = tickToTime(i);
				result.add(round);
			}
		}
		if (result.first() < 0 && result.last() > 0)
			result.add(0L);

		return result;
	}

	public void drawVlines(UGraphic ug, double height) {
		ug = applyForVLines(ug, getStyle(), skinParam);
		final ULine line = ULine.vline(height);
		final int nb = getNbTick();
		for (int i = 0; i <= nb; i++)
			ug.apply(UTranslate.dx(tickIntervalInPixels * i)).draw(line);

	}

	public double getHeight(StringBounder stringBounder) {
		return getTimeTextBlock(0).calculateDimension(stringBounder).getHeight();
	}

	private TimeTick getMax() {
		return times.last();
	}

	private TimeTick getMin() {
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

}
