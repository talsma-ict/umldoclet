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
package net.sourceforge.plantuml.descdiagram;

import java.util.Map;

import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.abel.LeafType;
import net.sourceforge.plantuml.classdiagram.AbstractEntityDiagram;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.decoration.symbol.USymbol;
import net.sourceforge.plantuml.decoration.symbol.USymbols;
import net.sourceforge.plantuml.skin.UmlDiagramType;

public class DescriptionDiagram extends AbstractEntityDiagram {

	public DescriptionDiagram(UmlSource source, Map<String, String> skinParam) {
		super(source, UmlDiagramType.DESCRIPTION, skinParam);
	}

	@Override
	public String cleanId(String id) {
		if (id == null)
			return null;
		if (id.startsWith("()"))
			id = StringUtils.trin(id.substring(2));
		if (id.startsWith(":") && id.endsWith(":/"))
			return id.substring(1, id.length() - 2);
		if (id.startsWith("(") && id.endsWith(")/"))
			return id.substring(1, id.length() - 2);
		return super.cleanId(id);
	}

	private boolean isUsecase() {
		for (Entity leaf : getEntityFactory().leafs()) {
			final LeafType type = leaf.getLeafType();
			final USymbol usymbol = leaf.getUSymbol();
			if (type == LeafType.USECASE || usymbol == getSkinParam().actorStyle().toUSymbol()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void makeDiagramReady() {
		super.makeDiagramReady();
		final LeafType defaultType = LeafType.DESCRIPTION;
		final USymbol defaultSymbol = isUsecase() ? getSkinParam().actorStyle().toUSymbol() : USymbols.INTERFACE;
		for (Entity leaf : getEntityFactory().leafs()) {
			if (leaf.getLeafType() == LeafType.STILL_UNKNOWN) {
				leaf.muteToType(defaultType, defaultSymbol);
			}
		}
	}

	@Override
	public String checkFinalError() {
		this.applySingleStrategy();
		return super.checkFinalError();
	}

}
