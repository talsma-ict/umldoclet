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

import java.util.Objects;

import net.sourceforge.plantuml.Hideable;
import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.OptionFlags;
import net.sourceforge.plantuml.Removeable;
import net.sourceforge.plantuml.UmlDiagramType;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.cucadiagram.entity.EntityImpl;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.USymbolInterface;
import net.sourceforge.plantuml.skin.VisibilityModifier;
import net.sourceforge.plantuml.style.StyleBuilder;
import net.sourceforge.plantuml.svek.Bibliotekon;
import net.sourceforge.plantuml.ugraphic.UComment;
import net.sourceforge.plantuml.ugraphic.UFont;

public class Link extends WithLinkType implements Hideable, Removeable {

	public final StyleBuilder getStyleBuilder() {
		return styleBuilder;
	}

	final private IEntity cl1;
	final private IEntity cl2;

	private String port1;
	private String port2;

	private final LinkArg linkArg;

	final private String uid;

	private CucaNote note;

	private boolean invis = false;
	private double weight = 1.0;

	private boolean constraint = true;
	private boolean inverted = false;
	private LinkArrow linkArrow = LinkArrow.NONE_OR_SEVERAL;

	private boolean opale;
	private boolean horizontalSolitary;
	private String sametail;
	private final StyleBuilder styleBuilder;
	private Stereotype stereotype;

	private Url url;

	public String idCommentForSvg() {
		if (type.looksLikeRevertedForSvg())
			return getEntity1().getCodeGetName() + "-backto-" + getEntity2().getCodeGetName();

		if (type.looksLikeNoDecorAtAllSvg())
			return getEntity1().getCodeGetName() + "-" + getEntity2().getCodeGetName();

		return getEntity1().getCodeGetName() + "-to-" + getEntity2().getCodeGetName();
	}

	public UComment commentForSvg() {
		if (type.looksLikeRevertedForSvg())
			return new UComment(
					"reverse link " + getEntity1().getCodeGetName() + " to " + getEntity2().getCodeGetName());

		return new UComment("link " + getEntity1().getCodeGetName() + " to " + getEntity2().getCodeGetName());
	}

	public Link(StyleBuilder styleBuilder, IEntity cl1, IEntity cl2, LinkType type, LinkArg linkArg) {
		if (linkArg.getLength() < 1)
			throw new IllegalArgumentException();

		this.styleBuilder = styleBuilder;
		this.cl1 = Objects.requireNonNull(cl1);
		this.cl2 = Objects.requireNonNull(cl2);

		this.type = type;
		final CucaDiagram diagram = ((EntityImpl) cl1).getDiagram();
		this.uid = "LNK" + diagram.getUniqueSequence();

		this.linkArg = linkArg;

		if (diagram.getPragma().useKermor()) {
			if (cl1.getEntityPosition().isNormal() == false ^ cl2.getEntityPosition().isNormal() == false)
				setConstraint(false);
		}
	}

	public Link getInv() {
		final Link result = new Link(styleBuilder, cl2, cl1, getType().getInversed(), linkArg.getInv());
		result.inverted = !this.inverted;
		result.port1 = this.port2;
		result.port2 = this.port1;
		result.url = this.url;
		result.linkConstraint = this.linkConstraint;
		result.stereotype = stereotype;
		result.linkArg.setVisibilityModifier(this.linkArg.getVisibilityModifier());
		return result;
	}

	@Override
	public void goNorank() {
		setConstraint(false);
	}

	public String getLabeldistance() {
		// Default in dot 1.0
		return getLinkArg().getLabeldistance();
	}

	public String getLabelangle() {
		// Default in dot -25
		return getLinkArg().getLabelangle();
	}

	public String getUid() {
		return uid;
	}

	public final boolean isInvis() {
		if (type.isInvisible())
			return true;

		return invis;
	}

	public final void setInvis(boolean invis) {
		this.invis = invis;
	}

	public boolean isBetween(IEntity cl1, IEntity cl2) {
		if (cl1.equals(this.cl1) && cl2.equals(this.cl2))
			return true;

		if (cl1.equals(this.cl2) && cl2.equals(this.cl1))
			return true;

		return false;
	}

	@Override
	public String toString() {
		return super.toString() + " {" + linkArg.getLength() + "} " + cl1 + "-->" + cl2;
	}

	public IEntity getEntity1() {
		return cl1;
	}

	public IEntity getEntity2() {
		return cl2;
	}

