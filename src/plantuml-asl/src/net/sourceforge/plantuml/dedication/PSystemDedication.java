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
package net.sourceforge.plantuml.dedication;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Objects;

import javax.imageio.stream.ImageInputStream;

import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.PlainDiagram;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.security.SImageIO;
import net.sourceforge.plantuml.ugraphic.AffineTransformType;
import net.sourceforge.plantuml.ugraphic.PixelImage;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UImage;

public class PSystemDedication extends PlainDiagram {

	private final BufferedImage img;

	public PSystemDedication(UmlSource source, BufferedImage img) {
		super(source);
		this.img = Objects.requireNonNull(img);
	}

	@Override
	protected UDrawable getRootDrawable(FileFormatOption fileFormatOption) {
		// return ug -> ug.draw(new UImage(new PixelImage(img, AffineTransformType.TYPE_BILINEAR)));
		return new UDrawable() {
			public void drawU(UGraphic ug) {
				ug.draw(new UImage(new PixelImage(img, AffineTransformType.TYPE_BILINEAR)));
			}
		};
	}

	public static BufferedImage getBufferedImage(InputStream is) {
		try {
			final Class<?> clVP8Decoder = Class.forName("net.sourceforge.plantuml.webp.VP8Decoder");
			final Object vp8Decoder = clVP8Decoder.getDeclaredConstructor().newInstance();
			// final VP8Decoder vp8Decoder = new VP8Decoder();
			final Method decodeFrame = clVP8Decoder.getMethod("decodeFrame", ImageInputStream.class);
			final ImageInputStream iis = SImageIO.createImageInputStream(is);
			decodeFrame.invoke(vp8Decoder, iis);
			// vp8Decoder.decodeFrame(iis);
			iis.close();
			final Object frame = clVP8Decoder.getMethod("getFrame").invoke(vp8Decoder);
			return (BufferedImage) frame.getClass().getMethod("getBufferedImage").invoke(frame);
			// final VP8Frame frame = vp8Decoder.getFrame();
			// return frame.getBufferedImage();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public DiagramDescription getDescription() {
		return new DiagramDescription("(Dedication)");
	}

}
