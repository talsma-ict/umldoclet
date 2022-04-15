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
package net.sourceforge.plantuml.cucadiagram;

import net.sourceforge.plantuml.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.skin.VisibilityModifier;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.svek.Ports;
import net.sourceforge.plantuml.svek.WithPorts;
import net.sourceforge.plantuml.ugraphic.UGraphic;

public class Body3 extends AbstractTextBlock implements TextBlock, WithPorts {

	private final List<CharSequence> rawBody = new ArrayList<>();
	private final FontParam fontParam;
	private final ISkinParam skinParam;
	private final Stereotype stereotype;
	private final Style style;

	public Body3(List<CharSequence> rawBody_, FontParam fontParam, ISkinParam skinParam, Stereotype stereotype,
			Style style) {
		for (CharSequence s : rawBody_) {
			this.rawBody.add(VisibilityModifier.replaceVisibilityModifierByUnicodeChar(s.toString(), true));
		}
		this.fontParam = fontParam;
		this.skinParam = skinParam;
		this.stereotype = stereotype;
		this.style = style;
	}

	public void drawU(UGraphic ug) {
		getTextBlock().drawU(ug);

	}

	private TextBlock getTextBlock() {
		Display display = Display.create(rawBody);

		FontConfiguration config;
		if (style != null) {
			config = FontConfiguration.create(skinParam, style);
		} else {
			config = FontConfiguration.create(skinParam, fontParam, stereotype);
		}

		TextBlock foo = display.create(config, HorizontalAlignment.LEFT, skinParam);
		return foo;
	}

	@Override
	public Ports getPorts(StringBounder stringBounder) {
		return new Ports();
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		return getTextBlock().calculateDimension(stringBounder);
	}

}