	public EntityPort getEntityPort1(Bibliotekon bibliotekon) {
		return getEntityPort((ILeaf) cl1, port1, bibliotekon);
	}

	public EntityPort getEntityPort2(Bibliotekon bibliotekon) {
		return getEntityPort((ILeaf) cl2, port2, bibliotekon);
	}

	private EntityPort getEntityPort(ILeaf leaf, String port, Bibliotekon bibliotekon) {
		if (leaf.getEntityPosition().usePortP())
			return EntityPort.forPort(bibliotekon.getNodeUid(leaf));
		return EntityPort.create(bibliotekon.getNodeUid(leaf), port);
	}

	@Override
	public LinkType getType() {
		if (opale)
			return new LinkType(LinkDecor.NONE, LinkDecor.NONE);

		if (getSametail() != null)
			return new LinkType(LinkDecor.NONE, LinkDecor.NONE);

		LinkType result = type;
		if (OptionFlags.USE_INTERFACE_EYE1) {
			if (isLollipopInterfaceEye(cl1))
				type = type.withLollipopInterfaceEye1();

			if (isLollipopInterfaceEye(cl2))
				type = type.withLollipopInterfaceEye2();

		}
		return result;
	}

	private boolean isReallyGroup(IEntity ent) {
		if (ent.isGroup() == false)
			return false;

		final IGroup group = (IGroup) ent;
		return group.getChildren().size() + group.getLeafsDirect().size() > 0;
	}

	public LinkType getTypePatchCluster() {
		LinkType result = getType();
		if (isReallyGroup(getEntity1()))
			result = result.withoutDecors2();

		if (isReallyGroup(getEntity2()))
			result = result.withoutDecors1();

		return result;
	}

	private LinkType getTypeSpecialForPrinting() {
		if (opale) {
			return new LinkType(LinkDecor.NONE, LinkDecor.NONE);
		}
		LinkType result = type;
		if (OptionFlags.USE_INTERFACE_EYE1) {
			if (isLollipopInterfaceEye(cl1))
				type = type.withLollipopInterfaceEye1();

			if (isLollipopInterfaceEye(cl2))
				type = type.withLollipopInterfaceEye2();

		}
		return result;
	}

	private boolean isLollipopInterfaceEye(IEntity ent) {
		return ent.getUSymbol() instanceof USymbolInterface;
	}

	public Display getLabel() {
		return getLinkArg().getLabel();
	}

	public int getLength() {
		return getLinkArg().getLength();
	}

	public final void setLength(int length) {
		this.getLinkArg().setLength(length);
	}

	public String getQualifier1() {
		return getLinkArg().getQualifier1();
	}

	public String getQualifier2() {
		return getLinkArg().getQualifier2();
	}

	public final double getWeight() {
		return weight;
	}

	public final void setWeight(double weight) {
		this.weight = weight;
	}

	public final CucaNote getNote() {
		return note;
	}

	public final void addNote(CucaNote note) {
		this.note = note;
	}

	public final void addNoteFrom(Link other, NoteLinkStrategy strategy) {
		if (other.note != null)
			this.note = other.note.withStrategy(strategy);
	}

	public boolean isAutoLinkOfAGroup() {
		if (getEntity1().isGroup() == false)
			return false;

		if (getEntity2().isGroup() == false)
			return false;

		if (getEntity1() == getEntity2())
			return true;

		return false;
	}

	public boolean containsType(LeafType type) {
		if (getEntity1().getLeafType() == type || getEntity2().getLeafType() == type)
			return true;

		return false;
	}

	public boolean contains(IEntity entity) {
		if (getEntity1() == entity || getEntity2() == entity)
			return true;

		return false;
	}

	public IEntity getOther(IEntity entity) {
		if (getEntity1() == entity)
			return getEntity2();

		if (getEntity2() == entity)
			return getEntity1();

		throw new IllegalArgumentException();
	}

//	public double getMarginDecors1(StringBounder stringBounder, UFont fontQualif, ISkinSimple spriteContainer) {
//		final double q = getQualifierMargin(stringBounder, fontQualif, linkArg.getQualifier1(), spriteContainer);
//		final LinkDecor decor = getType().getDecor1();
//		return decor.getMargin() + q;
//	}
//
//	public double getMarginDecors2(StringBounder stringBounder, UFont fontQualif, ISkinSimple spriteContainer) {
//		final double q = getQualifierMargin(stringBounder, fontQualif, linkArg.getQualifier2(), spriteContainer);
//		final LinkDecor decor = getType().getDecor2();
//		return decor.getMargin() + q;
//	}

