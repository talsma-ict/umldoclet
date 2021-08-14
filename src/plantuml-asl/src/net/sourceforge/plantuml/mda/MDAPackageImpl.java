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
package net.sourceforge.plantuml.mda;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import net.sourceforge.plantuml.api.mda.option2.MDAEntity;
import net.sourceforge.plantuml.api.mda.option2.MDAPackage;
import net.sourceforge.plantuml.cucadiagram.GroupRoot;
import net.sourceforge.plantuml.cucadiagram.IGroup;
import net.sourceforge.plantuml.cucadiagram.ILeaf;

public class MDAPackageImpl implements MDAPackage {

	private final Collection<MDAEntity> entities = new ArrayList<>();
	private final IGroup group;

	public MDAPackageImpl(IGroup group) {
		this.group = group;
		for (ILeaf leaf : group.getLeafsDirect()) {
			entities.add(new MDAEntityImpl(leaf));
		}
	}

	public Collection<MDAEntity> getEntities() {
		return Collections.unmodifiableCollection(entities);
	}

	public String getName() {
		if (group instanceof GroupRoot) {
			return "";
		}
		return group.getCodeGetName();
	}

}
