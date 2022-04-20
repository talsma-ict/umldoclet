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
package net.sourceforge.plantuml.security;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

public class SImageIO {

	public static ImageOutputStream createImageOutputStream(OutputStream os) throws IOException {
		return javax.imageio.ImageIO.createImageOutputStream(os);
	}

	public static void write(RenderedImage image, String format, OutputStream os) throws IOException {
		javax.imageio.ImageIO.write(image, format, os);
	}

	public static void write(RenderedImage image, String format, SFile file) throws IOException {
		javax.imageio.ImageIO.write(image, format, file.conv());
	}

	public static BufferedImage read(java.io.File file) throws IOException {
		return javax.imageio.ImageIO.read(file);
	}

	public static BufferedImage read(SFile file) throws IOException {
		return javax.imageio.ImageIO.read(file.conv());
	}

	public static BufferedImage read(InputStream is) throws IOException {
		return javax.imageio.ImageIO.read(is);
	}

	public static BufferedImage read(byte[] bytes) throws IOException {
		return javax.imageio.ImageIO.read(new ByteArrayInputStream(bytes));
	}

	public static ImageInputStream createImageInputStream(SFile file) throws IOException {
		return javax.imageio.ImageIO.createImageInputStream(file.conv());
	}

	public static ImageInputStream createImageInputStream(Object obj) throws IOException {
		if (obj instanceof SFile) {
			obj = ((SFile) obj).conv();
		}
		return javax.imageio.ImageIO.createImageInputStream(obj);
	}

	public static ImageInputStream createImageInputStream(InputStream is) throws IOException {
		return javax.imageio.ImageIO.createImageInputStream(is);
	}

	public static Iterator<ImageReader> getImageReaders(ImageInputStream iis) {
		return javax.imageio.ImageIO.getImageReaders(iis);
	}

	public static Iterator<ImageWriter> getImageWritersBySuffix(String string) {
		return javax.imageio.ImageIO.getImageWritersBySuffix(string);
	}

}
