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

import net.sourceforge.plantuml.graphic.SymbolContext;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.USymbol;

public enum ActorStyle {

	STICKMAN, STICKMAN_BUSINESS, AWESOME;

	public USymbol toUSymbol() {
		if (this == STICKMAN) {
			return USymbol.ACTOR_STICKMAN;
		} else if (this == AWESOME) {
			return USymbol.ACTOR_AWESOME;
		}
		throw new IllegalStateException();
	}

	public TextBlock getTextBlock(SymbolContext symbolContext) {
		if (this == STICKMAN) {
			return new ActorStickMan(symbolContext, false);
		} else if (this == STICKMAN_BUSINESS) {
			return new ActorStickMan(symbolContext, true);
		} else if (this == AWESOME) {
			return new ActorAwesome(symbolContext);
		}
		throw new IllegalStateException();
	}

}
