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
package net.sourceforge.plantuml.svek;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.BackSlash;
import net.sourceforge.plantuml.OptionPrint;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.cucadiagram.dot.GraphvizUtils;
import net.sourceforge.plantuml.flashcode.FlashCodeFactory;
import net.sourceforge.plantuml.flashcode.FlashCodeUtils;
import net.sourceforge.plantuml.fun.IconLoader;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.GraphicPosition;
import net.sourceforge.plantuml.graphic.GraphicStrings;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.QuoteUtils;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.ugraphic.AffineTransformType;
import net.sourceforge.plantuml.ugraphic.PixelImage;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UImage;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;
import net.sourceforge.plantuml.version.PSystemVersion;
import net.sourceforge.plantuml.version.Version;

public class GraphvizCrash extends AbstractTextBlock implements IEntityImage {

	private final TextBlock text1;
	private final BufferedImage flashCode;
	private final String text;
	private final boolean graphviz244onWindows;

	public GraphvizCrash(String text, boolean graphviz244onWindows, Throwable rootCause) {
		this.text = text;
		this.graphviz244onWindows = graphviz244onWindows;
		final FlashCodeUtils utils = FlashCodeFactory.getFlashCodeUtils();
		this.flashCode = utils.exportFlashcode(text, Color.BLACK, Color.WHITE);
		this.text1 = GraphicStrings.createBlackOnWhite(init(rootCause), IconLoader.getRandom(),
				GraphicPosition.BACKGROUND_CORNER_TOP_RIGHT);
	}

	public static List<String> anErrorHasOccured(Throwable exception, String text) {
		final List<String> strings = new ArrayList<String>();
		if (exception == null) {
			strings.add("An error has occured!");
		} else {
			strings.add("An error has occured : " + exception);
		}
		final String quote = StringUtils.rot(QuoteUtils.getSomeQuote());
		strings.add("<i>" + quote);
		strings.add(" ");
		strings.add("Diagram size: " + lines(text) + " lines / " + text.length() + " characters.");
		strings.add(" ");
		return strings;
	}

	private static int lines(String text) {
		int result = 0;
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == BackSlash.CHAR_NEWLINE) {
				result++;
			}
		}
		return result;
	}

	public static void checkOldVersionWarning(List<String> strings) {
		final long days = (System.currentTimeMillis() - Version.compileTime()) / 1000L / 3600 / 24;
		if (days >= 90) {
			strings.add(" ");
			strings.add("<b>This version of PlantUML is " + days + " days old, so you should");
			strings.add("<b>consider upgrading from https://plantuml.com/download");
		}
	}

	public static void pleaseGoTo(List<String> strings) {
		strings.add(" ");
		strings.add("Please go to https://plantuml.com/graphviz-dot to check your GraphViz version.");
		strings.add(" ");
	}

	public static void youShouldSendThisDiagram(List<String> strings) {
		strings.add("You should send this diagram and this image to <b>plantuml@gmail.com</b> or");
		strings.add("post to <b>https://plantuml.com/qa</b> to solve this issue.");
		strings.add("You can try to turn arround this issue by simplifing your diagram.");
	}

	public static void thisMayBeCaused(final List<String> strings) {
		strings.add("This may be caused by :");
		strings.add(" - a bug in PlantUML");
		strings.add(" - a problem in GraphViz");
	}

	private List<String> init(Throwable rootCause) {
		final List<String> strings = anErrorHasOccured(null, text);
		strings.add("For some reason, dot/GraphViz has crashed.");
		strings.add("");
		strings.add("RootCause " + rootCause);
		if (rootCause != null) {
			strings.addAll(CommandExecutionResult.getStackTrace(rootCause));
		}
		strings.add("");
		strings.add("This has been generated with PlantUML (" + Version.versionString() + ").");
		checkOldVersionWarning(strings);
		strings.add(" ");
		addProperties(strings);
		strings.add(" ");
		try {
			final String dotVersion = GraphvizUtils.dotVersion();
			strings.add("Default dot version: " + dotVersion);
		} catch (Throwable e) {
			strings.add("Cannot determine dot version: " + e.toString());
		}
		pleaseGoTo(strings);
		youShouldSendThisDiagram(strings);
		if (flashCode != null) {
			addDecodeHint(strings);
		}

		return strings;
	}

	private List<String> getText2() {
		final List<String> strings = new ArrayList<String>();
		strings.add(" ");
		strings.add("<b>It looks like you are running GraphViz 2.44 under Windows.");
		strings.add("If you have just installed GraphViz, you <i>may</i> have to execute");
		strings.add("the post-install command <b>dot -c</b> like in the following example:");
		return strings;
	}

	private List<String> getText3() {
		final List<String> strings = new ArrayList<String>();
		strings.add(" ");
		strings.add("You may have to have <i>Administrator rights</i> to avoid the following error message:");
		return strings;
	}

	public static void addDecodeHint(final List<String> strings) {
		strings.add(" ");
		strings.add(" Diagram source: (Use http://zxing.org/w/decode.jspx to decode the qrcode)");
	}

	public static void addProperties(final List<String> strings) {
		strings.addAll(OptionPrint.interestingProperties());
		strings.addAll(OptionPrint.interestingValues());
	}

	public boolean isHidden() {
		return false;
	}

	public HColor getBackcolor() {
		return HColorUtils.WHITE;
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		return getMain().calculateDimension(stringBounder);
	}

	public void drawU(UGraphic ug) {
		getMain().drawU(ug);
	}

	private TextBlock getMain() {
		TextBlock result = text1;
		if (flashCode != null) {
			final UImage flash = new UImage(new PixelImage(flashCode, AffineTransformType.TYPE_NEAREST_NEIGHBOR))
					.scale(3);
			result = TextBlockUtils.mergeTB(result, flash, HorizontalAlignment.LEFT);
		}

		if (graphviz244onWindows) {
			final TextBlock text2 = GraphicStrings.createBlackOnWhite(getText2());
			result = TextBlockUtils.mergeTB(result, text2, HorizontalAlignment.LEFT);

			final UImage dotc = new UImage(new PixelImage(PSystemVersion.getDotc(), AffineTransformType.TYPE_BILINEAR));
			result = TextBlockUtils.mergeTB(result, dotc, HorizontalAlignment.LEFT);

			final TextBlock text3 = GraphicStrings.createBlackOnWhite(getText3());
			result = TextBlockUtils.mergeTB(result, text3, HorizontalAlignment.LEFT);

			final UImage dotd = new UImage(new PixelImage(PSystemVersion.getDotd(), AffineTransformType.TYPE_BILINEAR));
			result = TextBlockUtils.mergeTB(result, dotd, HorizontalAlignment.LEFT);
		}

		return result;
	}

	public ShapeType getShapeType() {
		return ShapeType.RECTANGLE;
	}

	public Margins getShield(StringBounder stringBounder) {
		return Margins.NONE;
	}

	public double getOverscanX(StringBounder stringBounder) {
		return 0;
	}

}
