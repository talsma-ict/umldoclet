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
package net.sourceforge.plantuml.skin;

import java.util.LinkedHashMap;
import java.util.Map;

public class Pragma {

	private final Map<String, String> values = new LinkedHashMap<String, String>();

	public void define(String name, String value) {
		values.put(name, value);
	}

	public boolean isDefine(String name) {
		return values.containsKey(name);
	}

	public void undefine(String name) {
		values.remove(name);
	}

	public String getValue(String name) {
		return values.get(name);
	}

	public boolean horizontalLineBetweenDifferentPackageAllowed() {
		return isDefine("horizontallinebetweendifferentpackageallowed");
	}

	public boolean backToLegacyPackage() {
		return isDefine("backtolegacypackage");
	}

	public boolean useNewPackage() {
		return isDefine("usenewpackage");
	}

	private boolean isTrue(final String s) {
		return "true".equalsIgnoreCase(s) || "on".equalsIgnoreCase(s);
	}

	private boolean isFalse(final String s) {
		return "false".equalsIgnoreCase(s) || "off".equalsIgnoreCase(s);
	}

	public boolean useVerticalIf() {
		return isTrue(getValue("useverticalif"));
	}

	public boolean useTeozLayout() {
		return isTrue(getValue("teoz"));
	}

	public boolean useKermor() {
		return isTrue(getValue("kermor"));
	}

	public boolean useIntermediatePackages() {
		return !isFalse(getValue("useintermediatepackages"));
	}

}
