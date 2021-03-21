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
package net.sourceforge.plantuml.error;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.AbstractPSystem;
import net.sourceforge.plantuml.BackSlash;
import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.ErrorUml;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.FileImageData;
import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.SpriteContainerEmpty;
import net.sourceforge.plantuml.StringLocated;
import net.sourceforge.plantuml.api.ImageDataAbstract;
import net.sourceforge.plantuml.api.ImageDataSimple;
import net.sourceforge.plantuml.asciiart.UmlCharArea;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.eggs.PSystemWelcome;
import net.sourceforge.plantuml.flashcode.FlashCodeFactory;
import net.sourceforge.plantuml.flashcode.FlashCodeUtils;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.GraphicPosition;
import net.sourceforge.plantuml.graphic.GraphicStrings;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.InnerStrategy;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockRaw;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.graphic.VerticalAlignment;
import net.sourceforge.plantuml.security.SecurityUtils;
import net.sourceforge.plantuml.style.ClockwiseTopRightBottomLeft;
import net.sourceforge.plantuml.svek.GraphvizCrash;
import net.sourceforge.plantuml.svek.TextBlockBackcolored;
import net.sourceforge.plantuml.ugraphic.AffineTransformType;
import net.sourceforge.plantuml.ugraphic.ImageBuilder;
import net.sourceforge.plantuml.ugraphic.ImageParameter;
import net.sourceforge.plantuml.ugraphic.MinMax;
import net.sourceforge.plantuml.ugraphic.PixelImage;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UImage;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.ColorMapperIdentity;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorSet;
import net.sourceforge.plantuml.ugraphic.color.HColorSimple;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;
import net.sourceforge.plantuml.ugraphic.txt.UGraphicTxt;
import net.sourceforge.plantuml.version.LicenseInfo;
import net.sourceforge.plantuml.version.PSystemVersion;
import net.sourceforge.plantuml.version.Version;

public abstract class PSystemError extends AbstractPSystem {

	protected List<StringLocated> trace;
	protected ErrorUml singleError;

	final protected StringLocated getLastLine() {
		return trace.get(trace.size() - 1);
	}

	final public LineLocation getLineLocation() {
		return getLastLine().getLocation();
	}

	final public Collection<ErrorUml> getErrorsUml() {
		return Collections.singleton(singleError);
	}

	final public String getWarningOrError() {
		final StringBuilder sb = new StringBuilder();
		sb.append(getDescription());
		sb.append(BackSlash.CHAR_NEWLINE);
		for (CharSequence t : getTitle().getDisplay()) {
			sb.append(t);
			sb.append(BackSlash.CHAR_NEWLINE);
		}
		sb.append(BackSlash.CHAR_NEWLINE);
		return sb.toString();
	}

	private TextBlockBackcolored getGraphicalFormatted() {
		final FontConfiguration fc0 = GraphicStrings.sansSerif14(HColorUtils.BLACK).bold();
		final FontConfiguration fc1 = GraphicStrings.sansSerif14(HColorUtils.MY_GREEN).bold();
		final FontConfiguration fc2 = GraphicStrings.sansSerif14(HColorUtils.RED).bold();
		final FontConfiguration fc4 = GraphicStrings.sansSerif12(HColorUtils.MY_GREEN).bold().italic();

		final List<String> fullBody = getTextFullBody();
		final TextBlock result0 = TextBlockUtils.addBackcolor(
				TextBlockUtils.withMargin(new TextBlockRaw(getTextFromStack(), fc0), 1, 1, 1, 4), HColorUtils.MY_GREEN);
		final TextBlock result1 = new TextBlockRaw(allButLast(fullBody), fc1);
		final TextBlock result2 = new TextBlockRaw(onlyLast(fullBody), fc1.wave(HColorUtils.RED));
		final TextBlock result3 = new TextBlockRaw(getTextError(), fc2);
		final TextBlock result4 = TextBlockUtils.withMargin(new TextBlockRaw(header(), fc4), 0, 2, 0, 8);
		TextBlock result = result0;
		result = TextBlockUtils.mergeTB(result, result1, HorizontalAlignment.LEFT);
		result = TextBlockUtils.mergeTB(result, result2, HorizontalAlignment.LEFT);
		result = TextBlockUtils.mergeTB(result, result3, HorizontalAlignment.LEFT);
		result = TextBlockUtils.mergeTB(result4, result, HorizontalAlignment.LEFT);
		result = TextBlockUtils.withMargin(result, 5, 5);
		return TextBlockUtils.addBackcolor(result, HColorUtils.BLACK);
	}

