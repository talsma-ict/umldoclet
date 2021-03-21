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
package net.sourceforge.plantuml;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import javax.script.ScriptException;

import net.sourceforge.plantuml.anim.Animation;
import net.sourceforge.plantuml.anim.AnimationDecoder;
import net.sourceforge.plantuml.api.ImageDataSimple;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.cucadiagram.DisplaySection;
import net.sourceforge.plantuml.cucadiagram.UnparsableGraphvizException;
import net.sourceforge.plantuml.flashcode.FlashCodeFactory;
import net.sourceforge.plantuml.flashcode.FlashCodeUtils;
import net.sourceforge.plantuml.fun.IconLoader;
import net.sourceforge.plantuml.graphic.GraphicPosition;
import net.sourceforge.plantuml.graphic.GraphicStrings;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.mjpeg.MJPEGGenerator;
import net.sourceforge.plantuml.pdf.PdfConverter;
import net.sourceforge.plantuml.security.ImageIO;
import net.sourceforge.plantuml.security.SFile;
import net.sourceforge.plantuml.security.SecurityUtils;
import net.sourceforge.plantuml.style.ClockwiseTopRightBottomLeft;
import net.sourceforge.plantuml.style.NoStyleAvailableException;
import net.sourceforge.plantuml.svek.EmptySvgException;
import net.sourceforge.plantuml.svek.GraphvizCrash;
import net.sourceforge.plantuml.svek.TextBlockBackcolored;
import net.sourceforge.plantuml.ugraphic.AffineTransformType;
import net.sourceforge.plantuml.ugraphic.ImageBuilder;
import net.sourceforge.plantuml.ugraphic.ImageParameter;
import net.sourceforge.plantuml.ugraphic.PixelImage;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UImage;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.ColorMapperIdentity;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;
import net.sourceforge.plantuml.version.Version;

public abstract class UmlDiagram extends TitledDiagram implements Diagram, Annotated, WithSprite {

	private boolean rotation;
	private boolean hideUnlinkedData;

	private int minwidth = Integer.MAX_VALUE;

	private Animation animation;

	public UmlDiagram(UmlDiagramType type) {
		super(type);
	}

	public UmlDiagram(UmlDiagramType type, ISkinSimple orig) {
		super(type, orig);
	}

	final public int getMinwidth() {
		return minwidth;
	}

	final public void setMinwidth(int minwidth) {
		this.minwidth = minwidth;
	}

	final public boolean isRotation() {
		return rotation;
	}

	final public void setRotation(boolean rotation) {
		this.rotation = rotation;
	}

	public final DisplaySection getFooterOrHeaderTeoz(FontParam param) {
		if (param == FontParam.FOOTER) {
			return getFooter();
		}
		if (param == FontParam.HEADER) {
			return getHeader();
		}
		throw new IllegalArgumentException();
	}

	final public void setAnimation(Iterable<CharSequence> animationData) {
		try {
			final AnimationDecoder animationDecoder = new AnimationDecoder(animationData);
			this.animation = Animation.create(animationDecoder.decode());
		} catch (ScriptException e) {
			e.printStackTrace();
		}

	}

	final public Animation getAnimation() {
		return animation;
	}

	public final boolean isHideUnlinkedData() {
		return hideUnlinkedData;
	}

	public final void setHideUnlinkedData(boolean hideUnlinkedData) {
		this.hideUnlinkedData = hideUnlinkedData;
	}

