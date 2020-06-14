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
package net.sourceforge.plantuml;

public class LineLocationImpl implements LineLocation {

	private final String desc;
	private final int position;
	private final LineLocation parent;

	@Override
	public String toString() {
		return desc + " : " + position;
	}

	public LineLocationImpl(String desc, LineLocation parent) {
		this(desc, parent, -1);
	}

	private LineLocationImpl(String desc, LineLocation parent, int position) {
		if (desc == null) {
			throw new IllegalArgumentException();
		}
		this.parent = parent;
		this.desc = desc;
		this.position = position;
	}

	public LineLocationImpl oneLineRead() {
		return new LineLocationImpl(desc, parent, position + 1);
	}

	public int getPosition() {
		return position;
	}

	public String getDescription() {
		return desc;
	}

	public LineLocation getParent() {
		return parent;
	}

	private boolean isStandardLibrary() {
		return desc.startsWith("<");
	}

	public int compareTo(LineLocation other) {
		final LineLocationImpl other2 = (LineLocationImpl) other;
		if (this.isStandardLibrary() && other2.isStandardLibrary() == false) {
			return -1;
		}
		if (this.isStandardLibrary() == false && other2.isStandardLibrary()) {
			return 1;
		}
		return this.position - other2.position;
	}

}
