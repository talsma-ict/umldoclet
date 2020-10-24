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
package net.sourceforge.plantuml.version;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.AbstractPSystem;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.OptionFlags;
import net.sourceforge.plantuml.OptionPrint;
import net.sourceforge.plantuml.Run;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.cucadiagram.dot.GraphvizUtils;
import net.sourceforge.plantuml.dedication.Dedication;
import net.sourceforge.plantuml.graphic.GraphicPosition;
import net.sourceforge.plantuml.graphic.GraphicStrings;
import net.sourceforge.plantuml.preproc.ImportedFiles;
import net.sourceforge.plantuml.preproc.Stdlib;
import net.sourceforge.plantuml.preproc2.PreprocessorUtils;
import net.sourceforge.plantuml.security.ImageIO;
import net.sourceforge.plantuml.security.SFile;
import net.sourceforge.plantuml.security.SecurityProfile;
import net.sourceforge.plantuml.security.SecurityUtils;
import net.sourceforge.plantuml.svek.GraphvizCrash;
import net.sourceforge.plantuml.svek.TextBlockBackcolored;
import net.sourceforge.plantuml.ugraphic.ImageBuilder;
import net.sourceforge.plantuml.ugraphic.color.ColorMapperIdentity;

public class PSystemVersion extends AbstractPSystem {

	private final List<String> strings = new ArrayList<String>();
	private BufferedImage image;

	PSystemVersion(boolean withImage, List<String> args) {
		this.strings.addAll(args);
		if (withImage) {
			this.image = getPlantumlImage();
		}
	}

	private PSystemVersion(List<String> args, BufferedImage image) {
		this.strings.addAll(args);
		this.image = image;
	}

	public static BufferedImage getPlantumlImage() {
		return getImage("logo.png");
	}

	public static BufferedImage getCharlieImage() {
		return getImage("charlie.png");
	}

	public static BufferedImage getTime01() {
		return getImage("time01.png");
	}

	public static BufferedImage getTime15() {
		return getImage("time15.png");
	}

	public static BufferedImage getPlantumlSmallIcon() {
		return getImage("favicon.png");
	}

	public static BufferedImage getArecibo() {
		return getImage("arecibo.png");
	}

	public static BufferedImage getDotc() {
		return getImage("dotc.png");
	}

	public static BufferedImage getApple2Image() {
		return getImageWebp("apple2.png");
	}

