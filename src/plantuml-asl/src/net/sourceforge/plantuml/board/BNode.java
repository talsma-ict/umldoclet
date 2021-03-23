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
package net.sourceforge.plantuml.board;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BNode {

	private final String name;
	private final int stage;
	private int x = -1;
	private BNode parent;
	private final List<BNode> children = new ArrayList<BNode>();

	public BNode(int stage, String name) {
		this.name = name;
		this.stage = stage;
	}

	public void addChild(BNode child) {
		if (child.stage <= this.stage) {
			throw new IllegalArgumentException();
		}
		this.children.add(child);
		if (child.parent != null) {
			throw new IllegalArgumentException();
		}
		child.parent = this;
	}

	public final String getName() {
		return name;
	}

	public final int getStage() {
		return stage;
	}

	public final BNode getParent() {
		return parent;
	}

	@Override
	public String toString() {
		return name + "(" + stage + ") [" + x + "]";
	}

	public void computeX(AtomicInteger count) {
		this.x = count.intValue();
		for (int i = 0; i < children.size(); i++) {
			final BNode child = children.get(i);
			if (i > 0) {
				count.addAndGet(1);
			}
			child.computeX(count);
		}
	}

	public void initBarray(BArray array) {
		array.put(this);
		for (BNode child : children) {
			child.initBarray(array);
		}

	}

	public final int getX() {
		return x;
	}

}
