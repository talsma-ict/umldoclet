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
package net.sourceforge.plantuml.creole;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.FileSystem;
import net.sourceforge.plantuml.FileUtils;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.code.Base64Coder;
import net.sourceforge.plantuml.flashcode.FlashCodeFactory;
import net.sourceforge.plantuml.flashcode.FlashCodeUtils;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.ImgValign;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TileImageSvg;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UImage;

public class AtomImg extends AbstractAtom implements Atom {

	private static final String DATA_IMAGE_PNG_BASE64 = "data:image/png;base64,";
	private final BufferedImage image;
	private final double scale;
	private final Url url;

	private AtomImg(BufferedImage image, double scale, Url url) {
		this.image = image;
		this.scale = scale;
		this.url = url;
	}

	public static Atom createQrcode(String flash, double scale) {
		final FlashCodeUtils utils = FlashCodeFactory.getFlashCodeUtils();
		BufferedImage im = utils.exportFlashcode(flash, Color.BLACK, Color.WHITE);
		if (im == null) {
			im = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
		}
		return new AtomImg(new UImage(im).scaleNearestNeighbor(scale).getImage(), 1, null);
	}

	public static Atom create(String src, ImgValign valign, int vspace, double scale, Url url) {
		final UFont font = UFont.monospaced(14);
		final FontConfiguration fc = FontConfiguration.blackBlueTrue(font);

		if (src.startsWith(DATA_IMAGE_PNG_BASE64)) {
			final String data = src.substring(DATA_IMAGE_PNG_BASE64.length(), src.length());
			try {
				final byte bytes[] = Base64Coder.decode(data);
				return build(src, fc, bytes, scale, url);
			} catch (Exception e) {
				return AtomText.create("ERROR " + e.toString(), fc);
			}

		}
		try {
			// Check if valid URL
			if (src.startsWith("http:") || src.startsWith("https:")) {
				// final byte image[] = getFile(src);
				return build(src, fc, new URL(src), scale, url);
			}
			final File f = FileSystem.getInstance().getFile(src);
			if (f.exists() == false) {
				return AtomText.create("(File not found: " + f.getCanonicalPath() + ")", fc);
			}
			if (f.getName().endsWith(".svg")) {
				return new AtomImgSvg(new TileImageSvg(f));
			}
			final BufferedImage read = FileUtils.ImageIO_read(f);
			if (read == null) {
				return AtomText.create("(Cannot decode: " + f.getCanonicalPath() + ")", fc);
			}
			return new AtomImg(FileUtils.ImageIO_read(f), scale, url);
		} catch (IOException e) {
			return AtomText.create("ERROR " + e.toString(), fc);
		}
	}

	private static Atom build(String source, final FontConfiguration fc, final byte[] data, double scale, Url url)
			throws IOException {
		final BufferedImage read = ImageIO.read(new ByteArrayInputStream(data));
		if (read == null) {
			return AtomText.create("(Cannot decode: " + source + ")", fc);
		}
		return new AtomImg(read, scale, url);
	}

	private static Atom build(String text, final FontConfiguration fc, URL source, double scale, Url url)
			throws IOException {
		final BufferedImage read = FileUtils.ImageIO_read(source);
		if (read == null) {
			return AtomText.create("(Cannot decode: " + text + ")", fc);
		}
		return new AtomImg(read, scale, url);
	}

	// Added by Alain Corbiere
	private static byte[] getFile(String host) throws IOException {
		final ByteArrayOutputStream image = new ByteArrayOutputStream();
		InputStream input = null;
		try {
			final URL url = new URL(host);
			final URLConnection connection = url.openConnection();
			input = connection.getInputStream();
			final byte[] buffer = new byte[1024];
			int read;
			while ((read = input.read(buffer)) > 0) {
				image.write(buffer, 0, read);
			}
			image.close();
			return image.toByteArray();
		} finally {
			if (input != null) {
				input.close();
			}
		}
	}

	// End

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		return new Dimension2DDouble(image.getWidth() * scale, image.getHeight() * scale);
	}

	public double getStartingAltitude(StringBounder stringBounder) {
		return 0;
	}

	public void drawU(UGraphic ug) {
		if (url != null) {
			ug.startUrl(url);
		}
		ug.draw(new UImage(image).scale(scale));
		if (url != null) {
			ug.closeAction();
		}
	}

}
