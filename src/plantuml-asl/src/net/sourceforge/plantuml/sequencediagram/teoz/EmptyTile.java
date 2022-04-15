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
package net.sourceforge.plantuml.sequencediagram.teoz;

import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.real.Real;
import net.sourceforge.plantuml.sequencediagram.Event;
import net.sourceforge.plantuml.sequencediagram.Participant;
import net.sourceforge.plantuml.ugraphic.UGraphic;

public class EmptyTile extends AbstractTile implements Tile {

	private final double height;
	private final Tile position;

	public EmptyTile(double height, Tile position) {
		super(((AbstractTile) position).getStringBounder());
		this.height = height;
		this.position = position;
	}

	public void drawU(UGraphic ug) {
	}

	public double getPreferredHeight() {
		return height;
	}

	public void addConstraints() {
	}

	public Real getMinX() {
		return position.getMinX();
	}

	public Real getMaxX() {
		return position.getMaxX();
	}

	public Event getEvent() {
		return new Event() {
			public boolean dealWith(Participant someone) {
				return false;
			}

			public Url getUrl() {
				return null;
			}

			public boolean hasUrl() {
				return false;
			}

			public boolean isParallel() {
				return false;
			}
		};
	}

}
