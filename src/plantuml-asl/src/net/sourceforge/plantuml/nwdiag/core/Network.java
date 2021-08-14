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

import net.sourceforge.plantuml.nwdiag.next.NStage;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class Network {

	private final String name;
	private HColor color;
	private boolean visible = true;
	private String ownAdress;
	private double y;
	private boolean fullWidth;
	private final NStage nstage;

	@Override
	public String toString() {
		return name;
	}

	public Network(NStage nstage, String name) {
		this.name = name;
		this.nstage = nstage;
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

	public final void goInvisible() {
		this.visible = false;
	}

	public final boolean isVisible() {
		return visible;
	}

	public final double getY() {
		return y;
	}

	public final void setY(double y) {
		this.y = y;
	}

	public void setFullWidth(boolean fullWidth) {
		this.fullWidth = fullWidth;
	}

	public final boolean isFullWidth() {
		return fullWidth;
	}

	public final NStage getNstage() {
		return nstage;
	}

}
