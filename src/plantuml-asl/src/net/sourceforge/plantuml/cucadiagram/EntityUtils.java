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
package net.sourceforge.plantuml.cucadiagram;


public abstract class EntityUtils {

	public static boolean groupRoot(IGroup g) {
		if (g == null) {
			throw new IllegalStateException();
		}
		return g instanceof GroupRoot;
	}

	private static boolean isParent(IGroup groupToBeTested, IGroup parentGroup) {
		if (groupToBeTested.isGroup() == false) {
			// Very strange!
			return false;
		}
		if (groupToBeTested.isGroup() == false) {
			throw new IllegalArgumentException();
		}
		while (EntityUtils.groupRoot(groupToBeTested) == false) {
			if (groupToBeTested == parentGroup) {
				return true;
			}
			groupToBeTested = groupToBeTested.getParentContainer();
			if (groupToBeTested.isGroup() == false) {
				throw new IllegalStateException();
			}
		}
		return false;
	}

	public static boolean isPureInnerLink12(IGroup group, Link link) {
		if (group.isGroup() == false) {
			throw new IllegalArgumentException();
		}
		final IEntity e1 = link.getEntity1();
		final IEntity e2 = link.getEntity2();
		final IGroup group1 = e1.getParentContainer();
		final IGroup group2 = e2.getParentContainer();
		if (isParent(group1, group) && isParent(group2, group)) {
			return true;
		}
		return false;
	}

	public static boolean isPureInnerLink3(IGroup group, Link link) {
		if (group.isGroup() == false) {
			throw new IllegalArgumentException();
		}
		final IEntity e1 = link.getEntity1();
		final IEntity e2 = link.getEntity2();
		final IGroup group1 = e1.getParentContainer();
		final IGroup group2 = e2.getParentContainer();
		if (isParent(group2, group) == isParent(group1, group)) {
			return true;
		}
		return false;
	}
}
