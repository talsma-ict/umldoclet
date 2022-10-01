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
package net.sourceforge.plantuml.descdiagram;

import java.util.Map;
import java.util.Objects;

import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.UmlDiagramType;
import net.sourceforge.plantuml.classdiagram.AbstractEntityDiagram;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.cucadiagram.Code;
import net.sourceforge.plantuml.cucadiagram.ILeaf;
import net.sourceforge.plantuml.cucadiagram.Ident;
import net.sourceforge.plantuml.cucadiagram.LeafType;
import net.sourceforge.plantuml.graphic.USymbol;
import net.sourceforge.plantuml.graphic.USymbols;

public class DescriptionDiagram extends AbstractEntityDiagram {

	public DescriptionDiagram(UmlSource source, Map<String, String> skinParam) {
		super(source, UmlDiagramType.DESCRIPTION, skinParam);
	}

	@Override
	public Ident cleanIdent(Ident ident) {
		String codeString = ident.getName();
		if (codeString.startsWith("[") && codeString.endsWith("]")) {
			return ident.eventuallyRemoveStartingAndEndingDoubleQuote("\"([:");
		}
		if (codeString.startsWith(":") && codeString.endsWith(":")) {
			return ident.eventuallyRemoveStartingAndEndingDoubleQuote("\"([:");
		}
		if (codeString.startsWith("()")) {
			codeString = StringUtils.trin(codeString.substring(2));
			codeString = StringUtils.eventuallyRemoveStartingAndEndingDoubleQuote(codeString);
			return ident.parent().add(Ident.empty().add(codeString, null));
		}
		return ident;
	}

	@Override
	public ILeaf getOrCreateLeaf(Ident ident, Code code, LeafType type, USymbol symbol) {
		Objects.requireNonNull(ident);
		if (type == null) {
			String codeString = code.getName();
			if (codeString.startsWith("[") && codeString.endsWith("]")) {
				final USymbol sym = getSkinParam().componentStyle().toUSymbol();
				final Ident idNewLong = ident.eventuallyRemoveStartingAndEndingDoubleQuote("\"([:");
				return getOrCreateLeafDefault(idNewLong, idNewLong.toCode(this), LeafType.DESCRIPTION, sym);
			}
			if (codeString.startsWith(":") && codeString.endsWith(":")) {
				final Ident idNewLong = ident.eventuallyRemoveStartingAndEndingDoubleQuote("\"([:");
				return getOrCreateLeafDefault(idNewLong, idNewLong.toCode(this), LeafType.DESCRIPTION,
						getSkinParam().actorStyle().toUSymbol());
			}
			if (codeString.startsWith("()")) {
				codeString = StringUtils.trin(codeString.substring(2));
				codeString = StringUtils.eventuallyRemoveStartingAndEndingDoubleQuote(codeString);
				final Ident idNewLong = buildLeafIdent(codeString);
				final Code code99 = this.V1972() ? idNewLong : buildCode(codeString);
				return getOrCreateLeafDefault(idNewLong, code99, LeafType.DESCRIPTION, USymbols.INTERFACE);
			}
			final String tmp4 = StringUtils.eventuallyRemoveStartingAndEndingDoubleQuote(code.getName(), "\"([:");
			final Ident idNewLong = ident.eventuallyRemoveStartingAndEndingDoubleQuote("\"([:");
			code = this.V1972() ? idNewLong : buildCode(tmp4);
			return getOrCreateLeafDefault(idNewLong, code, LeafType.STILL_UNKNOWN, symbol);
		}
		return getOrCreateLeafDefault(ident, code, type, symbol);
	}

	private boolean isUsecase() {
		for (ILeaf leaf : getLeafsvalues()) {
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
		for (ILeaf leaf : getLeafsvalues()) {
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
