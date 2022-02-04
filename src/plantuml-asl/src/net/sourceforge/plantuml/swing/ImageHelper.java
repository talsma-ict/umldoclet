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
package net.sourceforge.plantuml.swing;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

/**
 * A collection of methods to help with processing images. A majority of this code was originally found online.
 */
public class ImageHelper {
	/**
	 * Returns a scaled instance of a {@code BufferedImage}.
	 * 
	 * Modified from: https://today.java.net/pub/a/today/2007/04/03/perils-of-image-getscaledinstance.html
	 * 
	 * @param img
	 *            the original image to be scaled
	 * @param targetDim
	 *            the desired dimensions of the scaled instance, in pixels
	 * @param hints
	 *            RenderingHints used when scaling the image
	 * @param higherQuality
	 *            if true, this method will use a multi-step scaling technique that provides higher quality than the
	 *            usual one-step technique (only useful in downscaling cases, targetDim is smaller than the original
	 *            dimensions, and generally only when the {@code BILINEAR} hint is specified)
	 * @return a scaled version of the original {@code BufferedImage}
	 */
	public static BufferedImage getScaledInstance(BufferedImage img, Dimension targetDim, RenderingHints hints,
			boolean higherQuality) {
		final int targetWidth = targetDim.width;
		final int targetHeight = targetDim.height;
		final int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB
				: BufferedImage.TYPE_INT_ARGB;
		BufferedImage ret = (BufferedImage) img;
		int w;
		int h;
		if (higherQuality) {
			// Use multi-step technique: start with original size, then
			// scale down in multiple passes with drawImage()
			// until the target size is reached
			w = img.getWidth();
			h = img.getHeight();
		} else {
			// Use one-step technique: scale directly from original
			// size to target size with a single drawImage() call
			w = targetWidth;
			h = targetHeight;
		}

		do {
			if (higherQuality && w > targetWidth) {
				w /= 2;
				if (w < targetWidth) {
					w = targetWidth;
				}
			}

			if (higherQuality && h > targetHeight) {
				h /= 2;
				if (h < targetHeight) {
					h = targetHeight;
				}
			}

			final BufferedImage tmp = new BufferedImage(w, h, type);
			final Graphics2D g2 = tmp.createGraphics();
			g2.setRenderingHints(hints);
			g2.drawImage(ret, 0, 0, w, h, null);
			g2.dispose();

			ret = tmp;
		} while (w != targetWidth || h != targetHeight);

		return ret;
	}

	/**
	 * Converts an Image to a BufferedImage.
	 * 
	 * From: http://stackoverflow.com/questions/13605248/java-converting-image-to-bufferedimage
	 */
	public static BufferedImage toBufferedImage(Image img) {
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}
		// Create a buffered image with transparency
		final BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null),
				BufferedImage.TYPE_INT_ARGB);

		// Draw the image on to the buffered image
		final Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();

		return bimage;
	}

	/**
	 * Calculates the dimensions of a scaled image given the dimensions of an image and the area it is to be drawn in
	 * while preserving aspect ratio.
	 * 
	 * From: http://stackoverflow.com/questions/10245220/java-image-resize-maintain-aspect-ratio
	 * 
	 * @param imgSize
	 *            dimensions of the original image.
	 * @param boundary
	 *            dimensions of the area the image is to be drawn in.
	 */
	public static Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {
		final int originalWidth = imgSize.width;
		final int originaHeight = imgSize.height;
		final int boundWidth = boundary.width;
		final int boundHeight = boundary.height;
		int newWidth = originalWidth;
		int newHeight = originaHeight;

		// first check if we need to scale width
		if (originalWidth > boundWidth) {
			// scale width to fit
			newWidth = boundWidth;
			// scale height to maintain aspect ratio
			newHeight = (newWidth * originaHeight) / originalWidth;
		}

		// then check if we need to scale even with the new height
		if (newHeight > boundHeight) {
			// scale height to fit instead
			newHeight = boundHeight;
			// scale width to maintain aspect ratio
			newWidth = (newHeight * originalWidth) / originaHeight;
		}

		return new Dimension(newWidth, newHeight);
	}

	public static Dimension getScaledDimensionWidthFit(Dimension imgSize, Dimension boundary) {
		final int originalWidth = imgSize.width;
		final int originaHeight = imgSize.height;
		final int boundWidth = boundary.width;
		final int boundHeight = boundary.height;
		int newWidth = originalWidth;
		int newHeight = originaHeight;

		// first check if we need to scale width
		if (originalWidth != boundWidth) {
			// scale width to fit
			newWidth = boundWidth;
			// scale height to maintain aspect ratio
			newHeight = (newWidth * originaHeight) / originalWidth;
		}

		return new Dimension(newWidth, newHeight);
	}

	public static Dimension getScaledDimension(Dimension dim, double zoom) {
		return new Dimension((int) (dim.getWidth() * zoom), (int) (dim.getHeight() * zoom));
	}
}
