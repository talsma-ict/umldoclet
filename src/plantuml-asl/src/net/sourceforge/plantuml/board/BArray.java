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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BArray implements Iterable<BNode> {

	private final Map<String, BNode> data = new HashMap<String, BNode>();
	private int maxX;
	private int maxY;

	public void put(BNode node) {
		final String key = getKey(node.getX(), node.getStage());
		if (data.containsKey(key)) {
			throw new IllegalArgumentException();
		}
		data.put(key, node);
		this.maxX = Math.max(this.maxX, node.getX());
		this.maxY = Math.max(this.maxY, node.getStage());
	}

	public BNode getCell(int x, int y) {
		final String key = getKey(x, y);
		return data.get(key);
	}

	private String getKey(int x, int y) {
		return "" + x + ";" + y;
	}

	public Iterator<BNode> iterator() {
		return Collections.unmodifiableCollection(data.values()).iterator();
	}

	public final int getMaxX() {
		return maxX;
	}

	public final int getMaxY() {
		return maxY;
	}

}
