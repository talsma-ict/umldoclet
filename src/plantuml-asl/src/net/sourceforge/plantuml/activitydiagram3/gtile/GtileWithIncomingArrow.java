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
package net.sourceforge.plantuml.activitydiagram3.gtile;

import java.util.Collection;
import java.util.Collections;

import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class GtileWithIncomingArrow extends GtileWithMargin implements Gtile {

	public GtileWithIncomingArrow(AbstractGtileRoot orig, double north) {
		super(orig, north, 0, 0);
		if (orig instanceof GtileEmpty) {
			System.err.println("Warning 1");
		}

	}

	@Override
	public Collection<GConnection> getInnerConnections() {
		final GConnection arrow = new GConnectionVerticalDown(UTranslate.dy(0), orig.getGPoint(GPoint.NORTH_HOOK),
				UTranslate.dy(north), orig.getGPoint(GPoint.NORTH_HOOK), TextBlockUtils.EMPTY_TEXT_BLOCK);
		return Collections.singletonList(arrow);
	}

}
