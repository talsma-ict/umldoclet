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
package net.sourceforge.plantuml.descdiagram;

import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.UmlDiagramType;
import net.sourceforge.plantuml.classdiagram.AbstractEntityDiagram;
import net.sourceforge.plantuml.cucadiagram.Code;
import net.sourceforge.plantuml.cucadiagram.ILeaf;
import net.sourceforge.plantuml.cucadiagram.LeafType;
import net.sourceforge.plantuml.graphic.USymbol;

public class DescriptionDiagram extends AbstractEntityDiagram {
	
	public DescriptionDiagram(ISkinSimple skinParam) {
		super(skinParam);
	}


	@Override
	public ILeaf getOrCreateLeaf(Code code, LeafType type, USymbol symbol) {
		if (getNamespaceSeparator() != null) {
			code = code.withSeparator(getNamespaceSeparator());
		}
		if (getNamespaceSeparator() != null && code.getFullName().contains(getNamespaceSeparator())) {
			// System.err.println("code=" + code);
			final Code fullyCode = code;
			// final String namespace = fullyCode.getNamespace(getLeafs());
			// System.err.println("namespace=" + namespace);
		}
		if (type == null) {
			String code2 = code.getFullName();
			if (code2.startsWith("[") && code2.endsWith("]")) {
				final USymbol sym = getSkinParam().useUml2ForComponent() ? USymbol.COMPONENT2 : USymbol.COMPONENT1;
				return getOrCreateLeafDefault(code.eventuallyRemoveStartingAndEndingDoubleQuote("\"([:"),
						LeafType.DESCRIPTION, sym);
			}
			if (code2.startsWith(":") && code2.endsWith(":")) {
				return getOrCreateLeafDefault(code.eventuallyRemoveStartingAndEndingDoubleQuote("\"([:"),
						LeafType.DESCRIPTION, USymbol.ACTOR);
			}
			if (code2.startsWith("()")) {
				code2 = StringUtils.trin(code2.substring(2));
				code2 = StringUtils.eventuallyRemoveStartingAndEndingDoubleQuote(code2);
				return getOrCreateLeafDefault(Code.of(code2), LeafType.DESCRIPTION, USymbol.INTERFACE);
			}
			code = code.eventuallyRemoveStartingAndEndingDoubleQuote("\"([:");
			return getOrCreateLeafDefault(code, LeafType.STILL_UNKNOWN, symbol);
		}
		return getOrCreateLeafDefault(code, type, symbol);
	}

	// @Override
	// public ILeaf createLeaf(Code code, List<? extends CharSequence> display, LeafType type) {
	// if (type != LeafType.COMPONENT) {
	// return super.createLeaf(code, display, type);
	// }
	// code = code.getFullyQualifiedCode(getCurrentGroup());
	// if (super.leafExist(code)) {
	// throw new IllegalArgumentException("Already known: " + code);
	// }
	// return createEntityWithNamespace(code, display, type);
	// }

	// private ILeaf createEntityWithNamespace(Code fullyCode, List<? extends CharSequence> display, LeafType type) {
	// IGroup group = getCurrentGroup();
	// final String namespace = fullyCode.getNamespace(getLeafs());
	// if (namespace != null && (EntityUtils.groupRoot(group) || group.getCode().equals(namespace) == false)) {
	// group = getOrCreateGroupInternal(Code.of(namespace), StringUtils.getWithNewlines(namespace), namespace,
	// GroupType.PACKAGE, getRootGroup());
	// }
	// return createLeafInternal(fullyCode,
	// display == null ? StringUtils.getWithNewlines(fullyCode.getShortName(getLeafs())) : display, type,
	// group);
	// }

	private boolean isUsecase() {
		for (ILeaf leaf : getLeafsvalues()) {
			final LeafType type = leaf.getLeafType();
			final USymbol usymbol = leaf.getUSymbol();
			if (type == LeafType.USECASE || usymbol == USymbol.ACTOR) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void makeDiagramReady() {
		super.makeDiagramReady();
		final LeafType defaultType = isUsecase() ? LeafType.DESCRIPTION : LeafType.DESCRIPTION;
		final USymbol defaultSymbol = isUsecase() ? USymbol.ACTOR : USymbol.INTERFACE;
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

	@Override
	public UmlDiagramType getUmlDiagramType() {
		return UmlDiagramType.DESCRIPTION;
	}

}
