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
package net.sourceforge.plantuml.baraye;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.cucadiagram.Bodier;
import net.sourceforge.plantuml.cucadiagram.BodierJSon;
import net.sourceforge.plantuml.cucadiagram.BodierMap;
import net.sourceforge.plantuml.cucadiagram.BodyFactory;
import net.sourceforge.plantuml.cucadiagram.Code;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.GroupRoot;
import net.sourceforge.plantuml.cucadiagram.GroupType;
import net.sourceforge.plantuml.cucadiagram.HideOrShow2;
import net.sourceforge.plantuml.cucadiagram.Ident;
import net.sourceforge.plantuml.cucadiagram.LeafType;
import net.sourceforge.plantuml.cucadiagram.Link;
import net.sourceforge.plantuml.cucadiagram.Stereotag;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.cucadiagram.Together;
import net.sourceforge.plantuml.cucadiagram.entity.IEntityFactory;
import net.sourceforge.plantuml.graphic.USymbol;
import net.sourceforge.plantuml.skin.VisibilityModifier;

public final class EntityFactory implements IEntityFactory {

	private final Map<String, ILeaf> leafsByCode;
	private final Map<String, IGroup> groupsByCode;

	/* private */public final Map<Ident, ILeaf> leafs2 = new LinkedHashMap<Ident, ILeaf>();
	/* private */public final Map<Ident, IGroup> groups2 = new LinkedHashMap<Ident, IGroup>();

	private final List<Link> links = new ArrayList<>();

	private int rawLayout;

	private final IGroup rootGroup = new GroupRoot(this);

	private final List<HideOrShow2> hides2;
	private final List<HideOrShow2> removed;
	/* private */ final public CucaDiagram namespaceSeparator;
	private Map<IGroup, ILeaf> emptyGroupsAsNode = new HashMap<IGroup, ILeaf>();

	public ILeaf getLeafForEmptyGroup(IGroup g) {
		return emptyGroupsAsNode.get(g);
	}

	public ILeaf createLeafForEmptyGroup(IGroup g, ISkinParam skinParam) {
		final ILeaf folder = this.createLeaf(null, g.getIdent(), g.getCode(), g.getDisplay(), LeafType.EMPTY_PACKAGE,
				g.getParentContainer(), null, this.namespaceSeparator.getNamespaceSeparator());
		((EntityImp) folder).setOriginalGroup(g);
		final USymbol symbol = g.getUSymbol();
		folder.setUSymbol(symbol);
		folder.setStereotype(g.getStereotype());
		folder.setColors(g.getColors());
		if (g.getUrl99() != null)
			folder.addUrl(g.getUrl99());
		for (Stereotag tag : g.stereotags())
			folder.addStereotag(tag);

		emptyGroupsAsNode.put(g, folder);
		return folder;
	}

	public EntityFactory(List<HideOrShow2> hides2, List<HideOrShow2> removed, CucaDiagram namespaceSeparator) {
		this.hides2 = hides2;
		this.removed = removed;
		this.namespaceSeparator = namespaceSeparator;
		this.leafsByCode = new LinkedHashMap<String, ILeaf>();
		this.groupsByCode = new LinkedHashMap<String, IGroup>();
	}

	public boolean isHidden(ILeaf leaf) {
		final IEntity other = isNoteWithSingleLinkAttachedTo(leaf);
		if (other instanceof ILeaf)
			return isHidden((ILeaf) other);

		boolean hidden = false;
		for (HideOrShow2 hide : hides2)
			hidden = hide.apply(hidden, leaf);

		return hidden;
	}

	public boolean isRemoved(Stereotype stereotype) {
		boolean result = false;
		for (HideOrShow2 hide : removed)
			result = hide.apply(result, stereotype);

		return result;
	}

	public boolean isRemoved(ILeaf leaf) {
		final IEntity other = isNoteWithSingleLinkAttachedTo(leaf);
		if (other instanceof ILeaf)
			return isRemoved((ILeaf) other);

		boolean result = false;
		for (HideOrShow2 hide : removed)
			result = hide.apply(result, leaf);

		return result;
	}

	private IEntity isNoteWithSingleLinkAttachedTo(ILeaf note) {
		if (note.getLeafType() != LeafType.NOTE)
			return null;
		assert note.getLeafType() == LeafType.NOTE;
		IEntity other = null;
		for (Link link : this.getLinks()) {
			if (link.getType().isInvisible())
				continue;
			if (link.contains(note) == false)
				continue;
			if (other != null)
				return null;
			other = link.getOther(note);
			if (other.getLeafType() == LeafType.NOTE)
				return null;

		}
		return other;

	}

	public boolean isRemovedIgnoreUnlinked(ILeaf leaf) {
		boolean result = false;
		for (HideOrShow2 hide : removed)
			if (hide.isAboutUnlinked() == false)
				result = hide.apply(result, leaf);

		return result;
	}

	public ILeaf createLeaf(Together together, Ident ident, Code code, Display display, LeafType entityType,
			IGroup parentContainer, Set<VisibilityModifier> hides, String namespaceSeparator) {
		final Bodier bodier;
		if (Objects.requireNonNull(entityType) == LeafType.MAP)
			bodier = new BodierMap();
		else if (Objects.requireNonNull(entityType) == LeafType.JSON)
			bodier = new BodierJSon();
		else
			bodier = BodyFactory.createLeaf(entityType, hides);

		final EntityImp result = new EntityImp(ident, code, this, bodier, parentContainer, entityType,
				namespaceSeparator, rawLayout);
		bodier.setLeaf(result);
		result.setDisplay(display);
		result.setTogether(together);
		return result;
	}

