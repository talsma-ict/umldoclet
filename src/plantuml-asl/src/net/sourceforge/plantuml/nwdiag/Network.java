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

import net.sourceforge.plantuml.ugraphic.color.HColor;

public class Network {

	private final String name;
	private final Map<Square, String> localSquare = new LinkedHashMap<Square, String>();
	private HColor color;
	private boolean visible = true;
	private String ownAdress;
	private boolean fullWidth;
	private final int stage;

	@Override
	public String toString() {
		return name + "(" + stage + ")";
	}

	public Network(String name, int stage) {
		this.name = name;
		this.stage = stage;
	}

	public String getAdress(Square element) {
		return localSquare.get(element);
	}

	public void addSquare(Square square, Map<String, String> props) {
		String address = props.get("address");
		if (address == null) {
			address = "";
		}
		if (address.length() == 0 && localSquare.containsKey(square)) {
			return;
		}
		localSquare.put(square, address);
	}

	public boolean constainsLocally(String name) {
		for (Square square : localSquare.keySet()) {
			if (square.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public final String getOwnAdress() {
		return ownAdress;
	}

	public final void setOwnAdress(String ownAdress) {
		this.ownAdress = ownAdress;
	}

	public final String getName() {
		return name;
	}

	public final HColor getColor() {
		return color;
	}

	public final void setColor(HColor color) {
		this.color = color;
	}

	public void goInvisible() {
		this.visible = false;
	}

	public final boolean isVisible() {
		return visible;
	}

	public void setFullWidth(boolean fullWidth) {
		this.fullWidth = fullWidth;
	}

	public final boolean isFullWidth() {
		return fullWidth;
	}

	public final int getStage() {
		return stage;
	}

	private double xmin;
	private double xmax;
	private double y;

	public void setMinMax(double xmin, double xmax) {
		this.xmin = xmin;
		this.xmax = xmax;

	}

	public final double getXmin() {
		return xmin;
	}

	public final double getXmax() {
		return xmax;
	}

	public final double getY() {
		return y;
	}

	public final void setY(double y) {
		this.y = y;
	}

}
