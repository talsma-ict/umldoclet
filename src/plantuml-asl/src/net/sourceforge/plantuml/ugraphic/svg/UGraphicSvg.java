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
package net.sourceforge.plantuml.ugraphic.svg;

import net.sourceforge.plantuml.awt.geom.Dimension2D;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.xml.transform.TransformerException;

import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.posimo.DotPath;
import net.sourceforge.plantuml.svg.DarkStrategy;
import net.sourceforge.plantuml.svg.LengthAdjust;
import net.sourceforge.plantuml.svg.SvgGraphics;
import net.sourceforge.plantuml.ugraphic.AbstractCommonUGraphic;
import net.sourceforge.plantuml.ugraphic.AbstractUGraphic;
import net.sourceforge.plantuml.ugraphic.ClipContainer;
import net.sourceforge.plantuml.ugraphic.UCenteredCharacter;
import net.sourceforge.plantuml.ugraphic.UComment;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UGroupType;
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
import net.sourceforge.plantuml.ugraphic.color.HColorGradient;

public class UGraphicSvg extends AbstractUGraphic<SvgGraphics> implements ClipContainer {

	private final boolean textAsPath2;
	private final String target;
	private final boolean interactive;

	public double dpiFactor() {
		return 1;
	}

	@Override
	protected AbstractCommonUGraphic copyUGraphic() {
		return new UGraphicSvg(this);
	}

	private UGraphicSvg(UGraphicSvg other) {
		super(other);
		this.textAsPath2 = other.textAsPath2;
		this.target = other.target;
		this.interactive = other.interactive;
		register();
	}

	public UGraphicSvg(HColor defaultBackground, boolean svgDimensionStyle, Dimension2D minDim, ColorMapper colorMapper,
			boolean textAsPath, double scale, String linkTarget, String hover, long seed, String preserveAspectRatio,
			StringBounder stringBounder, LengthAdjust lengthAdjust, boolean interactive) {
		this(defaultBackground, minDim, colorMapper,
				new SvgGraphics(colorMapper.toSvg(defaultBackground), svgDimensionStyle, minDim, scale, hover, seed,
						preserveAspectRatio, lengthAdjust, DarkStrategy.IGNORE_DARK_COLOR, interactive),
				textAsPath, linkTarget, stringBounder, interactive);
		if (defaultBackground instanceof HColorGradient) {
			final SvgGraphics svg = getGraphicObject();
			svg.paintBackcolorGradient(colorMapper, (HColorGradient) defaultBackground);
		}
	}

	@Override
	protected boolean manageHiddenAutomatically() {
		return false;
	}

	@Override
	protected void beforeDraw() {
		getGraphicObject().setHidden(getParam().isHidden());
	}

	@Override
	protected void afterDraw() {
		getGraphicObject().setHidden(false);
	}

	private UGraphicSvg(HColor defaultBackground, Dimension2D minDim, ColorMapper colorMapper, SvgGraphics svg,
			boolean textAsPath, String linkTarget, StringBounder stringBounder, boolean interactive) {
		super(defaultBackground, colorMapper, stringBounder, svg);
		this.textAsPath2 = textAsPath;
		this.target = linkTarget;
		this.interactive = interactive;
		register();
	}

	private void register() {
		registerDriver(URectangle.class, new DriverRectangleSvg(this));
		if (textAsPath2)
			registerDriver(UText.class, new DriverTextAsPathSvg(this));
		else
			registerDriver(UText.class, new DriverTextSvg(getStringBounder(), this));

		registerDriver(ULine.class, new DriverLineSvg(this));
		registerDriver(UPixel.class, new DriverPixelSvg());
		registerDriver(UPolygon.class, new DriverPolygonSvg(this));
		registerDriver(UEllipse.class, new DriverEllipseSvg(this));
		registerDriver(UImage.class, new DriverImagePng(this));
		registerDriver(UImageSvg.class, new DriverImageSvgSvg());
		registerDriver(UPath.class, new DriverPathSvg(this));
		registerDriver(DotPath.class, new DriverDotPathSvg());
		registerDriver(UCenteredCharacter.class, new DriverCenteredCharacterSvg());
	}

	public SvgGraphics getSvgGraphics() {
		return this.getGraphicObject();
	}

	@Override
	public void writeToStream(OutputStream os, String metadata, int dpi) throws IOException {
		try {
			if (metadata != null)
				getGraphicObject().addComment(metadata);

			if (interactive) {
				// For performance reasons and also because we want the entire graph DOM to be create so we can register
				// the event handlers on them we will append to the end of the document
				getGraphicObject().addStyle("onmouseinteractivefooter.css");
				getGraphicObject().addScriptTag("https://cdn.jsdelivr.net/npm/@svgdotjs/svg.js@3.0/dist/svg.min.js");
				getGraphicObject().addScript("onmouseinteractivefooter.js");
			}

			getGraphicObject().createXml(os);
		} catch (TransformerException e) {
			throw new IOException(e.toString());
		}
	}

	@Override
	public void startGroup(Map<UGroupType, String> typeIdents) {
		getGraphicObject().startGroup(typeIdents);
	}

	@Override
	public void closeGroup() {
		getGraphicObject().closeGroup();
	}

	@Override
	public void startUrl(Url url) {
		getGraphicObject().openLink(url.getUrl(), url.getTooltip(), target);
	}

	@Override
	public void closeUrl() {
		getGraphicObject().closeLink();
	}

	@Override
	protected void drawComment(UComment comment) {
		getGraphicObject().addComment(comment.getComment());
	}

	@Override
	public boolean matchesProperty(String propertyName) {
		if (propertyName.equalsIgnoreCase("SVG"))
			return true;

		return super.matchesProperty(propertyName);
	}

	// @Override
	// public String startHiddenGroup() {
	// getGraphicObject().startHiddenGroup();
	// return null;
	// }
	//
	// @Override
	// public String closeHiddenGroup() {
	// getGraphicObject().closeHiddenGroup();
	// return null;
	// }

}
