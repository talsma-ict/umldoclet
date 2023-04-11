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
package net.sourceforge.plantuml.abel;

public abstract class EntityUtils {

	private static boolean isParent(Entity groupToBeTested, Entity parentGroup) {
		if (groupToBeTested.isGroup() == false)
			// Very strange!
			return false;

		if (groupToBeTested.isGroup() == false)
			throw new IllegalArgumentException();

		while (groupToBeTested.isRoot() == false) {
			if (groupToBeTested == parentGroup)
				return true;

			groupToBeTested = groupToBeTested.getParentContainer();
			if (groupToBeTested.isGroup() == false)
				return false;
			// throw new IllegalStateException();

		}
		return false;
	}

	public static boolean isPureInnerLink12(Entity group, Link link) {
		if (group.isGroup() == false)
			throw new IllegalArgumentException();

		final Entity e1 = link.getEntity1();
		final Entity e2 = link.getEntity2();
		final Entity group1 = e1.getParentContainer();
		final Entity group2 = e2.getParentContainer();

		if (isParent(group1, group) && isParent(group2, group))
			return true;

		return false;
	}

	public static boolean isPureInnerLink3(Entity group, Link link) {
		if (group.isGroup() == false)
			throw new IllegalArgumentException();

		final Entity e1 = link.getEntity1();
		final Entity e2 = link.getEntity2();
		final Entity group1 = e1.getParentContainer();
		final Entity group2 = e2.getParentContainer();
		if (isParent(group2, group) == isParent(group1, group))
			return true;

		return false;
	}
}
