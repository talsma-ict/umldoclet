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
package net.sourceforge.plantuml.svek;


public enum SingleStrategy {

	SQUARE, HLINE, VLINE;

//	private Collection<Link> generateLinks(List<ILeaf> standalones) {
//		return putInSquare(standalones);
//	}

//	private Collection<Link> putInSquare(List<ILeaf> standalones) {
//		final List<Link> result = new ArrayList<Link>();
//		final LinkType linkType = new LinkType(LinkDecor.NONE, LinkDecor.NONE).getInvisible();
//		final int branch = computeBranch(standalones.size());
//		int headBranch = 0;
//		for (int i = 1; i < standalones.size(); i++) {
//			final int dist = i - headBranch;
//			final IEntity ent2 = standalones.get(i);
//			final Link link;
//			if (dist == branch) {
//				final IEntity ent1 = standalones.get(headBranch);
//				link = new Link(ent1, ent2, linkType, Display.NULL, 2);
//				headBranch = i;
//			} else {
//				final IEntity ent1 = standalones.get(i - 1);
//				link = new Link(ent1, ent2, linkType, Display.NULL, 1);
//			}
//			result.add(link);
//		}
//		return Collections.unmodifiableCollection(result);
//	}

	static int computeBranch(int size) {
		final double sqrt = Math.sqrt(size);
		final int r = (int) sqrt;
		if (r * r == size) {
			return r;
		}
		return r + 1;
	}

}