	@Override
	final protected ImageData exportDiagramNow(OutputStream os, int index, FileFormatOption fileFormatOption, long seed)
			throws IOException {

		final HColor hover = getSkinParam().hoverPathColor();
		if (fileFormatOption.getSvgLinkTarget() == null || fileFormatOption.getSvgLinkTarget().equals("_top")) {
			fileFormatOption = fileFormatOption.withSvgLinkTarget(getSkinParam().getSvgLinkTarget());
		}
		fileFormatOption = fileFormatOption.withPreserveAspectRatio(getSkinParam().getPreserveAspectRatio());
		fileFormatOption = fileFormatOption.withTikzFontDistortion(getSkinParam().getTikzFontDistortion());
		if (hover != null) {
			fileFormatOption = fileFormatOption.withHoverColor(getSkinParam().getColorMapper().toRGB(hover));
		}

		if (fileFormatOption.getFileFormat() == FileFormat.PDF) {
			return exportDiagramInternalPdf(os, index);
		}

		try {
			final ImageData imageData = exportDiagramInternal(os, index, fileFormatOption);
			this.lastInfo = new Dimension2DDouble(imageData.getWidth(), imageData.getHeight());
			return imageData;
		} catch (NoStyleAvailableException e) {
			// e.printStackTrace();
			exportDiagramError(os, e, fileFormatOption, seed, null);
		} catch (UnparsableGraphvizException e) {
			e.printStackTrace();
			exportDiagramError(os, e.getCause(), fileFormatOption, seed, e.getGraphvizVersion());
		} catch (Throwable e) {
			//e.printStackTrace();
			exportDiagramError(os, e, fileFormatOption, seed, null);
		}
		return ImageDataSimple.error();
	}

	private void exportDiagramError(OutputStream os, Throwable exception, FileFormatOption fileFormat, long seed,
			String graphvizVersion) throws IOException {
		exportDiagramError(os, exception, fileFormat, seed, getMetadata(), getFlashData(),
				getFailureText1(exception, graphvizVersion, getFlashData()));
	}

	public static void exportDiagramError(OutputStream os, Throwable exception, FileFormatOption fileFormat, long seed,
			String metadata, String flash, List<String> strings) throws IOException {

		if (fileFormat.getFileFormat() == FileFormat.ATXT || fileFormat.getFileFormat() == FileFormat.UTXT) {
			exportDiagramErrorText(os, exception, strings);
			return;
		}

		strings.addAll(CommandExecutionResult.getStackTrace(exception));
		HColor backcolor = HColorUtils.WHITE;
		final ImageParameter imageParameter = new ImageParameter(new ColorMapperIdentity(), false, null, 1.0, metadata,
				null, ClockwiseTopRightBottomLeft.none(), backcolor);

		final ImageBuilder imageBuilder = ImageBuilder.build(imageParameter);

		BufferedImage im2 = null;
		if (flash != null) {
			final FlashCodeUtils utils = FlashCodeFactory.getFlashCodeUtils();
			try {
				im2 = utils.exportFlashcode(flash, Color.BLACK, Color.WHITE);
			} catch (Throwable e) {
				Log.error("Issue in flashcode generation " + e);
				// e.printStackTrace();
			}
			if (im2 != null) {
				GraphvizCrash.addDecodeHint(strings);
			}
		}
		final BufferedImage im = im2;
		final TextBlockBackcolored graphicStrings = GraphicStrings.createBlackOnWhite(strings, IconLoader.getRandom(),
				GraphicPosition.BACKGROUND_CORNER_TOP_RIGHT);

		if (im == null) {
			imageBuilder.setUDrawable(graphicStrings);
		} else {
			imageBuilder.setUDrawable(new UDrawable() {
				public void drawU(UGraphic ug) {
					graphicStrings.drawU(ug);
					final double height = graphicStrings.calculateDimension(ug.getStringBounder()).getHeight();
					ug = ug.apply(UTranslate.dy(height));
					ug.draw(new UImage(new PixelImage(im, AffineTransformType.TYPE_NEAREST_NEIGHBOR)).scale(3));
				}
			});
		}
		imageBuilder.writeImageTOBEMOVED(fileFormat, seed, os);
	}

	private static void exportDiagramErrorText(OutputStream os, Throwable exception, List<String> strings) {
		final PrintWriter pw = SecurityUtils.createPrintWriter(os);
		exception.printStackTrace(pw);
		pw.println();
		pw.println();
		for (String s : strings) {
			s = s.replaceAll("\\</?\\w+?\\>", "");
			pw.println(s);
		}
		pw.flush();
	}

	public String getFlashData() {
		final UmlSource source = getSource();
		if (source == null) {
			return "";
		}
		return source.getPlainString();
	}

