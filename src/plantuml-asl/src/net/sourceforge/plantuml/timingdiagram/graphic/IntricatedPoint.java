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
package net.sourceforge.plantuml.timingdiagram.graphic;

import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.geom.XPoint2D;

public class IntricatedPoint {
    // ::remove folder when __HAXE__

	private final XPoint2D pta;
	private final XPoint2D ptb;

	public IntricatedPoint(XPoint2D pta, XPoint2D ptb) {
		this.pta = pta;
		this.ptb = ptb;
	}

	public final XPoint2D getPointA() {
		return pta;
	}

	public final XPoint2D getPointB() {
		return ptb;
	}

	public IntricatedPoint translated(UTranslate translate) {
		return new IntricatedPoint(translate.getTranslated(pta), translate.getTranslated(ptb));
	}

}
