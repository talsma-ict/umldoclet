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
package net.sourceforge.plantuml.ugraphic;

import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import net.sourceforge.plantuml.EmptyImageBuilder;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SvgCharSizeHack;
import net.sourceforge.plantuml.eps.EpsStrategy;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.png.PngIO;
import net.sourceforge.plantuml.svg.LengthAdjust;
import net.sourceforge.plantuml.ugraphic.color.ColorMapper;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.eps.UGraphicEps;
import net.sourceforge.plantuml.ugraphic.g2d.UGraphicG2d;
import net.sourceforge.plantuml.ugraphic.svg.UGraphicSvg;

public abstract class UGraphicUtils {

	public static void writeImage(OutputStream os, String metadata, FileFormatOption fileFormatOption, long seed,
			ColorMapper colorMapper, HColor background, TextBlock image) throws IOException {
		final FileFormat fileFormat = fileFormatOption.getFileFormat();
		if (fileFormat == FileFormat.PNG) {
			final BufferedImage im = createImage(fileFormatOption.getWatermark(), colorMapper, background, image);
			PngIO.write(im, os, fileFormatOption.isWithMetadata() ? metadata : null, 96);
		} else if (fileFormat == FileFormat.SVG) {
			final Dimension2D size = computeSize(colorMapper, background, image);
			final UGraphicSvg svg = new UGraphicSvg(true, size, colorMapper, colorMapper.toRGB(background), false, 1.0,
					fileFormatOption.getSvgLinkTarget(), fileFormatOption.getHoverColor(), seed,
					fileFormatOption.getPreserveAspectRatio(), SvgCharSizeHack.NO_HACK,
					LengthAdjust.defaultValue());
			image.drawU(svg);
			svg.createXml(os, fileFormatOption.isWithMetadata() ? metadata : null);
		} else if (fileFormat == FileFormat.EPS) {
			final UGraphicEps ug = new UGraphicEps(colorMapper, EpsStrategy.getDefault2());
			image.drawU(ug);
			os.write(ug.getEPSCode().getBytes());
		} else if (fileFormat == FileFormat.EPS_TEXT) {
			final UGraphicEps ug = new UGraphicEps(colorMapper, EpsStrategy.WITH_MACRO_AND_TEXT);
			image.drawU(ug);
			os.write(ug.getEPSCode().getBytes());
		} else {
			throw new UnsupportedOperationException();
		}
	}

	private static BufferedImage createImage(String watermark, ColorMapper colorMapper, HColor background,
			TextBlock image) {
		final Dimension2D size = computeSize(colorMapper, background, image);

		final EmptyImageBuilder builder = new EmptyImageBuilder(watermark, size.getWidth(), size.getHeight(),
				colorMapper.toColor(background));
		final BufferedImage im = builder.getBufferedImage();
		final Graphics2D g2d = builder.getGraphics2D();

		final UGraphicG2d ug = new UGraphicG2d(colorMapper, g2d, 1.0);
		image.drawU(ug);
		g2d.dispose();
		return im;
	}

	private static Dimension2D computeSize(ColorMapper colorMapper, HColor background, TextBlock image) {
		final EmptyImageBuilder builder = new EmptyImageBuilder(null, 10, 10, colorMapper.toColor(background));
		final Graphics2D g2d = builder.getGraphics2D();

		final UGraphicG2d tmp = new UGraphicG2d(colorMapper, g2d, 1.0);
		final Dimension2D size = image.calculateDimension(tmp.getStringBounder());
		g2d.dispose();
		return size;
	}

}
