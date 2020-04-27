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
package net.sourceforge.plantuml.graph;

public class ALinkImpl implements ALink {

	private final ANode node1;
	private final ANode node2;
	private final Object userData;
	private final int diffHeight;

	@Override
	public String toString() {
		return "" + node1 + " -> " + node2;
	}

	public ALinkImpl(ANode n1, ANode n2, int diffHeight, Object userData) {
		this.node1 = n1;
		this.node2 = n2;
		this.userData = userData;
		this.diffHeight = diffHeight;
	}

	public int getDiffHeight() {
		return diffHeight;
	}

	public ANode getNode1() {
		return node1;
	}

	public ANode getNode2() {
		return node2;
	}

	public final Object getUserData() {
		return userData;
	}

}
