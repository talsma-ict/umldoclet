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
package net.sourceforge.plantuml.oregon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SmartKeyboard {

	private final Keyboard keyboard;
	private final List<String> history = new ArrayList<String>();

	public SmartKeyboard(Keyboard keyboard) {
		this.keyboard = keyboard;
	}

	public String input(Screen screen) throws NoInputException {
		final String s = keyboard.input();
		history.add(s);
		screen.print("<i>? " + s);
		return s;
	}

	public int inputInt(Screen screen) throws NoInputException {
		final String s = input(screen);
		if (s.matches("\\d+") == false) {
			screen.print("Please enter a valid number instead of " + s);
			throw new NoInputException();
		}
		return Integer.parseInt(s);
	}

	public boolean hasMore() {
		return keyboard.hasMore();
	}

	public List<String> getHistory() {
		return Collections.unmodifiableList(history);
	}

}
