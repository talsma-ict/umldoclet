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
package net.sourceforge.plantuml.ugraphic.tikz;

import java.io.IOException;
import java.io.OutputStream;

import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.creole.legacy.AtomText;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.posimo.DotPath;
import net.sourceforge.plantuml.tikz.TikzGraphics;
import net.sourceforge.plantuml.ugraphic.AbstractCommonUGraphic;
import net.sourceforge.plantuml.ugraphic.AbstractUGraphic;
import net.sourceforge.plantuml.ugraphic.ClipContainer;
import net.sourceforge.plantuml.ugraphic.UCenteredCharacter;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UImage;
import net.sourceforge.plantuml.ugraphic.UImageSvg;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UPath;
import net.sourceforge.plantuml.ugraphic.UPolygon;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UText;
import net.sourceforge.plantuml.ugraphic.color.ColorMapper;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class UGraphicTikz extends AbstractUGraphic<TikzGraphics> implements ClipContainer {

	public UGraphicTikz(HColor defaultBackground, ColorMapper colorMapper, StringBounder stringBounder, double scale, boolean withPreamble) {
		super(defaultBackground, colorMapper, stringBounder, new TikzGraphics(scale, withPreamble));
		register();
	}

	@Override
	protected AbstractCommonUGraphic copyUGraphic() {
		return new UGraphicTikz(this);
	}

	private UGraphicTikz(UGraphicTikz other) {
		super(other);
		register();
	}

	private void register() {
		registerDriver(URectangle.class, new DriverRectangleTikz());
		registerDriver(UText.class, new DriverTextTikz());
		registerDriver(AtomText.class, new DriverAtomTextTikz());
		registerDriver(ULine.class, new DriverLineTikz());
		registerDriver(UPolygon.class, new DriverPolygonTikz());
		registerDriver(UEllipse.class, new DriverEllipseTikz());
		registerDriver(UImage.class, new DriverImageTikz());
		ignoreShape(UImageSvg.class);
		registerDriver(UPath.class, new DriverPathTikz());
		registerDriver(DotPath.class, new DriverDotPathTikz());
		// registerDriver(UCenteredCharacter.class, new DriverCenteredCharacterTikz());
		registerDriver(UCenteredCharacter.class, new DriverCenteredCharacterTikz2());
	}

	@Override
	public void startUrl(Url url) {
		getGraphicObject().openLink(url.getUrl(), url.getTooltip());
	}

	@Override
	public void closeUrl() {
		getGraphicObject().closeLink();
	}

	@Override
	public void writeToStream(OutputStream os, String metadata, int dpi) throws IOException {
		getGraphicObject().createData(os);
	}

	@Override
	public boolean matchesProperty(String propertyName) {
		if ("SPECIALTXT".equalsIgnoreCase(propertyName)) {
			return true;
		}
		return false;
	}

}