	private double getQualifierMargin(StringBounder stringBounder, UFont fontQualif, String qualif,
			ISkinSimple spriteContainer) {
		if (qualif != null) {
			final TextBlock b = Display.create(qualif).create(FontConfiguration.blackBlueTrue(fontQualif),
					HorizontalAlignment.LEFT, spriteContainer);
			final XDimension2D dim = b.calculateDimension(stringBounder);
			return Math.max(dim.getWidth(), dim.getHeight());
		}
		return 0;
	}

	public final boolean isConstraint() {
		return constraint;
	}

	public final void setConstraint(boolean constraint) {
		this.constraint = constraint;
	}

	public void setOpale(boolean opale) {
		this.opale = opale;
	}

	public final void setHorizontalSolitary(boolean horizontalSolitary) {
		this.horizontalSolitary = horizontalSolitary;
	}

	public final boolean isHorizontalSolitary() {
		return horizontalSolitary;
	}

	public final LinkArrow getLinkArrow() {
		if (inverted)
			return linkArrow.reverse();

		return linkArrow;
	}

	public final void setLinkArrow(LinkArrow linkArrow) {
		this.linkArrow = linkArrow;
	}

	public final boolean isInverted() {
		return inverted;
	}

	public boolean hasEntryPoint() {
		return (getEntity1().isGroup() == false && getEntity1().getEntityPosition() != EntityPosition.NORMAL)
				|| (getEntity2().isGroup() == false && getEntity2().getEntityPosition() != EntityPosition.NORMAL);
	}

	public boolean hasTwoEntryPointsSameContainer() {
		return getEntity1().isGroup() == false && getEntity2().isGroup() == false
				&& getEntity1().getEntityPosition() != EntityPosition.NORMAL
				&& getEntity2().getEntityPosition() != EntityPosition.NORMAL
				&& getEntity1().getParentContainer() == getEntity2().getParentContainer();
	}

	public Url getUrl() {
		return url;
	}

	public void setUrl(Url url) {
		this.url = url;
	}

	public boolean isHidden() {
		return hidden || cl1.isHidden() || cl2.isHidden();
	}

	public boolean sameConnections(Link other) {
		if (this.cl1 == other.cl1 && this.cl2 == other.cl2)
			return true;

		if (this.cl1 == other.cl2 && this.cl2 == other.cl1)
			return true;

		return false;
	}

	public boolean doesTouch(Link other) {
		if (this.cl1 == other.cl1)
			return true;

		if (this.cl1 == other.cl2)
			return true;

		if (this.cl2 == other.cl1)
			return true;

		if (this.cl2 == other.cl2)
			return true;

		return false;
	}

	public boolean isAutolink() {
		return cl1 == cl2;
	}

	public boolean isRemoved() {
		return cl1.isRemoved() || cl2.isRemoved();
	}

	public boolean hasUrl() {
		if (Display.isNull(linkArg.getLabel()) == false && linkArg.getLabel().hasUrl())
			return true;

		return getUrl() != null;
	}

	public String getSametail() {
		return sametail;
	}

	public void setSametail(String sametail) {
		this.sametail = sametail;
	}

	public void setPortMembers(String port1, String port2) {
		this.port1 = port1;
		this.port2 = port2;
		if (port1 != null)
			((ILeaf) cl1).addPortShortName(port1);

		if (port2 != null)
			((ILeaf) cl2).addPortShortName(port2);

	}

	private UmlDiagramType umlType;

	public void setUmlDiagramType(UmlDiagramType type) {
		this.umlType = type;
	}

	public UmlDiagramType getUmlDiagramType() {
		return umlType;
	}

	private LinkConstraint linkConstraint;

	public void setLinkConstraint(LinkConstraint linkConstraint) {
		this.linkConstraint = linkConstraint;
	}

	public final LinkConstraint getLinkConstraint() {
		return linkConstraint;
	}

	private LineLocation codeLine;

	public String getCodeLine() {
		if (codeLine == null)
			return null;

		return "" + codeLine.getPosition();
	}

	public void setCodeLine(LineLocation location) {
		this.codeLine = location;
	}

	public void setStereotype(Stereotype stereotype) {
		this.stereotype = stereotype;
	}

	public final Stereotype getStereotype() {
		return stereotype;
	}

	public final LinkArg getLinkArg() {
		return linkArg;
	}

	public final VisibilityModifier getVisibilityModifier() {
		return getLinkArg().getVisibilityModifier();
	}

}
