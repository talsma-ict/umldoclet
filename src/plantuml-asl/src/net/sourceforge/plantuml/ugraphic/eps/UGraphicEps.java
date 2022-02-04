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
package net.sourceforge.plantuml.ugraphic.eps;

import java.io.IOException;
import java.io.OutputStream;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.eps.EpsGraphics;
import net.sourceforge.plantuml.eps.EpsStrategy;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.posimo.DotPath;
import net.sourceforge.plantuml.ugraphic.AbstractCommonUGraphic;
import net.sourceforge.plantuml.ugraphic.AbstractUGraphic;
import net.sourceforge.plantuml.ugraphic.ClipContainer;
import net.sourceforge.plantuml.ugraphic.UCenteredCharacter;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UImage;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UPath;
import net.sourceforge.plantuml.ugraphic.UPolygon;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UText;
import net.sourceforge.plantuml.ugraphic.color.ColorMapper;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class UGraphicEps extends AbstractUGraphic<EpsGraphics> implements ClipContainer {

	private final EpsStrategy strategyTOBEREMOVED;

	@Override
	protected AbstractCommonUGraphic copyUGraphic() {
		return new UGraphicEps(this);
	}

	protected UGraphicEps(UGraphicEps other) {
		super(other);
		this.strategyTOBEREMOVED = other.strategyTOBEREMOVED;
		register();
	}

	public UGraphicEps(HColor defaultBackground, ColorMapper colorMapper, StringBounder stringBounder, EpsStrategy strategy) {
		super(defaultBackground, colorMapper, stringBounder, strategy.creatEpsGraphics());
		this.strategyTOBEREMOVED = strategy;
		register();
	}

	private void register() {
		registerDriver(URectangle.class, new DriverRectangleEps(this));
		registerDriver(UText.class, new DriverTextEps(this, strategyTOBEREMOVED));
		registerDriver(ULine.class, new DriverLineEps(this));
		registerDriver(UPolygon.class, new DriverPolygonEps(this));
		registerDriver(UEllipse.class, new DriverEllipseEps(this));
		registerDriver(UImage.class, new DriverImageEps(this));
		registerDriver(UPath.class, new DriverPathEps());
		registerDriver(DotPath.class, new DriverDotPathEps());
		registerDriver(UCenteredCharacter.class, new DriverCenteredCharacterEps());
	}

	public void close() {
		getEpsGraphics().close();
	}

	public String getEPSCode() {
		return getEpsGraphics().getEPSCode();
	}

	public EpsGraphics getEpsGraphics() {
		return this.getGraphicObject();
	}

	public void drawEps(String eps, double x, double y) {
		this.getGraphicObject().drawEps(eps, x, y);
	}

	static public String getEpsString(HColor defaultBackground, ColorMapper colorMapper, EpsStrategy epsStrategy,
			UDrawable udrawable) throws IOException {
		final UGraphicEps ug = new UGraphicEps(defaultBackground, colorMapper, FileFormat.EPS_TEXT.getDefaultStringBounder(), epsStrategy);
		udrawable.drawU(ug);
		return ug.getEPSCode();
	}

	@Override
	public void startUrl(Url url) {
		getGraphicObject().openLink(url.getUrl());
	}

	@Override
	public void closeUrl() {
		getGraphicObject().closeLink();
	}

	@Override
	public void writeToStream(OutputStream os, String metadata, int dpi) throws IOException {
		os.write(getEPSCode().getBytes());
	}

}
