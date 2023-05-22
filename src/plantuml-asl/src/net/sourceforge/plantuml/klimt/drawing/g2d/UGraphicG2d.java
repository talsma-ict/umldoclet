/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
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
package net.sourceforge.plantuml.klimt.drawing.g2d;

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

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.anim.AffineTransformation;
import net.sourceforge.plantuml.klimt.UAntiAliasing;
import net.sourceforge.plantuml.klimt.UChange;
import net.sourceforge.plantuml.klimt.UClip;
import net.sourceforge.plantuml.klimt.UPath;
import net.sourceforge.plantuml.klimt.color.ColorMapper;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.drawing.AbstractCommonUGraphic;
import net.sourceforge.plantuml.klimt.drawing.AbstractUGraphic;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.EnsureVisible;
import net.sourceforge.plantuml.klimt.shape.DotPath;
import net.sourceforge.plantuml.klimt.shape.UCenteredCharacter;
import net.sourceforge.plantuml.klimt.shape.UEllipse;
import net.sourceforge.plantuml.klimt.shape.UImage;
import net.sourceforge.plantuml.klimt.shape.UImageSvg;
import net.sourceforge.plantuml.klimt.shape.ULine;
import net.sourceforge.plantuml.klimt.shape.UPixel;
import net.sourceforge.plantuml.klimt.shape.UPolygon;
import net.sourceforge.plantuml.klimt.shape.URectangle;
import net.sourceforge.plantuml.klimt.shape.UText;
import net.sourceforge.plantuml.png.PngIO;
import net.sourceforge.plantuml.security.SecurityUtils;
import net.sourceforge.plantuml.url.Url;

public class UGraphicG2d extends AbstractUGraphic<Graphics2D> implements EnsureVisible {

	private BufferedImage bufferedImage;

	private final double dpiFactor;
	private final FileFormat format;

	private UAntiAliasing antiAliasing = UAntiAliasing.ANTI_ALIASING_ON;

	private List<Url> urls = new ArrayList<>();
	private Set<Url> allUrls = new HashSet<>();

	// ::comment when __CORE__
	private final boolean hasAffineTransform;
	// ::done

	public final Set<Url> getAllUrlsEncountered() {
		return Collections.unmodifiableSet(allUrls);
	}

	@Override
	public UGraphic apply(UChange change) {
		final UGraphicG2d copy = (UGraphicG2d) super.apply(change);
		if (change instanceof UAntiAliasing)
			copy.antiAliasing = (UAntiAliasing) change;

		return copy;
	}

	@Override
	protected AbstractCommonUGraphic copyUGraphic() {
		final UGraphicG2d result = new UGraphicG2d(this);
		return result;
	}

	private UGraphicG2d(UGraphicG2d other) {
		super(other.getStringBounder());
		copy(other);
		// ::comment when __CORE__
		this.hasAffineTransform = other.hasAffineTransform;
		// ::done
		this.dpiFactor = other.dpiFactor;
		this.bufferedImage = other.bufferedImage;
		this.urls = other.urls;
		this.allUrls = other.allUrls;
		this.antiAliasing = other.antiAliasing;
		this.format = other.format;
		register(dpiFactor);
	}

	public UGraphicG2d(HColor defaultBackground, ColorMapper colorMapper, StringBounder stringBounder, Graphics2D g2d,
			double dpiFactor, FileFormat format) {
		// ::revert when __CORE__
		this(defaultBackground, colorMapper, stringBounder, g2d, dpiFactor, 0, 0, format, null);
		// this(defaultBackground, colorMapper, stringBounder, g2d, dpiFactor, 0, 0,
		// format);
		// ::done
	}

	// ::revert when __CORE__
	public UGraphicG2d(HColor defaultBackground, ColorMapper colorMapper, StringBounder stringBounder, Graphics2D g2d,
			double dpiFactor, double dx, double dy, FileFormat format, AffineTransformation affineTransform) {
		// public UGraphicG2d(HColor defaultBackground, ColorMapper colorMapper,
		// StringBounder stringBounder, Graphics2D g2d,
		// double dpiFactor, double dx, double dy, FileFormat format) {
		// ::done
		super(stringBounder);
		copy(defaultBackground, colorMapper, g2d);
		this.format = format;
		this.dpiFactor = dpiFactor;
		if (dpiFactor != 1.0)
			g2d.scale(dpiFactor, dpiFactor);

		// ::comment when __CORE__
		this.hasAffineTransform = affineTransform != null;
		if (this.hasAffineTransform) {
			if (dx != 0 || dy != 0)
				getGraphicObject().transform(AffineTransform.getTranslateInstance(dx, dy));
			getGraphicObject().transform(affineTransform.getAffineTransform());
		}
		// ::done
		register(dpiFactor);
	}

	private void register(double dpiFactor) {
		registerDriver(URectangle.class, new DriverRectangleG2d(dpiFactor, this));
		// ::comment when __CORE__
		if (this.hasAffineTransform || dpiFactor != 1.0)
			registerDriver(UText.class, new DriverTextAsPathG2d(this, getStringBounder()));
		else
			// ::done
			registerDriver(UText.class, new DriverTextG2d(this, getStringBounder()));

		registerDriver(ULine.class, new DriverLineG2d(dpiFactor));
		registerDriver(UPixel.class, new DriverPixelG2d());
		registerDriver(UPolygon.class, new DriverPolygonG2d(dpiFactor, this));
		registerDriver(UEllipse.class, new DriverEllipseG2d(dpiFactor, this));
		ignoreShape(UImageSvg.class);
		registerDriver(UImage.class, new DriverImageG2d(dpiFactor, this));
		registerDriver(DotPath.class, new DriverDotPathG2d(this));
		registerDriver(UPath.class, new DriverPathG2d(dpiFactor));
		registerDriver(UCenteredCharacter.class, new DriverCenteredCharacterG2d());
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

	@Override
	public void startUrl(Url url) {
		Objects.requireNonNull(url);
		// javascript: security issue
		if (SecurityUtils.ignoreThisLink(url.getUrl())) {
			urls.add(null);
		} else {
			urls.add(url);
			allUrls.add(url);
		}
	}

	@Override
	public void closeUrl() {
		urls.remove(urls.size() - 1);
	}

	public void ensureVisible(double x, double y) {
		for (Url u : urls)
			if (u != null && (getClip() == null || getClip().isInside(x, y)))
				u.ensureVisible(x, y);

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

	@Override
	public void writeToStream(OutputStream os, String metadata, int dpi) throws IOException {
		final BufferedImage im = getBufferedImage();
		PngIO.write(im, getColorMapper(), os, metadata, dpi);
	}

	@Override
	public double dpiFactor() {
		return dpiFactor;
	}

}
