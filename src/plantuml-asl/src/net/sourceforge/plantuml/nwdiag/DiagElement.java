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
package net.sourceforge.plantuml.nwdiag;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.ComponentStyle;
import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.SymbolContext;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.graphic.USymbol;
import net.sourceforge.plantuml.skin.ActorStyle;
import net.sourceforge.plantuml.svek.PackageStyle;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;

public class DiagElement {

	private USymbol shape = USymbol.RECTANGLE;
	private final String name;
	private String description;
	private final Network mainNetwork;
	private final ISkinSimple spriteContainer;

	@Override
	public String toString() {
		return name;
	}

	public DiagElement(String name, Network network, ISkinSimple spriteContainer) {
		this.description = name;
		this.mainNetwork = network;
		this.name = name;
		this.spriteContainer = spriteContainer;
	}

	private TextBlock toTextBlock(String s) {
		if (s == null) {
			return null;
		}
		if (s.length() == 0) {
			return TextBlockUtils.empty(0, 0);
		}
		s = s.replace(", ", "\\n");
		return Display.getWithNewlines(s).create(getFontConfiguration(), HorizontalAlignment.LEFT, spriteContainer);
	}

	private FontConfiguration getFontConfiguration() {
		final UFont font = UFont.serif(11);
		return new FontConfiguration(font, HColorUtils.BLACK, HColorUtils.BLACK, false);
	}

	public LinkedElement asTextBlock(Map<Network, String> conns, Network next) {
		final Map<Network, TextBlock> conns2 = new LinkedHashMap<Network, TextBlock>();
		for (Entry<Network, String> ent : conns.entrySet()) {
			conns2.put(ent.getKey(), toTextBlock(ent.getValue()));
		}
		final SymbolContext symbolContext = new SymbolContext(ColorParam.activityBackground.getDefaultValue(),
				ColorParam.activityBorder.getDefaultValue()).withShadow(3);
		final TextBlock desc = toTextBlock(description);
		final TextBlock box = shape.asSmall(TextBlockUtils.empty(0, 0), desc, TextBlockUtils.empty(0, 0), symbolContext,
				HorizontalAlignment.CENTER);
		return new LinkedElement(this, box, mainNetwork, next, conns2);
	}

	public String getDescription() {
		return description;
	}

	public final Network getMainNetwork() {
		return mainNetwork;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public final void setShape(String shapeName) {
		final USymbol shapeFromString = USymbol.fromString(shapeName, ActorStyle.STICKMAN, ComponentStyle.RECTANGLE,
				PackageStyle.RECTANGLE);
		if (shapeFromString != null) {
			this.shape = shapeFromString;
		}
	}

}
