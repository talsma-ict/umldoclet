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
package net.sourceforge.plantuml.dot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.abel.EntityFactory;
import net.sourceforge.plantuml.abel.EntityPortion;
import net.sourceforge.plantuml.abel.Link;
import net.sourceforge.plantuml.cucadiagram.GroupHierarchy;
import net.sourceforge.plantuml.cucadiagram.PortionShower;
import net.sourceforge.plantuml.skin.Pragma;
import net.sourceforge.plantuml.skin.UmlDiagramType;
import net.sourceforge.plantuml.style.ISkinParam;
import net.sourceforge.plantuml.svek.DotMode;

final public class DotData implements PortionShower {

	final private List<Link> links;
	final private Collection<Entity> leafs;
	final private UmlDiagramType umlDiagramType;
	final private ISkinParam skinParam;
	// final private Rankdir rankdir;
	final private GroupHierarchy groupHierarchy;
	final private Entity topParent;
	final private PortionShower portionShower;
	final private boolean isHideEmptyDescriptionForState;
	final private DotMode dotMode;
	final private String namespaceSeparator;
	final private Pragma pragma;

	private final EntityFactory entityFactory;

	public EntityFactory getEntityFactory() {
		return entityFactory;
	}

	public DotData(Entity topParent, List<Link> links, Collection<Entity> leafs, UmlDiagramType umlDiagramType,
			ISkinParam skinParam, GroupHierarchy groupHierarchy, PortionShower portionShower,
			EntityFactory entityFactory, boolean isHideEmptyDescriptionForState, DotMode dotMode,
			String namespaceSeparator, Pragma pragma) {
		this.namespaceSeparator = namespaceSeparator;
		this.pragma = pragma;
		this.topParent = Objects.requireNonNull(topParent);
		this.dotMode = dotMode;
		this.isHideEmptyDescriptionForState = isHideEmptyDescriptionForState;
		this.links = links;
		this.leafs = leafs;
		this.umlDiagramType = umlDiagramType;
		this.skinParam = skinParam;
		// this.rankdir = rankdir;
		this.groupHierarchy = groupHierarchy;
		this.portionShower = portionShower;
		this.entityFactory = entityFactory;
	}

	public DotData(Entity topParent, List<Link> links, Collection<Entity> leafs, UmlDiagramType umlDiagramType,
			ISkinParam skinParam, GroupHierarchy groupHierarchy, EntityFactory entityFactory,
			boolean isHideEmptyDescriptionForState, DotMode dotMode, String namespaceSeparator, Pragma pragma) {
		this(topParent, links, leafs, umlDiagramType, skinParam, groupHierarchy, new PortionShower() {
			public boolean showPortion(EntityPortion portion, Entity entity) {
				return true;
			}
		}, entityFactory, isHideEmptyDescriptionForState, dotMode, namespaceSeparator, pragma);
	}

	public UmlDiagramType getUmlDiagramType() {
		return umlDiagramType;
	}

	public ISkinParam getSkinParam() {
		return skinParam;
	}

	public GroupHierarchy getGroupHierarchy() {
		return groupHierarchy;
	}

	public List<Link> getLinks() {
		return links;
	}

	public Collection<Entity> getLeafs() {
		return leafs;
	}

	public final Entity getTopParent() {
		return topParent;
	}

	public boolean isEmpty(Entity g) {
		return groupHierarchy.isEmpty(g);
	}

	public boolean showPortion(EntityPortion portion, Entity entity) {
		return portionShower.showPortion(portion, entity);
	}

	public Entity getRootGroup() {
		return entityFactory.getRootGroup();
	}

	public boolean isDegeneratedWithFewEntities(int nb) {
		return entityFactory.groups().size() == 0 && getLinks().size() == 0 && getLeafs().size() == nb;
	}

	public final boolean isHideEmptyDescriptionForState() {
		return isHideEmptyDescriptionForState;
	}

	public final DotMode getDotMode() {
		return dotMode;
	}

	public final String getNamespaceSeparator() {
		return namespaceSeparator;
	}

	public Pragma getPragma() {
		return pragma;
	}

	public void removeIrrelevantSametail() {
		final Map<String, Integer> sametails = new HashMap<String, Integer>();
		for (Link link : links) {
			if (link.getType().getDecor2().isExtendsLike()) {
				link.setSametail(link.getEntity1().getUid());
			}
			final String sametail = link.getSametail();
			if (sametail == null) {
				continue;
			}
			final Integer value = sametails.get(sametail);
			sametails.put(sametail, value == null ? 1 : value + 1);
		}
		final Collection<String> toremove = new HashSet<>();
		final int limit = skinParam.groupInheritance();
		for (Map.Entry<String, Integer> ent : sametails.entrySet()) {
			final String key = ent.getKey();
			if (ent.getValue() < limit) {
				toremove.add(key);
			} else {
				final List<Link> some = new ArrayList<>();
				for (Link link : links) {
					if (key.equals(link.getSametail())) {
						some.add(link);
					}
				}
				final Entity leaf = getLeaf(key);
				final Neighborhood neighborhood = new Neighborhood(leaf, some, getLinksOfThisLeaf(leaf));
				leaf.setNeighborhood(neighborhood);
			}
		}

		for (Link link : links) {
			final String sametail = link.getSametail();
			if (sametail == null) {
				continue;
			}
			if (toremove.contains(sametail)) {
				link.setSametail(null);
			}
		}
	}

	private List<Link> getLinksOfThisLeaf(Entity leaf) {
		final List<Link> result = new ArrayList<>();
		for (Link link : links) {
			if (link.contains(leaf)) {
				result.add(link);
			}
		}
		return result;
	}

	private Entity getLeaf(String key) {
		for (Entity entity : leafs) {
			if (entity.getUid().equals(key)) {
				return entity;
			}
		}
		return null;

	}

}
