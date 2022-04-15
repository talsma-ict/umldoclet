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
package net.sourceforge.plantuml.tim.iterator;

import java.util.List;

import net.sourceforge.plantuml.StringLocated;
import net.sourceforge.plantuml.tim.EaterException;

public class CodeIteratorImpl implements CodeIterator {

	private final List<StringLocated> list;
	private int current = 0;
	private int countJump = 0;

	static class Position implements CodePosition {
		final int pos;

		Position(int pos) {
			this.pos = pos;
		}

//		@Override
//		public String toString() {
//			return "-->" + list.get(pos);
//		}
	}

	public CodeIteratorImpl(List<StringLocated> list) {
		this.list = list;
	}

	public StringLocated peek() {
		if (current == list.size()) {
			return null;
		}
		if (current > list.size()) {
			throw new IllegalStateException();
		}
		return list.get(current);
	}

	public void next() {
		if (current >= list.size()) {
			throw new IllegalStateException();
		}
		assert current < list.size();
		current++;
		assert current <= list.size();
	}

	public CodePosition getCodePosition() {
		return new Position(current);
	}

	public void jumpToCodePosition(CodePosition newPosition) throws EaterException {
		this.countJump++;
		if (this.countJump > 999) {
			throw EaterException.unlocated("Infinite loop?");
		}
		final Position pos = (Position) newPosition;
		this.current = pos.pos;

	}

}
