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
package net.sourceforge.plantuml.tim;

import net.sourceforge.plantuml.StringLocated;

public class EaterExceptionLocated extends Exception {

	private final String message;
	private final StringLocated location;

	private EaterExceptionLocated(String message, StringLocated location) {
		this.message = message;
		this.location = location;
	}

	public static EaterExceptionLocated located(String message, StringLocated location) {
		if (location == null) {
			throw new IllegalArgumentException();
		}
		return new EaterExceptionLocated(message, location);
	}

	public final String getMessage() {
		return message;
	}

	public final StringLocated getLocation() {
		return location;
	}

}
