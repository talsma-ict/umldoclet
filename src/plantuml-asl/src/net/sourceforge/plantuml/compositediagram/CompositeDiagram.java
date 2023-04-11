/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
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
package net.sourceforge.plantuml.compositediagram;

import java.util.Map;

import net.sourceforge.plantuml.classdiagram.AbstractEntityDiagram;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.skin.UmlDiagramType;

public class CompositeDiagram extends AbstractEntityDiagram {
	// ::remove folder when __HAXE__

	public CompositeDiagram(UmlSource source, Map<String, String> skinParam) {
		super(source, UmlDiagramType.COMPOSITE, skinParam);
	}

//	@Override
//	protected IEntity getOrCreateLeaf2(Quark ident, Quark code, LeafType type, USymbol symbol) {
//		Objects.requireNonNull(ident);
//		// final Ident idNewLong = buildLeafIdent(id);
//		if (type == null) {
//			if (isGroup(code.getName())) {
//				return getGroup(code.getName());
//			}
//			return reallyCreateLeaf(ident, Display.getWithNewlines(code.getName()), LeafType.BLOCK, symbol);
//		}
//		return reallyCreateLeaf(ident, Display.getWithNewlines(code.getName()), type, symbol);
//	}

}