	static private List<String> getFailureText1(Throwable exception, String graphvizVersion, String textDiagram) {
		final List<String> strings = GraphvizCrash.anErrorHasOccured(exception, textDiagram);
		strings.add("PlantUML (" + Version.versionString() + ") cannot parse result from dot/GraphViz.");
		if (exception instanceof EmptySvgException) {
			strings.add("Because dot/GraphViz returns an empty string.");
		}
		GraphvizCrash.checkOldVersionWarning(strings);
		if (graphvizVersion != null) {
			strings.add(" ");
			strings.add("GraphViz version used : " + graphvizVersion);
		}
		GraphvizCrash.pleaseGoTo(strings);
		GraphvizCrash.addProperties(strings);
		strings.add(" ");
		GraphvizCrash.thisMayBeCaused(strings);
		strings.add(" ");
		GraphvizCrash.youShouldSendThisDiagram(strings);
		strings.add(" ");
		return strings;
	}

	public static List<String> getFailureText2(Throwable exception, String textDiagram) {
		final List<String> strings = GraphvizCrash.anErrorHasOccured(exception, textDiagram);
		strings.add("PlantUML (" + Version.versionString() + ") has crashed.");
		GraphvizCrash.checkOldVersionWarning(strings);
		strings.add(" ");
		GraphvizCrash.youShouldSendThisDiagram(strings);
		strings.add(" ");
		return strings;
	}

	private void exportDiagramInternalMjpeg(OutputStream os) throws IOException {
		final SFile f = new SFile("c:/test.avi");
		final int nb = 150;
		final double framerate = 30;
		final MJPEGGenerator m = new MJPEGGenerator(f, 640, 480, framerate, nb);

		for (int i = 0; i < nb; i++) {
			final AffineTransform at = new AffineTransform();
			final double coef = (nb - 1 - i) * 1.0 / nb;
			at.setToShear(coef, coef);
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			// exportDiagramTOxxBEREMOVED(baos, null, 0, new
			// FileFormatOption(FileFormat.PNG, at));
			baos.close();
			final BufferedImage im = ImageIO.read(new ByteArrayInputStream(baos.toByteArray()));
			m.addImage(im);
		}
		m.finishAVI();

	}

	private Dimension2D lastInfo;

	private ImageData exportDiagramInternalPdf(OutputStream os, int index) throws IOException {
		final File svg = FileUtils.createTempFileLegacy("pdf", ".svf");
		final File pdfFile = FileUtils.createTempFileLegacy("pdf", ".pdf");
		final OutputStream fos = new BufferedOutputStream(new FileOutputStream(svg));
		final ImageData result = exportDiagram(fos, index, new FileFormatOption(FileFormat.SVG));
		fos.close();
		PdfConverter.convert(svg, pdfFile);
		FileUtils.copyToStream(pdfFile, os);
		return result;
	}

	protected abstract ImageData exportDiagramInternal(OutputStream os, int index, FileFormatOption fileFormatOption)
			throws IOException;

	final protected void exportCmap(SuggestedFile suggestedFile, int index, final ImageData cmapdata)
			throws FileNotFoundException {
		final String name = changeName(suggestedFile.getFile(index).getAbsolutePath());
		final SFile cmapFile = new SFile(name);
		PrintWriter pw = null;
		try {
			if (PSystemUtils.canFileBeWritten(cmapFile) == false) {
				return;
			}
			pw = cmapFile.createPrintWriter();
			pw.print(cmapdata.getCMapData(cmapFile.getName().substring(0, cmapFile.getName().length() - 6)));
			pw.close();
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	static String changeName(String name) {
		return name.replaceAll("(?i)\\.\\w{3}$", ".cmapx");
	}

	@Override
	public String getWarningOrError() {
		if (lastInfo == null) {
			return null;
		}
		final double actualWidth = lastInfo.getWidth();
		if (actualWidth == 0) {
			return null;
		}
		final String value = getSkinParam().getValue("widthwarning");
		if (value == null) {
			return null;
		}
		if (value.matches("\\d+") == false) {
			return null;
		}
		final int widthwarning = Integer.parseInt(value);
		if (actualWidth > widthwarning) {
			return "The image is " + ((int) actualWidth) + " pixel width. (Warning limit is " + widthwarning + ")";
		}
		return null;
	}

	public void setHideEmptyDescription(boolean hideEmptyDescription) {
	}

}