	public IGroup createGroup(Ident ident, Code code, Display display, Code namespace, GroupType groupType,
			IGroup parentContainer, Set<VisibilityModifier> hides, String namespaceSeparator) {
		Objects.requireNonNull(groupType);
		for (Entry<Ident, IGroup> ent : groups2.entrySet())
			if (ent.getKey().equals(ident))
				return ent.getValue();

		final Bodier bodier = BodyFactory.createGroup(hides);
		final EntityImp result = new EntityImp(ident, code, this, bodier, parentContainer, groupType, namespace,
				namespaceSeparator, rawLayout);
		if (Display.isNull(display) == false)
			result.setDisplay(display);

		return result;
	}

	public void addLeaf(ILeaf entity) {
		leafsByCode.put(entity.getCodeGetName(), entity);
		leafs2.put(entity.getIdent(), entity);
	}

	public void addGroup(IGroup group) {
		groupsByCode.put(group.getCodeGetName(), group);
		groups2.put(group.getIdent(), group);
	}

	private void ensureParentIsCreated(Ident ident) {
		if (groups2.get(ident.parent()) != null)
			return;
		getParentContainer(ident, null);
	}

	public /* private */ void removeGroup(String name) {
		final IEntity removed = Objects.requireNonNull(groupsByCode.remove(name));
		final IEntity removed2 = groups2.remove(removed.getIdent());
		if (removed != removed2) {
			bigError();
		}
	}

	public /* private */ void removeGroup(Ident ident) {
		Objects.requireNonNull(groups2.remove(Objects.requireNonNull(ident)));
	}

	public static void bigError() {
		// Thread.dumpStack();
		// System.exit(0);
		// throw new IllegalArgumentException();
	}

	public /* private */ void removeLeaf(String name) {
		final IEntity removed = Objects.requireNonNull(leafsByCode.remove(Objects.requireNonNull(name)));
		final IEntity removed2 = leafs2.remove(removed.getIdent());
		if (removed != removed2) {
			bigError();
		}
	}

	public /* private */ void removeLeaf(Ident ident) {
		final IEntity removed = leafs2.remove(Objects.requireNonNull(ident));
		if (removed == null) {
			System.err.println("leafs2=" + leafs2.keySet());
			throw new IllegalArgumentException(ident.toString());
		}
	}

	public IGroup muteToGroup(String name, Code namespace, GroupType type, IGroup parent) {
		final ILeaf leaf = leafsByCode.get(name);
		((EntityImp) leaf).muteToGroup(namespace, type, parent);
		final IGroup result = (IGroup) leaf;
		removeLeaf(name);
		return result;
	}

	public IGroup getRootGroup() {
		return rootGroup;
	}

	public final ILeaf getLeafStrict(Ident ident) {
		return leafs2.get(ident);
	}

	public Ident buildFullyQualified(Ident currentPath, Ident id) {
		if (currentPath.equals(id) == false)
			if (leafs2.containsKey(id) || groups2.containsKey(id))
				return id;

		if (id.size() > 1)
			return id;

		return currentPath.add(id);
	}

	public final IGroup getGroupStrict(Ident ident) {
		if (namespaceSeparator.getNamespaceSeparator() == null)
			return getGroupVerySmart(ident);

		final IGroup result = groups2.get(ident);
		return result;
	}

	public final IGroup getGroupVerySmart(Ident ident) {
		final IGroup result = groups2.get(ident);
		if (result == null)
			for (Entry<Ident, IGroup> ent : groups2.entrySet())
				if (ent.getKey().getLast().equals(ident.getLast()))
					return ent.getValue();

		return result;
	}

	public final ILeaf getLeaf(Code code) {
		final ILeaf result = leafsByCode.get(code.getName());
		if (result != null && result != leafs2.get(result.getIdent()))
			bigError();

		for (ILeaf tmp : leafsByCode.values())
			if (tmp.getIdent().equals(code))
				return tmp;

		return result;
	}

	public final IGroup getGroup(Code code) {
		final IGroup result = groupsByCode.get(code.getName());
		if (result != null && result != groups2.get(result.getIdent()))
			bigError();

		return result;
	}

	public final Collection<ILeaf> leafs() {
		final Collection<ILeaf> result = Collections.unmodifiableCollection(leafsByCode.values());
		if (new ArrayList<>(result).equals(new ArrayList<>(leafs2())) == false)
			bigError();

		return result;
	}

	public final Collection<IGroup> groups() {
		final Collection<IGroup> result = Collections.unmodifiableCollection(groupsByCode.values());
		if (new ArrayList<>(result).equals(new ArrayList<>(groups2())) == false)
			bigError();

		return result;
	}

	public final Collection<IGroup> groups2() {
		final Collection<IGroup> result = Collections.unmodifiableCollection(groups2.values());
		return Collections.unmodifiableCollection(result);
	}

	public final Collection<ILeaf> leafs2() {
		final Collection<ILeaf> result = Collections.unmodifiableCollection(leafs2.values());
		return Collections.unmodifiableCollection(result);
	}

	public void incRawLayout() {
		rawLayout++;
	}

	public final List<Link> getLinks() {
		return Collections.unmodifiableList(links);
	}

	public void addLink(Link link) {
		if (link.isSingle() && containsSimilarLink(link))
			return;

		links.add(link);
	}

	private boolean containsSimilarLink(Link other) {
		for (Link link : links)
			if (other.sameConnections(link))
				return true;

		return false;
	}

	public void removeLink(Link link) {
		final boolean ok = links.remove(link);
		if (ok == false)
			throw new IllegalArgumentException();

	}

	public IGroup getParentContainer(Ident ident, IGroup parentContainer) {
		return Objects.requireNonNull(parentContainer);
	}

	public CucaDiagram getDiagram() {
		return namespaceSeparator;
	}

}
