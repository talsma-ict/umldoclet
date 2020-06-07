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
package net.sourceforge.plantuml.creole.atom;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.FileSystem;
import net.sourceforge.plantuml.FileUtils;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.code.Base64Coder;
import net.sourceforge.plantuml.creole.legacy.AtomText;
import net.sourceforge.plantuml.flashcode.FlashCodeFactory;
import net.sourceforge.plantuml.flashcode.FlashCodeUtils;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.ImgValign;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TileImageSvg;
import net.sourceforge.plantuml.security.ImageIO;
import net.sourceforge.plantuml.security.SFile;
import net.sourceforge.plantuml.security.SURL;
import net.sourceforge.plantuml.security.SecurityProfile;
import net.sourceforge.plantuml.security.SecurityUtils;
import net.sourceforge.plantuml.ugraphic.AffineTransformType;
import net.sourceforge.plantuml.ugraphic.PixelImage;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UImage;

public class AtomImg extends AbstractAtom implements Atom {

	private static final String DATA_IMAGE_PNG_BASE64 = "data:image/png;base64,";
	private final BufferedImage image;
	private final double scale;
	private final Url url;
	private final String rawFileName;

	private AtomImg(BufferedImage image, double scale, Url url, String rawFileName) {
		this.image = image;
		this.scale = scale;
		this.url = url;
		this.rawFileName = rawFileName;
	}

	public static Atom createQrcode(String flash, double scale) {
		final FlashCodeUtils utils = FlashCodeFactory.getFlashCodeUtils();
		BufferedImage im = utils.exportFlashcode(flash, Color.BLACK, Color.WHITE);
		if (im == null) {
			im = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
		}
		return new AtomImg(new UImage(new PixelImage(im, AffineTransformType.TYPE_NEAREST_NEIGHBOR))
				.scale(scale).getImage(1), 1, null, null);
	}

	public static Atom create(String src, ImgValign valign, int vspace, double scale, Url url) {
		final UFont font = UFont.monospaced(14);
		final FontConfiguration fc = FontConfiguration.blackBlueTrue(font);

		if (src.startsWith(DATA_IMAGE_PNG_BASE64)) {
			final String data = src.substring(DATA_IMAGE_PNG_BASE64.length(), src.length());
			try {
				final byte bytes[] = Base64Coder.decode(data);
				return buildRasterFromData(src, fc, bytes, scale, url);
			} catch (Exception e) {
				return AtomText.create("ERROR " + e.toString(), fc);
			}

		}
		try {
			// Check if valid URL
			if (src.startsWith("http:") || src.startsWith("https:")) {
				if (src.endsWith(".svg")) {
					return buildSvgFromUrl(src, fc, SURL.create(src), scale, url);
				}
				return buildRasterFromUrl(src, fc, SURL.create(src), scale, url);
			}
			final SFile f = FileSystem.getInstance().getFile(src);
			if (f.exists() == false) {
				if (SecurityUtils.getSecurityProfile() == SecurityProfile.UNSECURE) {
					return AtomText.create("(File not found: " + f.getPrintablePath() + ")", fc);
				}
				return AtomText.create("(Cannot decode)", fc);
			}
			if (f.getName().endsWith(".svg")) {
				final String tmp = FileUtils.readSvg(f);
				if (tmp == null) {
					return AtomText.create("(Cannot decode)", fc);
				}
				return new AtomImgSvg(new TileImageSvg(tmp));
			}
			final BufferedImage read = f.readRasterImageFromFile();
			if (read == null) {
				if (SecurityUtils.getSecurityProfile() == SecurityProfile.UNSECURE) {
					return AtomText.create("(Cannot decode: " + f.getPrintablePath() + ")", fc);
				}
				return AtomText.create("(Cannot decode)", fc);
			}
			return new AtomImg(f.readRasterImageFromFile(), scale, url, src);
		} catch (IOException e) {
			e.printStackTrace();
			if (SecurityUtils.getSecurityProfile() == SecurityProfile.UNSECURE) {
				return AtomText.create("ERROR " + e.toString(), fc);
			}
			return AtomText.create("ERROR", fc);
		}
	}

	private static Atom buildRasterFromData(String source, final FontConfiguration fc, final byte[] data, double scale,
			Url url) throws IOException {
		final BufferedImage read = ImageIO.read(new ByteArrayInputStream(data));
		if (read == null) {
			return AtomText.create("(Cannot decode: " + source + ")", fc);
		}
		return new AtomImg(read, scale, url, null);
	}

	private static Atom buildRasterFromUrl(String text, final FontConfiguration fc, SURL source, double scale, Url url)
			throws IOException {
		if (source == null) {
			return AtomText.create("(Cannot decode: " + text + ")", fc);
		}
		final BufferedImage read = source.readRasterImageFromURL();
		if (read == null) {
			return AtomText.create("(Cannot decode: " + text + ")", fc);
		}
		return new AtomImg(read, scale, url, "http");
	}

	private static Atom buildSvgFromUrl(String text, final FontConfiguration fc, SURL source, double scale, Url url)
			throws IOException {
		if (source == null) {
			return AtomText.create("(Cannot decode SVG: " + text + ")", fc);
		}
		final byte[] read = getFile(source);
		if (read == null) {
			return AtomText.create("(Cannot decode SVG: " + text + ")", fc);
		}
		return new AtomImgSvg(new TileImageSvg(new String(read, "UTF-8")));
	}

	// Added by Alain Corbiere
	private static byte[] getFile(SURL url) {
		try {
			InputStream input = null;
			try {
				final URLConnection connection = url.openConnection();
				if (connection == null) {
					return null;
				}
				input = connection.getInputStream();
				final ByteArrayOutputStream image = new ByteArrayOutputStream();
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
		} catch (Exception e) {
			e.printStackTrace();
			return null;
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
		ug.draw(new UImage(new PixelImage(image, AffineTransformType.TYPE_BILINEAR))
				.withRawFileName(rawFileName).scale(scale));
		if (url != null) {
			ug.closeUrl();
		}
	}

}
