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
package net.sourceforge.plantuml.ugraphic.g2d;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import net.sourceforge.plantuml.EnsureVisible;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.anim.AffineTransformation;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.png.PngIO;
import net.sourceforge.plantuml.posimo.DotPath;
import net.sourceforge.plantuml.ugraphic.AbstractCommonUGraphic;
import net.sourceforge.plantuml.ugraphic.AbstractUGraphic;
import net.sourceforge.plantuml.ugraphic.UAntiAliasing;
import net.sourceforge.plantuml.ugraphic.UCenteredCharacter;
import net.sourceforge.plantuml.ugraphic.UChange;
import net.sourceforge.plantuml.ugraphic.UClip;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UGraphic2;
import net.sourceforge.plantuml.ugraphic.UImage;
import net.sourceforge.plantuml.ugraphic.UImageSvg;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UPath;
import net.sourceforge.plantuml.ugraphic.UPixel;
import net.sourceforge.plantuml.ugraphic.UPolygon;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UText;
import net.sourceforge.plantuml.ugraphic.color.ColorMapper;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class UGraphicG2d extends AbstractUGraphic<Graphics2D> implements EnsureVisible, UGraphic2 {

	private BufferedImage bufferedImage;

	private final double dpiFactor;

	private UAntiAliasing antiAliasing = UAntiAliasing.ANTI_ALIASING_ON;

	private/* final */List<Url> urls = new ArrayList<>();
	private Set<Url> allUrls = new HashSet<>();

	private final boolean hasAffineTransform;

	public final Set<Url> getAllUrlsEncountered() {
		return Collections.unmodifiableSet(allUrls);
	}

	@Override
	public UGraphic apply(UChange change) {
		final UGraphicG2d copy = (UGraphicG2d) super.apply(change);
		if (change instanceof UAntiAliasing) {
			copy.antiAliasing = (UAntiAliasing) change;
		}
		return copy;
	}

	@Override
	protected AbstractCommonUGraphic copyUGraphic() {
		return new UGraphicG2d(this);
	}

	private UGraphicG2d(UGraphicG2d other) {
		super(other);
		this.hasAffineTransform = other.hasAffineTransform;
		this.dpiFactor = other.dpiFactor;
		this.bufferedImage = other.bufferedImage;
		this.urls = other.urls;
		this.allUrls = other.allUrls;
		this.antiAliasing = other.antiAliasing;
		register(dpiFactor);
	}

	public UGraphicG2d(HColor defaultBackground, ColorMapper colorMapper, Graphics2D g2d, double dpiFactor) {
		this(defaultBackground, colorMapper, g2d, dpiFactor, null, 0, 0);
	}

	public UGraphicG2d(HColor defaultBackground, ColorMapper colorMapper, Graphics2D g2d, double dpiFactor,
			AffineTransformation affineTransform, double dx, double dy) {
		super(defaultBackground, colorMapper, g2d);
		this.hasAffineTransform = affineTransform != null;
		this.dpiFactor = dpiFactor;
		if (dpiFactor != 1.0) {
			g2d.scale(dpiFactor, dpiFactor);
		}
		if (this.hasAffineTransform) {
			if (dx != 0 || dy != 0) {
				getGraphicObject().transform(AffineTransform.getTranslateInstance(dx, dy));
			}
			getGraphicObject().transform(affineTransform.getAffineTransform());
		}
		register(dpiFactor);
	}

	private void register(double dpiFactor) {
		registerDriver(URectangle.class, new DriverRectangleG2d(dpiFactor, this));
		if (this.hasAffineTransform || dpiFactor != 1.0) {
			registerDriver(UText.class, new DriverTextAsPathG2d(this, TextBlockUtils.getFontRenderContext()));
		} else {
			registerDriver(UText.class, new DriverTextG2d(this));
		}
		registerDriver(ULine.class, new DriverLineG2d(dpiFactor));
		registerDriver(UPixel.class, new DriverPixelG2d());
		registerDriver(UPolygon.class, new DriverPolygonG2d(dpiFactor, this));
		registerDriver(UEllipse.class, new DriverEllipseG2d(dpiFactor, this));
		registerDriver(UImageSvg.class, new DriverImageG2d(dpiFactor, this));
		registerDriver(UImage.class, new DriverImageG2d(dpiFactor, this));
		registerDriver(DotPath.class, new DriverDotPathG2d(this));
		registerDriver(UPath.class, new DriverPathG2d(dpiFactor));
		registerDriver(UCenteredCharacter.class, new DriverCenteredCharacterG2d());
	}

	public StringBounder getStringBounder() {
		// if (hasAffineTransform) {
		// return TextBlockUtils.getDummyStringBounder();
		// }
		return FileFormat.PNG.getDefaultStringBounder();
	}

	@Override
	protected void beforeDraw() {
		super.beforeDraw();
		applyClip();
		antiAliasing.apply(getGraphicObject());
	}

	private void applyClip() {
		final UClip uclip = getClip();
		if (uclip == null) {
			getGraphicObject().setClip(null);
		} else {
			final Shape clip = new Rectangle2D.Double(uclip.getX(), uclip.getY(), uclip.getWidth(), uclip.getHeight());
			getGraphicObject().setClip(clip);
		}
	}

	protected final double getDpiFactor() {
		return dpiFactor;
	}

	public void startUrl(Url url) {
		urls.add(Objects.requireNonNull(url));
		allUrls.add(url);
	}

	public void closeUrl() {
		urls.remove(urls.size() - 1);
	}

	public void ensureVisible(double x, double y) {
		for (Url u : urls) {
			if (getClip() == null || getClip().isInside(x, y)) {
				u.ensureVisible(x, y);
			}
		}
	}

	public BufferedImage getBufferedImage() {
		return bufferedImage;
	}

	public void setBufferedImage(BufferedImage bufferedImage) {
		this.bufferedImage = bufferedImage;
	}

	public Graphics2D getGraphics2D() {
		return getGraphicObject();
	}

	public void writeImageTOBEMOVED(OutputStream os, String metadata, int dpi) throws IOException {
		final BufferedImage im = getBufferedImage();
		PngIO.write(im, os, metadata, dpi);
	}

	@Override
	public double dpiFactor() {
		return dpiFactor;
	}

}
