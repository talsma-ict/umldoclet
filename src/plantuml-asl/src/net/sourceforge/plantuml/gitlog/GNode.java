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
package net.sourceforge.plantuml.gitlog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.cucadiagram.Display;

public class GNode {

	private final List<GNode> up = new ArrayList<>();
	private final List<GNode> down = new ArrayList<>();
	private final List<String> texts = new ArrayList<>();

	private String comment;

	public void addText(String text) {
		this.texts.add(text);
	}

	public boolean isTop() {
		return up.size() == 0;
	}

	public final String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public static void link(GNode n1, GNode n2) {
		n1.down.add(n2);
		n2.up.add(n1);
	}

	@Override
	public String toString() {
		return texts.toString();
	}

	public Display getDisplay() {
		return Display.create(texts);
	}

	public Collection<GNode> getDowns() {
		return Collections.unmodifiableCollection(down);
	}

	public boolean canEatTheNextOne() {
		if (up.size() != 1) {
			return false;
		}
		if (down.size() != 1) {
			return false;
		}
		final GNode next = down.get(0);
		if (next.up.size() != 1) {
			return false;
		}
		if (next.down.size() != 1) {
			return false;
		}
		return true;
	}

	public GNode eatTheNextOne() {
		if (canEatTheNextOne() == false) {
			throw new IllegalStateException();
		}
		final GNode removed = down.get(0);
		final GNode newNext = removed.down.get(0);
		this.texts.addAll(removed.texts);
		this.down.set(0, newNext);
		if (newNext.up.remove(removed) == false) {
			throw new IllegalStateException();
		}
		newNext.up.add(this);
		return removed;
	}

}
