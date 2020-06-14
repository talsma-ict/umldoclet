/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
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
package net.sourceforge.plantuml.hector;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.plantuml.cucadiagram.Link;

public class PinFactory {

	private final Map<Object, Pin> pins = new HashMap<Object, Pin>();

	Pin create(Object userData) {
		return create(Integer.MAX_VALUE, userData);
	}

	public Pin create(int row, Object userData) {
		if (userData == null) {
			return new Pin(row, userData);
		}
		Pin result = pins.get(userData);
		if (result == null) {
			result = new Pin(row, userData);
			pins.put(userData, result);
		}
		return result;
	}

	public PinLink createPinLink(Link link) {
		final PinLink result = new PinLink(create(link.getEntity1()), create(link.getEntity2()), link.getLength(), link);
		return result;
	}

}