	private List<String> header() {
		final List<String> result = new ArrayList<String>();
		result.add("PlantUML " + Version.versionString());
		GraphvizCrash.checkOldVersionWarning(result);
		return result;
	}

	private List<String> getPureAsciiFormatted() {
		final List<String> result = getTextFromStack();
		result.addAll(getTextFullBody());
		result.add("^^^^^");
		result.addAll(getTextError());
		return result;
	}

	private List<String> getTextFromStack() {
		LineLocation lineLocation = getLineLocation();
		final List<String> result = new ArrayList<String>();
		if (lineLocation != null) {
			append(result, lineLocation);
			while (lineLocation.getParent() != null) {
				lineLocation = lineLocation.getParent();
				append(result, lineLocation);
			}
		}
		return result;
	}

	protected List<String> getTextFullBody() {
		final List<String> result = new ArrayList<String>();
		result.add(" ");
		final int traceSize = trace.size();
		if (traceSize > 40) {
			for (StringLocated s : trace.subList(0, 5)) {
				addToResult(result, s);
			}
			result.add("...");
			final int skipped = traceSize - 5 - 20;
			result.add("... ( skipping " + skipped + " lines )");
			result.add("...");
			for (StringLocated s : trace.subList(traceSize - 20, traceSize)) {
				addToResult(result, s);
			}
		} else {
			for (StringLocated s : trace) {
				addToResult(result, s);
			}
		}
		return result;
	}

	private void addToResult(final List<String> result, StringLocated s) {
		String tmp = s.getString();
		if (tmp.length() > 120) {
			tmp = tmp.substring(0, 120) + " ...";
		}
		result.add(tmp);
	}

	private List<String> getTextError() {
		return Arrays.asList(" " + singleError.getError());
	}

	@Override
	final protected ImageData exportDiagramNow(OutputStream os, int num, FileFormatOption fileFormat, long seed)
			throws IOException {
		if (fileFormat.getFileFormat() == FileFormat.ATXT || fileFormat.getFileFormat() == FileFormat.UTXT) {
			final UGraphicTxt ugt = new UGraphicTxt();
			final UmlCharArea area = ugt.getCharArea();
			area.drawStringsLRSimple(getPureAsciiFormatted(), 0, 0);
			area.print(SecurityUtils.createPrintStream(os));
			return new ImageDataSimple(1, 1);

		}
		final TextBlockBackcolored result = getGraphicalFormatted();

		TextBlock udrawable;
		HColor backcolor = result.getBackcolor();
		final ImageParameter imageParameter = new ImageParameter(new ColorMapperIdentity(), false, null, 1.0,
				getMetadata(), null, ClockwiseTopRightBottomLeft.none(), backcolor);
		final ImageBuilder imageBuilder = ImageBuilder.build(imageParameter);
		imageBuilder.setRandomPixel(true);
		if (getSource().getTotalLineCountLessThan5()) {
			udrawable = addWelcome(result);
		} else {
			udrawable = result;
		}
		final int min = (int) (System.currentTimeMillis() / 60000L) % 60;
		// udrawable = addMessageAdopt(udrawable);
		if (min == 1 || min == 8 || min == 13 || min == 55) {
			udrawable = addMessagePatreon(udrawable);
		} else if (min == 15) {
			udrawable = addMessageLiberapay(udrawable);
		} else if (min == 30 || min == 39 || min == 48) {
			udrawable = addMessageDedication(udrawable);
		} else if (getSource().containsIgnoreCase("arecibo")) {
			udrawable = addMessageArecibo(udrawable);
		}
		imageBuilder.setUDrawable(udrawable);
		final ImageData imageData = imageBuilder.writeImageTOBEMOVED(fileFormat, seed(), os);
		((ImageDataAbstract) imageData).setStatus(FileImageData.ERROR);
		return imageData;
	}

	private void append(List<String> result, LineLocation lineLocation) {
		if (lineLocation.getDescription() != null) {
			result.add("[From " + lineLocation.getDescription() + " (line " + (lineLocation.getPosition() + 1) + ") ]");
		}
	}

	final public DiagramDescription getDescription() {
		return new DiagramDescription("(Error)");
	}

	private List<String> allButLast(List<String> full) {
		return full.subList(0, full.size() - 1);
	}

	private List<String> onlyLast(List<String> full) {
		return full.subList(full.size() - 1, full.size());
	}

