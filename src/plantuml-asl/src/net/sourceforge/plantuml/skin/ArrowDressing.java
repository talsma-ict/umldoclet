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
package net.sourceforge.plantuml.skin;

import java.util.Objects;

public class ArrowDressing {

	private final ArrowHead head;
	private final ArrowPart part;
	// private final ArrowDecoration decoration;

	public String name() {
		return toString();
	}

	@Override
	public String toString() {
		return head.name();
	}

	private ArrowDressing(ArrowHead head, ArrowPart part) {
		this.head = Objects.requireNonNull(head);
		this.part = Objects.requireNonNull(part);
	}

	public static ArrowDressing create() {
		return new ArrowDressing(ArrowHead.NONE, ArrowPart.FULL);
	}

	public ArrowDressing withHead(ArrowHead head) {
		return new ArrowDressing(head, part);
	}

	public ArrowDressing withPart(ArrowPart part) {
		return new ArrowDressing(head, part);
	}

	public ArrowHead getHead() {
		return head;
	}

	public ArrowPart getPart() {
		return part;
	}

}
