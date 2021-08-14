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
package net.sourceforge.plantuml.nwdiag.legacy;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.SymbolContext;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.nwdiag.core.NServer;

public class NServerLegacy extends NServer {

	private final NetworkLegacy mainNetwork;
	private final ISkinSimple spriteContainer;
	private boolean hasItsOwnColumn = true;
	private NServerLegacy sameCol;

	public NServerLegacy(String name, NetworkLegacy network, ISkinSimple spriteContainer) {
		super(name);
		this.mainNetwork = network;
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

	public LinkedElement asTextBlock(Map<NetworkLegacy, String> conns, List<NetworkLegacy> networks) {
		final Map<NetworkLegacy, TextBlock> conns2 = new LinkedHashMap<NetworkLegacy, TextBlock>();
		for (Entry<NetworkLegacy, String> ent : conns.entrySet()) {
			conns2.put(ent.getKey(), toTextBlock(ent.getValue()));
		}
		final SymbolContext symbolContext = new SymbolContext(ColorParam.activityBackground.getDefaultValue(),
				ColorParam.activityBorder.getDefaultValue()).withShadow(3);
		final TextBlock desc = toTextBlock(getDescription());
		final TextBlock box = getShape().asSmall(TextBlockUtils.empty(0, 0), desc, TextBlockUtils.empty(0, 0),
				symbolContext, HorizontalAlignment.CENTER);
		return new LinkedElement(this, box, conns2, networks);
	}

	public final NetworkLegacy getMainNetwork() {
		return mainNetwork;
	}

	public void doNotHaveItsOwnColumn() {
		this.hasItsOwnColumn = false;
	}

	public final boolean hasItsOwnColumn() {
		return hasItsOwnColumn;
	}

	public void sameColThan(NServerLegacy sameCol) {
		this.sameCol = sameCol;
	}

	public final NServerLegacy getSameCol() {
		return sameCol;
	}

	private int numCol = -1;

	public void setNumCol(int j) {
		this.numCol = j;
	}

	public final int getNumCol() {
		return numCol;
	}

}
