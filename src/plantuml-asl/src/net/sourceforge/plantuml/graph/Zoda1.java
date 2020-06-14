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
package net.sourceforge.plantuml.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @deprecated
 * 
 */
public class Zoda1 {

	private final Map<String, ANodeImpl> nodes = new LinkedHashMap<String, ANodeImpl>();
	private final List<ALink> links = new ArrayList<ALink>();

	public ANodeImpl getNode(String code) {
		ANodeImpl result = nodes.get(code);
		if (result == null) {
			result = new ANodeImpl(code);
			nodes.put(code, result);
		}
		return result;
	}

	public ANodeImpl getExistingNode(String code) {
		return nodes.get(code);
	}

	public List<ALink> getLinks() {
		return Collections.unmodifiableList(links);
	}

	public List<ANode> getNodes() {
		return Collections.unmodifiableList(new ArrayList<ANode>(nodes.values()));
	}

	public void addLink(String link) {
		final LinkString l = new LinkString(link);
		final ANodeImpl n1 = getNode(l.getNode1());
		final ANodeImpl n2 = getNode(l.getNode2());
		links.add(new ALinkImpl(n1, n2, 1, null));
	}

	public void computeRows() {
		getNodes().get(0).setRow(0);

		for (int i = 0; i < links.size(); i++) {
			oneStep1();
			oneStep2();
		}

		removeUnplacedNodes();
	}

	private void removeUnplacedNodes() {
		for (final Iterator<ANodeImpl> it = nodes.values().iterator(); it.hasNext();) {
			final ANodeImpl n = it.next();
			if (n.getRow() == Integer.MIN_VALUE) {
				removeLinksOf(n);
				it.remove();
			}
		}

	}

	private void removeLinksOf(ANodeImpl n) {
		for (final Iterator<ALink> it = links.iterator(); it.hasNext();) {
			final ALink link = it.next();
			if (link.getNode1() == n || link.getNode2() == n) {
				it.remove();
			}
		}

	}

	public int getRowMax() {
		int max = 0;
		for (ANode n : getNodes()) {
			if (n.getRow() == Integer.MIN_VALUE) {
				return Integer.MIN_VALUE;
			}
			if (n.getRow() > max) {
				max = n.getRow();
			}
		}
		return max;
	}

	private void oneStep1() {
		for (ALink link : links) {
			final ANode n1 = link.getNode1();
			if (n1.getRow() == Integer.MIN_VALUE) {
				continue;
			}
			final ANode n2 = link.getNode2();
			if (n2.getRow() == Integer.MIN_VALUE) {
				n2.setRow(n1.getRow() + 1);
			} else if (n2.getRow() < n1.getRow() + 1) {
				n2.setRow(n1.getRow() + 1);
			}
		}
	}

	private void oneStep2() {
		for (ALink link : links) {
			final ANode n1 = link.getNode1();
			final ANode n2 = link.getNode2();
			if (n1.getRow() == Integer.MIN_VALUE && n2.getRow() != Integer.MIN_VALUE) {
				if (n2.getRow() == 0) {
					allDown();
				}
				final int row = n2.getRow() - 1;
				if (row == -1) {
					throw new UnsupportedOperationException();
				}
				n1.setRow(row);
			}
		}
	}

	private void allDown() {
		for (ANodeImpl n : nodes.values()) {
			if (n.getRow() != Integer.MIN_VALUE) {
				n.setRow(n.getRow() + 1);
			}
		}

	}

}