	private static BufferedImage getImage(final String name) {
		try {
			final InputStream is = PSystemVersion.class.getResourceAsStream(name);
			final BufferedImage image = ImageIO.read(is);
			is.close();
			return image;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
	}

	private static BufferedImage getImageWebp(final String name) {
		try {
			final InputStream is = PSystemVersion.class.getResourceAsStream(name);
			final BufferedImage image = Dedication.getBufferedImage(is);
			is.close();
			return image;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
	}

	private static BufferedImage transparentIcon;

	public static BufferedImage getPlantumlSmallIcon2() {
		if (transparentIcon != null) {
			return transparentIcon;
		}
		final BufferedImage ico = getPlantumlSmallIcon();
		if (ico == null) {
			return new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
		}
		transparentIcon = new BufferedImage(ico.getWidth(), ico.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
		for (int i = 0; i < ico.getWidth(); i++) {
			for (int j = 0; j < ico.getHeight(); j++) {
				final int col = ico.getRGB(i, j);
				if (col != ico.getRGB(0, 0)) {
					transparentIcon.setRGB(i, j, col);
				}
			}
		}
		return transparentIcon;
	}

	@Override
	final protected ImageData exportDiagramNow(OutputStream os, int num, FileFormatOption fileFormat, long seed)
			throws IOException {
		final TextBlockBackcolored result = GraphicStrings.createBlackOnWhite(strings, image,
				GraphicPosition.BACKGROUND_CORNER_BOTTOM_RIGHT);
		final ImageBuilder imageBuilder = ImageBuilder.buildA(new ColorMapperIdentity(), false, null, getMetadata(),
				null, 1.0, result.getBackcolor());
		imageBuilder.setUDrawable(result);
		return imageBuilder.writeImageTOBEMOVED(fileFormat, seed, os);
	}

	public static PSystemVersion createShowVersion() {
		final List<String> strings = new ArrayList<String>();
		strings.add("<b>PlantUML version " + Version.versionString() + "</b> (" + Version.compileTimeString() + ")");
		strings.add("(" + License.getCurrent() + " source distribution)");
		GraphvizCrash.checkOldVersionWarning(strings);
		if (OptionFlags.ALLOW_INCLUDE) {
			if (SecurityUtils.getSecurityProfile() == SecurityProfile.UNSECURE) {
				strings.add("Loaded from " + Version.getJarPath());
			}
			if (OptionFlags.getInstance().isWord()) {
				strings.add("Word Mode");
				strings.add("Command Line: " + Run.getCommandLine());
				strings.add("Current Dir: " + new SFile(".").getAbsolutePath());
				strings.add("plantuml.include.path: " + PreprocessorUtils.getenv("plantuml.include.path"));
			}
		}
		strings.add(" ");
		// strings.add("<b>Stdlib:");
		// Stdlib.addInfoVersion(strings, false);
		// strings.add(" ");

		GraphvizUtils.addDotStatus(strings, true);
		strings.add(" ");
		for (String name : OptionPrint.interestingProperties()) {
			strings.add(name);
		}
		for (String v : OptionPrint.interestingValues()) {
			strings.add(v);
		}

		return new PSystemVersion(true, strings);
	}

	public static PSystemVersion createStdLib() {
		final List<String> strings = new ArrayList<String>();
		Stdlib.addInfoVersion(strings, true);
		strings.add(" ");

		return new PSystemVersion(true, strings);
	}

	public static PSystemVersion createShowAuthors() {
		// Duplicate in OptionPrint
		final List<String> strings = getAuthorsStrings(true);
		return new PSystemVersion(true, strings);
	}

	public static List<String> getAuthorsStrings(boolean withTag) {
		final List<String> strings = new ArrayList<String>();
		add(strings, "<b>PlantUML version " + Version.versionString() + "</b> (" + Version.compileTimeString() + ")",
				withTag);
		add(strings, "(" + License.getCurrent() + " source distribution)", withTag);
		add(strings, " ", withTag);
		add(strings, "<u>Original idea</u>: Arnaud Roques", withTag);
		add(strings, "<u>Word Macro</u>: Alain Bertucat & Matthieu Sabatier", withTag);
		add(strings, "<u>Word Add-in</u>: Adriaan van den Brand", withTag);
		add(strings, "<u>J2V8 & viz.js integration</u>: Andreas Studer", withTag);
		add(strings, "<u>Official Eclipse Plugin</u>: Hallvard Tr\u00E6tteberg", withTag);
		add(strings, "<u>Original Eclipse Plugin</u>: Claude Durif & Anne Pecoil", withTag);
		add(strings, "<u>Servlet & XWiki</u>: Maxime Sinclair", withTag);
		add(strings, "<u>Docker</u>: David Ducatel", withTag);
		add(strings, "<u>AWS lib</u>: Chris Passarello", withTag);
		add(strings, "<u>Stdlib Icons</u>: tupadr3", withTag);
		add(strings, "<u>Site design</u>: Raphael Cotisson", withTag);
		add(strings, "<u>Logo</u>: Benjamin Croizet", withTag);

		add(strings, " ", withTag);
		add(strings, "http://plantuml.com", withTag);
		add(strings, " ", withTag);
		return strings;
	}

	private static void add(List<String> result, String s, boolean withTag) {
		if (withTag == false) {
			s = s.replaceAll("\\</?\\w+\\>", "");
		}
		result.add(s);

	}

	public static PSystemVersion createTestDot() throws IOException {
		final List<String> strings = new ArrayList<String>();
		strings.add(Version.fullDescription());
		GraphvizUtils.addDotStatus(strings, true);
		return new PSystemVersion(false, strings);
	}

	public static PSystemVersion createDumpStackTrace() throws IOException {
		final List<String> strings = new ArrayList<String>();
		final Throwable creationPoint = new Throwable();
		creationPoint.fillInStackTrace();
		for (StackTraceElement ste : creationPoint.getStackTrace()) {
			strings.add(ste.toString());
		}
		return new PSystemVersion(false, strings);
	}

	public static PSystemVersion createKeyDistributor() throws IOException {
		final LicenseInfo license = LicenseInfo.retrieveDistributor();
		BufferedImage im = null;
		final List<String> strings = new ArrayList<String>();
		if (license == null) {
			strings.add("No license found");
		} else {
			strings.add(license.getOwner());
			strings.add(license.getContext());
			strings.add(license.getGenerationDate().toString());
			strings.add(license.getExpirationDate().toString());
			im = LicenseInfo.retrieveDistributorImage(license);
		}
		return new PSystemVersion(strings, im);
	}

	public static PSystemVersion createPath() throws IOException {
		final List<String> strings = new ArrayList<String>();
		strings.add("<u>Current Dir</u>: " + new SFile(".").getPrintablePath());
		strings.add(" ");
		strings.add("<u>Default path</u>:");
		for (SFile f : ImportedFiles.createImportedFiles(null).getPath()) {
			strings.add(f.getPrintablePath());
		}
		return new PSystemVersion(true, strings);
	}

	public DiagramDescription getDescription() {
		return new DiagramDescription("(Version)");
	}

	public List<String> getLines() {
		return Collections.unmodifiableList(strings);
	}

}