	private TextBlockBackcolored getWelcome() throws IOException {
		return new PSystemWelcome(GraphicPosition.BACKGROUND_CORNER_TOP_RIGHT).getGraphicStrings();
	}

	private TextBlock addWelcome(final TextBlockBackcolored result) throws IOException {
		final TextBlockBackcolored welcome = getWelcome();
		return TextBlockUtils.mergeTB(welcome, result, HorizontalAlignment.LEFT);
	}

	private TextBlock addMessageLiberapay(final TextBlock source) throws IOException {
		if (LicenseInfo.retrieveNamedOrDistributorQuickIsValid()) {
			return source;
		}
		final TextBlock message = getMessageLiberapay();
		TextBlock result = TextBlockUtils.mergeTB(message, source, HorizontalAlignment.LEFT);
		result = TextBlockUtils.mergeTB(result, message, HorizontalAlignment.LEFT);
		return result;
	}

	private TextBlock addMessagePatreon(final TextBlock source) throws IOException {
		if (LicenseInfo.retrieveNamedOrDistributorQuickIsValid()) {
			return source;
		}
		final TextBlock message = getMessagePatreon();
		TextBlock result = TextBlockUtils.mergeTB(message, source, HorizontalAlignment.LEFT);
		result = TextBlockUtils.mergeTB(result, message, HorizontalAlignment.LEFT);
		return result;
	}

	private TextBlock addMessageDedication(final TextBlock source) throws IOException {
		if (LicenseInfo.retrieveNamedOrDistributorQuickIsValid()) {
			return source;
		}
		final TextBlock message = getMessageDedication();
		TextBlock result = TextBlockUtils.mergeTB(message, source, HorizontalAlignment.LEFT);
		return result;
	}

	private TextBlock addMessageAdopt(final TextBlock source) throws IOException {
		if (LicenseInfo.retrieveNamedOrDistributorQuickIsValid()) {
			return source;
		}
		final TextBlock message = getMessageAdopt();
		TextBlock result = TextBlockUtils.mergeTB(message, source, HorizontalAlignment.LEFT);
		return result;
	}

	private TextBlock addMessageArecibo(final TextBlock source) throws IOException {
		final UImage message = new UImage(
				new PixelImage(PSystemVersion.getArecibo(), AffineTransformType.TYPE_BILINEAR));
		TextBlock result = TextBlockUtils.mergeLR(source, TextBlockUtils.fromUImage(message), VerticalAlignment.TOP);
		return result;
	}

	private TextBlockBackcolored getMessageDedication() {
		final FlashCodeUtils utils = FlashCodeFactory.getFlashCodeUtils();
		final HColorSimple backColor = (HColorSimple) HColorSet.instance().getColorOrWhite("#eae2c9");

		final BufferedImage qrcode = smaller(
				utils.exportFlashcode("http://plantuml.com/dedication", Color.BLACK, backColor.getColor999()));
		final Display disp = Display.create("<b>Add your own dedication into PlantUML", " ", "For just $5 per month!",
				"Details on <i>[[http://plantuml.com/dedication]]");

		final UFont font = UFont.sansSerif(14);
		final FontConfiguration fc = new FontConfiguration(font, HColorUtils.BLACK, HColorUtils.BLACK, false);
		final TextBlock text = TextBlockUtils
				.withMargin(disp.create(fc, HorizontalAlignment.LEFT, new SpriteContainerEmpty()), 10, 0);
		final TextBlock result;
		if (qrcode == null) {
			result = text;
		} else {
			final UImage qr = new UImage(new PixelImage(qrcode, AffineTransformType.TYPE_NEAREST_NEIGHBOR)).scale(3);
			result = TextBlockUtils.mergeLR(text, TextBlockUtils.fromUImage(qr), VerticalAlignment.CENTER);
		}
		return TextBlockUtils.addBackcolor(result, backColor);

	}

	private TextBlockBackcolored getMessageAdopt() {
		final HColorSimple backColor = (HColorSimple) HColorSet.instance().getColorOrWhite("#eff4d2");

		final Display disp = Display.create("<b>Adopt-a-Word and put your message here!", " ",
				"Details on <i>[[http://plantuml.com/adopt]]", " ");

		final UFont font = UFont.sansSerif(14);
		final FontConfiguration fc = new FontConfiguration(font, HColorUtils.BLACK, HColorUtils.BLACK, false);
		final TextBlock text = TextBlockUtils
				.withMargin(disp.create(fc, HorizontalAlignment.LEFT, new SpriteContainerEmpty()), 10, 0);
		final TextBlock result;
		result = text;
		return TextBlockUtils.addBackcolor(result, backColor);

	}

