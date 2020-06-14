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
package net.sourceforge.plantuml.tim;

public class ConditionalContext {

	private boolean isTrue;
	private boolean hasBeenBurn;

	private ConditionalContext(boolean isTrue) {
		this.isTrue = isTrue;
		if (this.isTrue) {
			hasBeenBurn = true;
		}
	}

	public static ConditionalContext fromValue(boolean isTrue) {
		return new ConditionalContext(isTrue);
	}

	public boolean conditionIsOkHere() {
		return isTrue;
	}

	public void enteringElseIf() {
		this.isTrue = false;
	}

	public void nowInElse() {
		this.isTrue = !hasBeenBurn;
	}

	public void nowInSomeElseIf() {
		this.isTrue = true;
		this.hasBeenBurn = true;
	}

	public final boolean hasBeenBurn() {
		return hasBeenBurn;
	}

	public final void setHasBeenBurn(boolean hasBeenBurn) {
		this.hasBeenBurn = hasBeenBurn;
	}

}
