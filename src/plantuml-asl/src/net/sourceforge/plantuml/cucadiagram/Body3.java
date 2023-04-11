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
package net.sourceforge.plantuml.cucadiagram;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.klimt.creole.Display;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.font.FontConfiguration;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.HorizontalAlignment;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.klimt.shape.AbstractTextBlock;
import net.sourceforge.plantuml.klimt.shape.TextBlock;
import net.sourceforge.plantuml.skin.VisibilityModifier;
import net.sourceforge.plantuml.stereo.Stereotype;
import net.sourceforge.plantuml.style.ISkinParam;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.svek.Ports;
import net.sourceforge.plantuml.svek.WithPorts;

public class Body3 extends AbstractTextBlock implements TextBlock, WithPorts {

	private final List<CharSequence> rawBody = new ArrayList<>();
	private final ISkinParam skinParam;
	private final Stereotype stereotype;
	private final Style style;

	public Body3(List<CharSequence> rawBody_, ISkinParam skinParam, Stereotype stereotype, Style style) {
		for (CharSequence s : rawBody_)
			this.rawBody.add(VisibilityModifier.replaceVisibilityModifierByUnicodeChar(s.toString(), true));

		this.skinParam = skinParam;
		this.stereotype = stereotype;
		this.style = style;
	}

	public void drawU(UGraphic ug) {
		getTextBlock().drawU(ug);

	}

	private TextBlock getTextBlock() {
		final Display display = Display.create(rawBody);
		final FontConfiguration config = FontConfiguration.create(skinParam, style);
		return display.create(config, HorizontalAlignment.LEFT, skinParam);
	}

	@Override
	public Ports getPorts(StringBounder stringBounder) {
		return new Ports();
	}

	public XDimension2D calculateDimension(StringBounder stringBounder) {
		return getTextBlock().calculateDimension(stringBounder);
	}

}
