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
package net.sourceforge.plantuml;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.creole.Neutron;
import net.sourceforge.plantuml.creole.atom.Atom;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.Line;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.log.Logme;
import net.sourceforge.plantuml.preproc.Defines;
import net.sourceforge.plantuml.security.SImageIO;
import net.sourceforge.plantuml.ugraphic.AffineTransformType;
import net.sourceforge.plantuml.ugraphic.PixelImage;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UImage;
import net.sourceforge.plantuml.ugraphic.UImageSvg;
import net.sourceforge.plantuml.ugraphic.UShape;

public class EmbeddedDiagram extends AbstractTextBlock implements Line, Atom {

	public static final String EMBEDDED_START = "{{";
	public static final String EMBEDDED_END = "}}";

	public static String getEmbeddedType(CharSequence cs) {
		if (cs == null)
			return null;

		final String s = StringUtils.trin(cs.toString());
		if (s.startsWith(EMBEDDED_START) == false)
			return null;

		if (s.equals(EMBEDDED_START))
			return "uml";

		if (s.equals(EMBEDDED_START))
			return "uml";

		if (s.equals(EMBEDDED_START + "uml"))
			return "uml";

		if (s.equals(EMBEDDED_START + "wbs"))
			return "wbs";

		if (s.equals(EMBEDDED_START + "mindmap"))
			return "mindmap";

		if (s.equals(EMBEDDED_START + "gantt"))
			return "gantt";

		if (s.equals(EMBEDDED_START + "json"))
			return "json";

		if (s.equals(EMBEDDED_START + "yaml"))
			return "yaml";

		if (s.equals(EMBEDDED_START + "wire"))
			return "wire";

		if (s.equals(EMBEDDED_START + "creole"))
			return "creole";

		if (s.equals(EMBEDDED_START + "board"))
			return "board";

		if (s.equals(EMBEDDED_START + "ebnf"))
			return "ebnf";

		return null;
	}

	public static EmbeddedDiagram createAndSkip(String type, Iterator<CharSequence> it, ISkinSimple skinParam) {
		final List<String> result = new ArrayList<String>();
		result.add("@start" + type);
		int nested = 1;
		while (it.hasNext()) {
			final CharSequence s2 = it.next();
			if (EmbeddedDiagram.getEmbeddedType(StringUtils.trinNoTrace(s2)) != null)
				// if (StringUtils.trinNoTrace(s2).startsWith(EmbeddedDiagram.EMBEDDED_START))
				nested++;
			else if (StringUtils.trinNoTrace(s2).equals(EmbeddedDiagram.EMBEDDED_END)) {
				nested--;
				if (nested == 0)
					break;
			}
			result.add(s2.toString());
		}
		result.add("@end" + type);
		return EmbeddedDiagram.from(skinParam, result);

	}

	private final List<StringLocated> list;
	private final ISkinSimple skinParam;
	private BufferedImage image;

	private EmbeddedDiagram(ISkinSimple skinParam, List<StringLocated> system) {
		this.list = system;
		this.skinParam = skinParam;
	}

	public static EmbeddedDiagram from(ISkinSimple skinParam, List<String> strings) {
		return new EmbeddedDiagram(skinParam, BlockUml.convert(strings));
	}

	public double getStartingAltitude(StringBounder stringBounder) {
		return 0;
	}

	public XDimension2D calculateDimension(StringBounder stringBounder) {
		try {
			final BufferedImage im = getImage();
			return new XDimension2D(im.getWidth(), im.getHeight());
		} catch (IOException e) {
			Logme.error(e);
		} catch (InterruptedException e) {
			Logme.error(e);
		}
		return new XDimension2D(42, 42);
	}

	public void drawU(UGraphic ug) {
		try {
			final boolean isSvg = ug.matchesProperty("SVG");
			if (isSvg) {
				final String imageSvg = getImageSvg();
				final UImageSvg svg = new UImageSvg(imageSvg, 1);
				ug.draw(svg);
				return;
			}
			final BufferedImage im = getImage();
			final UShape image = new UImage(new PixelImage(im, AffineTransformType.TYPE_BILINEAR));
			ug.draw(image);
		} catch (IOException e) {
			Logme.error(e);
		} catch (InterruptedException e) {
			Logme.error(e);
		}

	}

	private String getImageSvg() throws IOException, InterruptedException {
		final Diagram system = getSystem();
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		system.exportDiagram(os, 0, new FileFormatOption(FileFormat.SVG));
		os.close();
		return new String(os.toByteArray());
	}

	private BufferedImage getImage() throws IOException, InterruptedException {
		if (image == null)
			image = getImageSlow();

		return image;
	}

	private BufferedImage getImageSlow() throws IOException, InterruptedException {
		final Diagram system = getSystem();
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		system.exportDiagram(os, 0, new FileFormatOption(FileFormat.PNG));
		os.close();
		return SImageIO.read(os.toByteArray());
	}

	public HorizontalAlignment getHorizontalAlignment() {
		return HorizontalAlignment.LEFT;
	}

	private Diagram getSystem() throws IOException, InterruptedException {
		final BlockUml blockUml = new BlockUml(list, Defines.createEmpty(), skinParam, null, null);
		return blockUml.getDiagram();

	}

	@Override
	public List<Neutron> getNeutrons() {
		return Arrays.asList(Neutron.create(this));
	}

}
