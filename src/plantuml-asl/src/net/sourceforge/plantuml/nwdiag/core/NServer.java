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
package net.sourceforge.plantuml.nwdiag.core;

import java.util.LinkedHashMap;
import java.util.Map;

import net.sourceforge.plantuml.ComponentStyle;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.USymbol;
import net.sourceforge.plantuml.nwdiag.next.NBar;
import net.sourceforge.plantuml.skin.ActorStyle;
import net.sourceforge.plantuml.svek.PackageStyle;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;

public class NServer {

	private final Map<Network, String> connections = new LinkedHashMap<Network, String>();

	private USymbol shape = USymbol.RECTANGLE;
	private final String name;
	private String description;
	private final NBar bar = new NBar();

	public void connect(Network network, Map<String, String> props) {
		String address = props.get("address");
		if (address == null) {
			address = "";
		}
		if (address.length() == 0 && connections.containsKey(network)) {
			return;
		}
		connections.put(network, address);
		bar.addStage(network.getNstage());
	}

	@Override
	public final String toString() {
		return name;
	}

	public NServer(String name) {
		this.description = name;
		this.name = name;
	}

	protected final FontConfiguration getFontConfiguration() {
		final UFont font = UFont.serif(11);
		return new FontConfiguration(font, HColorUtils.BLACK, HColorUtils.BLACK, false);
	}

	public final String getDescription() {
		return description;
	}

	public final void setDescription(String description) {
		this.description = description;
	}

	public final String getName() {
		return name;
	}

	public final void setShape(String shapeName) {
		final USymbol shapeFromString = USymbol.fromString(shapeName, ActorStyle.STICKMAN, ComponentStyle.RECTANGLE,
				PackageStyle.RECTANGLE);
		if (shapeFromString != null) {
			this.shape = shapeFromString;
		}
	}

	public final USymbol getShape() {
		return shape;
	}

	public final NBar getBar() {
		return bar;
	}

}
