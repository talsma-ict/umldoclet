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
package net.sourceforge.plantuml.klimt.drawing.svg;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.xml.transform.TransformerException;

import net.sourceforge.plantuml.klimt.ClipContainer;
import net.sourceforge.plantuml.klimt.UGroupType;
import net.sourceforge.plantuml.klimt.UPath;
import net.sourceforge.plantuml.klimt.drawing.AbstractCommonUGraphic;
import net.sourceforge.plantuml.klimt.drawing.AbstractUGraphic;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.shape.DotPath;
import net.sourceforge.plantuml.klimt.shape.UCenteredCharacter;
import net.sourceforge.plantuml.klimt.shape.UComment;
import net.sourceforge.plantuml.klimt.shape.UEllipse;
import net.sourceforge.plantuml.klimt.shape.UImage;
import net.sourceforge.plantuml.klimt.shape.UImageSvg;
import net.sourceforge.plantuml.klimt.shape.ULine;
import net.sourceforge.plantuml.klimt.shape.UPixel;
import net.sourceforge.plantuml.klimt.shape.UPolygon;
import net.sourceforge.plantuml.klimt.shape.URectangle;
import net.sourceforge.plantuml.klimt.shape.UText;
import net.sourceforge.plantuml.url.Url;

public class UGraphicSvg extends AbstractUGraphic<SvgGraphics> implements ClipContainer {
	// ::remove file when __HAXE__

	private final boolean textAsPath;
	private /* final */ SvgOption option;

	public double dpiFactor() {
		return 1;
	}

	@Override
	protected AbstractCommonUGraphic copyUGraphic() {
		final UGraphicSvg result = new UGraphicSvg(getStringBounder(), textAsPath);
		result.copy(this);
		result.option = this.option;
		return result;
	}

	private UGraphicSvg(StringBounder stringBounder, boolean textAsPath) {
		super(stringBounder);
		this.textAsPath = textAsPath;
		register();
	}

	public static UGraphicSvg build(SvgOption option, boolean textAsPath, long seed, StringBounder stringBounder) {
		final UGraphicSvg result = new UGraphicSvg(stringBounder, textAsPath);
		result.copy(option.getBackcolor(), option.getColorMapper(), new SvgGraphics(seed, option));
		result.option = option;
		return result;
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

	private void register() {
		registerDriver(URectangle.class, new DriverRectangleSvg(this));
		if (textAsPath)
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
				getGraphicObject().addCommentMetadata(metadata);

			if (option.isInteractive()) {
				// For performance reasons and also because we want the entire graph DOM to be
				// create so we can register
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
		getGraphicObject().openLink(url.getUrl(), url.getTooltip(), option.getLinkTarget());
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
