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
package net.sourceforge.plantuml.ugraphic.g2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

import net.sourceforge.plantuml.Log;

public class DriverShadowedG2d {

	private ConvolveOp getConvolveOp(int blurRadius, double dpiFactor) {
		final float[] elements = new float[(int) (blurRadius * blurRadius * dpiFactor)];
		for (int k = 0; k < elements.length; k++) {
			elements[k] = (float) (1.0 / elements.length);
		}
		final Kernel myKernel = new Kernel((int) (blurRadius * Math.sqrt(dpiFactor)),
				(int) (blurRadius * Math.sqrt(dpiFactor)), elements);

		// if EDGE_NO_OP is not selected, EDGE_ZERO_FILL is the default which
		// creates a black border
		return new ConvolveOp(myKernel, ConvolveOp.EDGE_NO_OP, null);
	}

	private final Color color = new Color(170, 170, 170);
	private final Color colorLine = new Color(30, 30, 30);

	protected void drawShadow(Graphics2D g2d, Shape shape, double deltaShadow, double dpiFactor) {
		if (dpiFactor < 1) {
			dpiFactor = 1;
		}
		final Rectangle2D bounds = shape.getBounds2D();
		final double ww = bounds.getMaxX() - bounds.getMinX();
		final double hh = bounds.getMaxY() - bounds.getMinY();

		final double w = (ww + deltaShadow * 2 + 6) * dpiFactor;
		final double h = (hh + deltaShadow * 2 + 6) * dpiFactor;
		BufferedImage destination = null;
		try {
			destination = new BufferedImage((int) w, (int) h, BufferedImage.TYPE_INT_ARGB);
			final Graphics2D gg = destination.createGraphics();
			gg.scale(dpiFactor, dpiFactor);
			gg.translate(deltaShadow - bounds.getMinX(), deltaShadow - bounds.getMinY());
			final boolean isLine = shape instanceof Line2D.Double;
			if (isLine) {
				gg.setColor(colorLine);
				gg.draw(shape);
			} else {
				gg.setColor(color);
				gg.fill(shape);
			}
			gg.dispose();

			final ConvolveOp simpleBlur = getConvolveOp(6, dpiFactor);
			destination = simpleBlur.filter(destination, null);
		} catch (OutOfMemoryError error) {
			Log.info("Warning: Cannot draw shadow, image too big.");
		} catch (Exception e) {
			Log.info("Warning: Cannot draw shadow: " + e);
		}
		if (destination != null) {
			final AffineTransform at = g2d.getTransform();
			g2d.scale(1 / dpiFactor, 1 / dpiFactor);
			g2d.drawImage(destination, (int) (bounds.getMinX() * dpiFactor), (int) (bounds.getMinY() * dpiFactor),
					null);
			g2d.setTransform(at);
		}
	}

	protected void drawOnlyLineShadow(Graphics2D g2d, Shape shape, double deltaShadow, double dpiFactor) {
		if (dpiFactor < 1) {
			dpiFactor = 1;
		}
		final Rectangle2D bounds = shape.getBounds2D();
		final double ww = bounds.getMaxX() - bounds.getMinX();
		final double hh = bounds.getMaxY() - bounds.getMinY();

		final double w = (ww + deltaShadow * 2 + 6) * dpiFactor;
		final double h = (hh + deltaShadow * 2 + 6) * dpiFactor;
		BufferedImage destination = null;
		try {
			destination = new BufferedImage((int) w, (int) h, BufferedImage.TYPE_INT_ARGB);
			final Graphics2D gg = destination.createGraphics();
			gg.scale(dpiFactor, dpiFactor);
			gg.translate(deltaShadow - bounds.getMinX(), deltaShadow - bounds.getMinY());
			gg.draw(shape);
			gg.dispose();

			final ConvolveOp simpleBlur = getConvolveOp(6, dpiFactor);
			destination = simpleBlur.filter(destination, null);
		} catch (OutOfMemoryError error) {
			Log.info("Warning: Cannot draw shadow, image too big.");
		} catch (Exception e) {
			Log.info("Warning: Cannot draw shadow: " + e);
		}
		if (destination != null) {
			final AffineTransform at = g2d.getTransform();
			g2d.scale(1 / dpiFactor, 1 / dpiFactor);
			g2d.drawImage(destination, (int) (bounds.getMinX() * dpiFactor), (int) (bounds.getMinY() * dpiFactor),
					null);
			g2d.setTransform(at);
		}
	}

	protected void drawOnlyLineShadowSpecial(Graphics2D g2d, Shape shape, double deltaShadow, double dpiFactor) {
		if (dpiFactor < 1) {
			dpiFactor = 1;
		}
		final Rectangle2D bounds = shape.getBounds2D();
		final double ww = bounds.getMaxX() - bounds.getMinX();
		final double hh = bounds.getMaxY() - bounds.getMinY();

		final double w = (ww + deltaShadow * 2 + 6) * dpiFactor;
		final double h = (hh + deltaShadow * 2 + 6) * dpiFactor;
		BufferedImage destination = null;
		try {
			destination = new BufferedImage((int) w, (int) h, BufferedImage.TYPE_INT_ARGB);
			final Graphics2D gg = destination.createGraphics();
			gg.scale(dpiFactor, dpiFactor);
			gg.translate(deltaShadow - bounds.getMinX(), deltaShadow - bounds.getMinY());
			gg.draw(shape);
			gg.dispose();

			final ConvolveOp simpleBlur = getConvolveOp(6, dpiFactor);
			destination = simpleBlur.filter(destination, null);
		} catch (OutOfMemoryError error) {
			Log.info("Warning: Cannot draw shadow, image too big.");
		} catch (Exception e) {
			Log.info("Warning: Cannot draw shadow: " + e);
		}
		if (destination != null) {
			final AffineTransform at = g2d.getTransform();
			g2d.scale(1 / dpiFactor, 1 / dpiFactor);
			final Shape sav = g2d.getClip();

			final Area full = new Area(
					new Rectangle2D.Double(0, 0, (bounds.getMaxX() + deltaShadow * 2 + 6) * dpiFactor,
							(bounds.getMaxY() + deltaShadow * 2 + 6) * dpiFactor));
			if (dpiFactor == 1) {
				full.subtract(new Area(shape));
			} else {
				full.subtract(
						new Area(shape).createTransformedArea(AffineTransform.getScaleInstance(dpiFactor, dpiFactor)));
			}
			g2d.setClip(full);

			g2d.drawImage(destination, (int) (bounds.getMinX() * dpiFactor), (int) (bounds.getMinY() * dpiFactor),
					null);
			g2d.setClip(sav);
			g2d.setTransform(at);
		}
	}
}
