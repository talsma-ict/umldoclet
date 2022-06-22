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

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.UmlDiagram;
import net.sourceforge.plantuml.UmlDiagramType;
import net.sourceforge.plantuml.api.ThemeStyle;
import net.sourceforge.plantuml.awt.geom.Dimension2D;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.InnerStrategy;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.svek.TextBlockBackcolored;
import net.sourceforge.plantuml.timingdiagram.graphic.IntricatedPoint;
import net.sourceforge.plantuml.timingdiagram.graphic.TimeArrow;
import net.sourceforge.plantuml.ugraphic.MinMax;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class TimingDiagram extends UmlDiagram implements Clocks {

	public static final double marginX1 = 5;
	private final double marginX2 = 5;

	private final Map<String, TimeTick> codes = new HashMap<String, TimeTick>();
	private final Map<String, Player> players = new LinkedHashMap<String, Player>();
	private final Map<String, PlayerClock> clocks = new HashMap<String, PlayerClock>();
	private final List<TimeMessage> messages = new ArrayList<>();
	private final List<Highlight> highlights = new ArrayList<>();
	private final TimingRuler ruler = new TimingRuler(getSkinParam());
	private TimeTick now;
	private Player lastPlayer;
	private TimeAxisStategy timeAxisStategy = TimeAxisStategy.AUTOMATIC;
	private boolean compactByDefault = false;

	public DiagramDescription getDescription() {
		return new DiagramDescription("(Timing Diagram)");
	}

	public TimingDiagram(ThemeStyle style, UmlSource source) {
		super(style, source, UmlDiagramType.TIMING, null);
	}

	@Override
	protected ImageData exportDiagramInternal(OutputStream os, int index, FileFormatOption fileFormatOption)
			throws IOException {

		return createImageBuilder(fileFormatOption).drawable(getTextBlock()).write(os);
	}

	private TextBlockBackcolored getTextBlock() {
		return new TextBlockBackcolored() {

			public void drawU(UGraphic ug) {
				drawInternal(ug);
			}

			public Rectangle2D getInnerPosition(String member, StringBounder stringBounder, InnerStrategy strategy) {
				return null;
			}

			public Dimension2D calculateDimension(StringBounder stringBounder) {
				final double withBeforeRuler = getPart1MaxWidth(stringBounder);
				final double totalWith = withBeforeRuler + ruler.getWidth() + marginX1 + marginX2;
				return new Dimension2DDouble(totalWith, getHeightTotal(stringBounder));
			}

			public MinMax getMinMax(StringBounder stringBounder) {
				throw new UnsupportedOperationException();
			}

			public HColor getBackcolor() {
				return null;
			}
		};
	}

	private StyleSignatureBasic getStyleSignature() {
		return StyleSignatureBasic.of(SName.root, SName.element, SName.timingDiagram);
	}

	private HColor black() {
		final Style style = getStyleSignature().getMergedStyle(getSkinParam().getCurrentStyleBuilder());
		return style.value(PName.LineColor).asColor(getSkinParam().getThemeStyle(), getSkinParam().getIHtmlColorSet());

	}

	private void drawInternal(UGraphic ug) {
		ruler.ensureNotEmpty();
		final StringBounder stringBounder = ug.getStringBounder();
		final double part1MaxWidth = getPart1MaxWidth(stringBounder);
		final UTranslate widthPart1 = UTranslate.dx(part1MaxWidth);
		if (compactByDefault == false)
			drawBorder(ug);

		ug = ug.apply(UTranslate.dx(marginX1));

		drawHighlightsBack(ug.apply(widthPart1));
		ruler.drawVlines(ug.apply(widthPart1), getHeightInner(stringBounder));
		boolean first = true;

		for (Player player : players.values()) {
			final UGraphic ugPlayer = ug.apply(getUTranslateForPlayer(player, stringBounder));
			final double caption = getHeightForCaptions(stringBounder);
			if (first) {
				if (player.isCompact() == false)
					drawHorizontalSeparator(ugPlayer);

				player.getPart1(part1MaxWidth, caption).drawU(ugPlayer);
				player.getPart2().drawU(ugPlayer.apply(widthPart1).apply(UTranslate.dy(caption)));
			} else {
				if (player.isCompact() == false)
					drawHorizontalSeparator(ugPlayer.apply(UTranslate.dy(caption)));

				player.getPart1(part1MaxWidth, 0).drawU(ugPlayer.apply(UTranslate.dy(caption)));
				player.getPart2().drawU(ugPlayer.apply(widthPart1).apply(UTranslate.dy(caption)));
			}
			first = false;
		}
		ug = ug.apply(widthPart1);
		ruler.drawTimeAxis(ug.apply(getLastTranslate(stringBounder)), this.timeAxisStategy, codes);

		for (TimeMessage timeMessage : messages)
			drawMessages(ug, timeMessage);

		drawHighlightsLines(ug);
	}

	private void drawHorizontalSeparator(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		ug = ug.apply(black());
		ug = ug.apply(getBorderStroke());
		ug = ug.apply(UTranslate.dx(-marginX1));
		ug.draw(ULine.hline(getWidthTotal(stringBounder)));
	}

	private void drawBorder(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		final ULine border = ULine.vline(getLastTranslate(stringBounder).getDy());
		ug = ug.apply(black()).apply(getBorderStroke());
		ug.draw(border);
		ug.apply(UTranslate.dx(getWidthTotal(stringBounder))).draw(border);
	}

	private UStroke getBorderStroke() {
		return getStyleSignature().getMergedStyle(getCurrentStyleBuilder()).getStroke();
	}

	private UTranslate getLastTranslate(final StringBounder stringBounder) {
		return getUTranslateForPlayer(null, stringBounder).compose(UTranslate.dy(getHeightForCaptions(stringBounder)));
	}

	private void drawHighlightsBack(UGraphic ug) {
		final double height = getHeightInner(ug.getStringBounder());
		for (Highlight highlight : highlights)
			highlight.drawHighlightsBack(ug, ruler, height);

	}

	private void drawHighlightsLines(UGraphic ug) {
		final double height = getHeightInner(ug.getStringBounder());
		for (Highlight highlight : highlights) {
			highlight.drawHighlightsLines(ug, ruler, height);
			final double start = ruler.getPosInPixel(highlight.getTickFrom());
			highlight.getCaption(getSkinParam()).drawU(ug.apply(new UTranslate(start + 3, 2)));
		}
	}

	private double getHeightTotal(StringBounder stringBounder) {
		return getHeightInner(stringBounder) + ruler.getHeight(stringBounder);
	}

	private double getHeightInner(StringBounder stringBounder) {
		return getLastTranslate(stringBounder).getDy();
	}

	private double getHeightForCaptions(StringBounder stringBounder) {
		double result = 0;
		for (Highlight highlight : highlights) {
			final TextBlock caption = highlight.getCaption(getSkinParam());
			result = Math.max(result, caption.calculateDimension(stringBounder).getHeight());
		}
		return result;
	}

	private double getWidthTotal(final StringBounder stringBounder) {
		return getPart1MaxWidth(stringBounder) + ruler.getWidth() + marginX1 + marginX2;
	}

	private double getPart1MaxWidth(StringBounder stringBounder) {
		double width = 0;
		for (Player player : players.values())
			width = Math.max(width, player.getPart1(0, 0).calculateDimension(stringBounder).getWidth());

		return width;
	}

	private void drawMessages(UGraphic ug, TimeMessage message) {
		final Player player1 = message.getPlayer1();
		final Player player2 = message.getPlayer2();

		final StringBounder stringBounder = ug.getStringBounder();
		final UTranslate translate1 = getUTranslateForPlayer(player1, stringBounder)
				.compose(UTranslate.dy(getHeightForCaptions(stringBounder)));
		final UTranslate translate2 = getUTranslateForPlayer(player2, stringBounder)
				.compose(UTranslate.dy(getHeightForCaptions(stringBounder)));

		final IntricatedPoint pt1 = player1.getTimeProjection(stringBounder, message.getTick1());
		final IntricatedPoint pt2 = player2.getTimeProjection(stringBounder, message.getTick2());

		if (pt1 == null || pt2 == null)
			return;

		final TimeArrow timeArrow = TimeArrow.create(pt1.translated(translate1), pt2.translated(translate2),
				message.getLabel(), getSkinParam(), message);
		timeArrow.drawU(ug);

	}

	private UTranslate getUTranslateForPlayer(Player candidat, StringBounder stringBounder) {
		double y = 0;
		for (Player player : players.values()) {
			if (candidat == player)
				return UTranslate.dy(y);

//			if (y == 0) {
//				y += getHeightHighlights(stringBounder);
//			}
			y += player.getFullHeight(stringBounder);
		}
		if (candidat == null)
			return UTranslate.dy(y);

		throw new IllegalArgumentException();
	}

	public CommandExecutionResult createRobustConcise(String code, String full, TimingStyle type, boolean compact,
			Stereotype stereotype) {
		final Player player = new PlayerRobustConcise(type, full, getSkinParam(), ruler, compactByDefault || compact,
				stereotype);
		players.put(code, player);
		lastPlayer = player;
		return CommandExecutionResult.ok();
	}

	public CommandExecutionResult createClock(String code, String full, int period, int pulse, int offset,
			boolean compact) {
		final PlayerClock player = new PlayerClock(full, getSkinParam(), ruler, period, pulse, offset,
				compactByDefault);
		players.put(code, player);
		clocks.put(code, player);
		final TimeTick tick = new TimeTick(new BigDecimal(period), TimingFormat.DECIMAL);
		ruler.addTime(tick);
		return CommandExecutionResult.ok();
	}

	public PlayerAnalog createAnalog(String code, String full, boolean compact) {
		final PlayerAnalog player = new PlayerAnalog(full, getSkinParam(), ruler, compactByDefault);
		players.put(code, player);
		return player;
	}

	public CommandExecutionResult createBinary(String code, String full, boolean compact, Stereotype stereotype) {
		final Player player = new PlayerBinary(full, getSkinParam(), ruler, compactByDefault, stereotype);
		players.put(code, player);
		return CommandExecutionResult.ok();
	}

	public TimeMessage createTimeMessage(Player player1, TimeTick time1, Player player2, TimeTick time2, String label) {
		final TimeMessage message = new TimeMessage(new TickInPlayer(player1, time1), new TickInPlayer(player2, time2),
				label, getSkinParam());
		messages.add(message);
		return message;
	}

	public void addTime(TimeTick time, String code) {
		this.now = time;
		ruler.addTime(time);
		if (code != null)
			this.codes.put(code, time);

	}

	public TimeTick getCodeValue(String code) {
		return codes.get(code);
	}

	public void updateNow(TimeTick time) {
		this.now = time;
	}

	public Player getPlayer(String code) {
		return players.get(code);
	}

	public TimeTick getNow() {
		return now;
	}

	public TimeTick getClockValue(String clockName, int nb) {
		final PlayerClock clock = clocks.get(clockName);
		if (clock == null)
			return null;

		return new TimeTick(new BigDecimal(nb * clock.getPeriod()), TimingFormat.DECIMAL);
	}

	public void setLastPlayer(Player player) {
		this.lastPlayer = player;
	}

	public Player getLastPlayer() {
		return lastPlayer;
	}

	public void scaleInPixels(long tick, long pixel) {
		ruler.scaleInPixels(tick, pixel);
	}

	public CommandExecutionResult setTimeAxisStategy(TimeAxisStategy newStrategy) {
		this.timeAxisStategy = newStrategy;
		return CommandExecutionResult.ok();
	}

	public CommandExecutionResult highlight(TimeTick tickFrom, TimeTick tickTo, Display caption, Colors colors) {
		this.highlights.add(new Highlight(getSkinParam(), tickFrom, tickTo, caption, colors));
		return CommandExecutionResult.ok();

	}

	public void goCompactMode() {
		this.compactByDefault = true;
	}

	private SimpleDateFormat sdf;

	public CommandExecutionResult useDateFormat(String dateFormat) {
		try {
			this.sdf = new SimpleDateFormat(dateFormat, Locale.US);
		} catch (Exception e) {
			return CommandExecutionResult.error("Bad date format");
		}

		return CommandExecutionResult.ok();
	}

	@Override
	public TimingFormat getTimingFormatDate() {
		if (sdf == null)
			return TimingFormat.DATE;
		return TimingFormat.create(sdf);
	}

}
