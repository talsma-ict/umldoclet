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
package net.sourceforge.plantuml.svek;

import net.sourceforge.plantuml.graphic.StringBounder;

public class ShapePseudoImpl implements IShapePseudo {

	private final String uid;
	private final double width;
	private final double height;

	public ShapePseudoImpl(String uid, double width, double height) {
		this.uid = uid;
		this.width = width;
		this.height = height;
	}

	public String getUid() {
		return uid;
	}

	public void appendShape(StringBuilder sb, StringBounder stringBounder) {
		sb.append(uid + " [shape=rect,label=\"\"");
		sb.append(",width=" + SvekUtils.pixelToInches(width));
		sb.append(",height=" + SvekUtils.pixelToInches(height));
		sb.append("];");
	}

	public double getMaxWidthFromLabelForEntryExit(StringBounder stringBounder) {
		throw new UnsupportedOperationException();
	}

}