	private TextBlockBackcolored getMessagePatreon() {
		final UImage message = new UImage(
				new PixelImage(PSystemVersion.getTime01(), AffineTransformType.TYPE_BILINEAR));
		final Color back = new Color(message.getImage(1).getRGB(0, 0));
		final HColor backColor = new HColorSimple(back, false);

		final FlashCodeUtils utils = FlashCodeFactory.getFlashCodeUtils();
		final BufferedImage qrcode = smaller(
				utils.exportFlashcode("http://plantuml.com/patreon", Color.BLACK, Color.WHITE));

		final int scale = 2;

		final double imWidth = message.getWidth() + (qrcode == null ? 0 : qrcode.getWidth() * scale + 20);
		final double imHeight = qrcode == null ? message.getHeight()
				: Math.max(message.getHeight(), qrcode.getHeight() * scale + 10);
		return new TextBlockBackcolored() {

			public void drawU(UGraphic ug) {
				if (qrcode == null) {
					ug.apply(new UTranslate(1, 1)).draw(message);
				} else {
					final UImage qr = new UImage(new PixelImage(qrcode, AffineTransformType.TYPE_NEAREST_NEIGHBOR))
							.scale(scale);
					ug.apply(new UTranslate(1, (imHeight - message.getHeight()) / 2)).draw(message);
					ug.apply(new UTranslate(1 + message.getWidth(), (imHeight - qr.getHeight()) / 2)).draw(qr);
				}
			}

			public Rectangle2D getInnerPosition(String member, StringBounder stringBounder, InnerStrategy strategy) {
				return null;
			}

			public Dimension2D calculateDimension(StringBounder stringBounder) {
				return new Dimension2DDouble(imWidth + 1, imHeight + 1);
			}

			public MinMax getMinMax(StringBounder stringBounder) {
				return MinMax.fromMax(imWidth + 1, imHeight + 1);
			}

			public HColor getBackcolor() {
				return backColor;
			}
		};

	}

	private TextBlockBackcolored getMessageLiberapay() {
		final UImage message = new UImage(
				new PixelImage(PSystemVersion.getTime15(), AffineTransformType.TYPE_BILINEAR));
		final Color back = new Color(message.getImage(1).getRGB(0, 0));
		final HColor backColor = new HColorSimple(back, false);

		final FlashCodeUtils utils = FlashCodeFactory.getFlashCodeUtils();
		final BufferedImage qrcode = smaller(utils.exportFlashcode("http://plantuml.com/lp", Color.BLACK, Color.WHITE));

		final int scale = 2;

		final double imWidth = message.getWidth() + (qrcode == null ? 0 : qrcode.getWidth() * scale + 20);
		final double imHeight = qrcode == null ? message.getHeight()
				: Math.max(message.getHeight(), qrcode.getHeight() * scale + 10);
		return new TextBlockBackcolored() {

			public void drawU(UGraphic ug) {
				if (qrcode == null) {
					ug.apply(new UTranslate(1, 1)).draw(message);
				} else {
					final UImage qr = new UImage(new PixelImage(qrcode, AffineTransformType.TYPE_NEAREST_NEIGHBOR))
							.scale(scale);
					ug.apply(new UTranslate(1, (imHeight - message.getHeight()) / 2)).draw(message);
					ug.apply(new UTranslate(1 + message.getWidth(), (imHeight - qr.getHeight()) / 2)).draw(qr);
				}
			}

			public Rectangle2D getInnerPosition(String member, StringBounder stringBounder, InnerStrategy strategy) {
				return null;
			}

			public Dimension2D calculateDimension(StringBounder stringBounder) {
				return new Dimension2DDouble(imWidth + 1, imHeight + 1);
			}

			public MinMax getMinMax(StringBounder stringBounder) {
				return MinMax.fromMax(imWidth + 1, imHeight + 1);
			}

			public HColor getBackcolor() {
				return backColor;
			}
		};

	}

	public int score() {
		final int result = trace.size() * 10 + singleError.score();
		return result;
	}

	private BufferedImage smaller(BufferedImage im) {
		if (im == null) {
			return null;
		}
		final int nb = 1;
		return im.getSubimage(nb, nb, im.getWidth() - 2 * nb, im.getHeight() - 2 * nb);
	}

}
