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
package net.sourceforge.plantuml.klimt.drawing.eps;

import java.io.IOException;
import java.io.OutputStream;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.klimt.ClipContainer;
import net.sourceforge.plantuml.klimt.UPath;
import net.sourceforge.plantuml.klimt.color.ColorMapper;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.drawing.AbstractCommonUGraphic;
import net.sourceforge.plantuml.klimt.drawing.AbstractUGraphic;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.shape.DotPath;
import net.sourceforge.plantuml.klimt.shape.UCenteredCharacter;
import net.sourceforge.plantuml.klimt.shape.UDrawable;
import net.sourceforge.plantuml.klimt.shape.UEllipse;
import net.sourceforge.plantuml.klimt.shape.UImage;
import net.sourceforge.plantuml.klimt.shape.ULine;
import net.sourceforge.plantuml.klimt.shape.UPolygon;
import net.sourceforge.plantuml.klimt.shape.URectangle;
import net.sourceforge.plantuml.klimt.shape.UText;
import net.sourceforge.plantuml.url.Url;

public class UGraphicEps extends AbstractUGraphic<EpsGraphics> implements ClipContainer {

	private final EpsStrategy strategyTOBEREMOVED;

	@Override
	protected AbstractCommonUGraphic copyUGraphic() {
		final UGraphicEps result = new UGraphicEps(this);
		return result;
	}

	protected UGraphicEps(UGraphicEps other) {
		super(other.getStringBounder());
		copy(other);
		this.strategyTOBEREMOVED = other.strategyTOBEREMOVED;
		register();
	}

	public UGraphicEps(HColor defaultBackground, ColorMapper colorMapper, StringBounder stringBounder,
			EpsStrategy strategy) {
		super(stringBounder);
		copy(defaultBackground, colorMapper, strategy.creatEpsGraphics());
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
		final UGraphicEps ug = new UGraphicEps(defaultBackground, colorMapper,
				FileFormat.EPS_TEXT.getDefaultStringBounder(), epsStrategy);
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
