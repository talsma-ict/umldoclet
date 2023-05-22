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
package net.sourceforge.plantuml.svek;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.abel.CucaNote;
import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.abel.Hideable;
import net.sourceforge.plantuml.abel.LeafType;
import net.sourceforge.plantuml.abel.Link;
import net.sourceforge.plantuml.abel.LinkArrow;
import net.sourceforge.plantuml.abel.NoteLinkStrategy;
import net.sourceforge.plantuml.cucadiagram.EntityPort;
import net.sourceforge.plantuml.decoration.LinkDecor;
import net.sourceforge.plantuml.decoration.LinkMiddleDecor;
import net.sourceforge.plantuml.decoration.LinkType;
import net.sourceforge.plantuml.decoration.Rainbow;
import net.sourceforge.plantuml.descdiagram.command.StringWithArrow;
import net.sourceforge.plantuml.dot.DotSplines;
import net.sourceforge.plantuml.dot.GraphvizVersion;
import net.sourceforge.plantuml.klimt.UGroupType;
import net.sourceforge.plantuml.klimt.UStroke;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.ColorType;
import net.sourceforge.plantuml.klimt.color.Colors;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.color.HColors;
import net.sourceforge.plantuml.klimt.creole.CreoleMode;
import net.sourceforge.plantuml.klimt.creole.Display;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.font.FontConfiguration;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.BezierUtils;
import net.sourceforge.plantuml.klimt.geom.HorizontalAlignment;
import net.sourceforge.plantuml.klimt.geom.MagneticBorder;
import net.sourceforge.plantuml.klimt.geom.Moveable;
import net.sourceforge.plantuml.klimt.geom.PointAndAngle;
import net.sourceforge.plantuml.klimt.geom.Positionable;
import net.sourceforge.plantuml.klimt.geom.PositionableUtils;
import net.sourceforge.plantuml.klimt.geom.Side;
import net.sourceforge.plantuml.klimt.geom.VerticalAlignment;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.klimt.geom.XPoint2D;
import net.sourceforge.plantuml.klimt.shape.DotPath;
import net.sourceforge.plantuml.klimt.shape.TextBlock;
import net.sourceforge.plantuml.klimt.shape.TextBlockUtils;
import net.sourceforge.plantuml.klimt.shape.UDrawable;
import net.sourceforge.plantuml.klimt.shape.ULine;
import net.sourceforge.plantuml.klimt.shape.UPolygon;
import net.sourceforge.plantuml.skin.AlignmentParam;
import net.sourceforge.plantuml.skin.ColorParam;
import net.sourceforge.plantuml.skin.LineParam;
import net.sourceforge.plantuml.skin.Pragma;
import net.sourceforge.plantuml.skin.UmlDiagramType;
import net.sourceforge.plantuml.skin.VisibilityModifier;
import net.sourceforge.plantuml.skin.rose.Rose;
import net.sourceforge.plantuml.stereo.Stereotype;
import net.sourceforge.plantuml.style.ISkinParam;
import net.sourceforge.plantuml.style.StyleBuilder;
import net.sourceforge.plantuml.svek.extremity.Extremity;
import net.sourceforge.plantuml.svek.extremity.ExtremityArrow;
import net.sourceforge.plantuml.svek.extremity.ExtremityFactory;
import net.sourceforge.plantuml.svek.extremity.ExtremityFactoryExtends;
import net.sourceforge.plantuml.svek.extremity.ExtremityOther;
import net.sourceforge.plantuml.svek.image.EntityImageNoteLink;
import net.sourceforge.plantuml.url.Url;
import net.sourceforge.plantuml.utils.Direction;
import net.sourceforge.plantuml.utils.Log;
import net.sourceforge.plantuml.utils.Position;

public class SvekLine implements Moveable, Hideable, GuideLine {

	private static final XDimension2D CONSTRAINT_SPOT = new XDimension2D(10, 10);

	private final Cluster ltail;
	private final Cluster lhead;
	private final Link link;

	private final EntityPort startUid;
	private final EntityPort endUid;

	private final TextBlock startTailText;
	private final TextBlock endHeadText;
	private final TextBlock labelText;
	private boolean divideLabelWidthByTwo = false;

	private final int lineColor;
	private final int noteLabelColor;
	private final int startTailColor;
	private final int endHeadColor;

	private final StringBounder stringBounder;
	private final Bibliotekon bibliotekon;

	private DotPath dotPath;

	private Positionable startTailLabelXY;
	private Positionable endHeadLabelXY;
	private Positionable labelXY;

	private UDrawable extremity1;
	private UDrawable extremity2;

	private double dx;
	private double dy;

	private boolean opale;
	private Cluster projectionCluster;

	private final Pragma pragma;
	private final HColor backgroundColor;
	private final boolean useRankSame;
	private final UStroke defaultThickness;
	private HColor arrowLollipopColor;
	private final ISkinParam skinParam;

	private final double labelShield;

	private final UmlDiagramType type;

	@Override
	public String toString() {
		return super.toString() + " color=" + lineColor;
	}

	public Direction getArrowDirection() {
		if (getLinkArrow() == LinkArrow.BACKWARD)
			return getArrowDirectionInternal().getInv();

		return getArrowDirectionInternal();
	}

	private Direction getArrowDirectionInternal() {
		if (isAutolink()) {
			final double startAngle = dotPath.getStartAngle();
			return Direction.LEFT;
		}
		final XPoint2D start = dotPath.getStartPoint();
		final XPoint2D end = dotPath.getEndPoint();
		final double ang = Math.atan2(end.getX() - start.getX(), end.getY() - start.getY());
		if (ang > -Math.PI / 4 && ang < Math.PI / 4)
			return Direction.DOWN;

		if (ang > Math.PI * 3 / 4 || ang < -Math.PI * 3 / 4)
			return Direction.UP;

		return end.getX() > start.getX() ? Direction.RIGHT : Direction.LEFT;
	}

	public double getArrowDirection2() {
		if (getLinkArrow() == LinkArrow.BACKWARD)
			return Math.PI + getArrowDirectionInternal2();

		return getArrowDirectionInternal2();
	}

	private double getArrowDirectionInternal2() {
		if (isAutolink()) {
			final double startAngle = dotPath.getStartAngle();
			return startAngle;
		}
		final XPoint2D start = dotPath.getStartPoint();
		final XPoint2D end = dotPath.getEndPoint();
		final double ang = Math.atan2(end.getX() - start.getX(), end.getY() - start.getY());
		return ang;
	}

	private Cluster getCluster2(Bibliotekon bibliotekon, Entity entityMutable) {
		for (Cluster cl : bibliotekon.allCluster())
			if (cl.getGroups().contains(entityMutable))
				return cl;

		throw new IllegalArgumentException();
	}

	public SvekLine(Link link, ColorSequence colorSequence, ISkinParam skinParam, StringBounder stringBounder,
			FontConfiguration font, Bibliotekon bibliotekon, Pragma pragma, GraphvizVersion graphvizVersion) {

		// ::comment when __CORE__
		if (graphvizVersion.useShieldForQuantifier() && link.getLinkArg().getQuantifier1() != null)
			link.getEntity1().ensureMargins(Margins.uniform(16));

		if (graphvizVersion.useShieldForQuantifier() && link.getLinkArg().getQuantifier2() != null)
			link.getEntity2().ensureMargins(Margins.uniform(16));
		// ::done

		if (link.getLinkArg().getKal1() != null)
			this.kal1 = new Kal(this, link.getLinkArg().getKal1(), font, skinParam, (Entity) link.getEntity1(), link,
					stringBounder);

		if (link.getLinkArg().getKal2() != null)
			this.kal2 = new Kal(this, link.getLinkArg().getKal2(), font, skinParam, (Entity) link.getEntity2(), link,
					stringBounder);

		this.type = skinParam.getUmlDiagramType();
		this.link = Objects.requireNonNull(link);
		this.skinParam = skinParam;
		// this.umlType = link.getUmlDiagramType();
		this.useRankSame = skinParam.useRankSame();
		this.startUid = link.getEntityPort1(bibliotekon);
		this.endUid = link.getEntityPort2(bibliotekon);

		Cluster ltail = null;
		if (startUid.startsWith(Cluster.CENTER_ID))
			ltail = getCluster2(bibliotekon, link.getEntity1());

		Cluster lhead = null;
		if (endUid.startsWith(Cluster.CENTER_ID))
			lhead = getCluster2(bibliotekon, link.getEntity2());

		if (link.getColors() != null) {
			skinParam = link.getColors().mute(skinParam);
			font = font.mute(link.getColors());
		}
		this.backgroundColor = skinParam.getBackgroundColor();
		this.defaultThickness = skinParam.getThickness(LineParam.arrow, null);
		this.arrowLollipopColor = skinParam.getHtmlColor(ColorParam.arrowLollipop, null, false);
		if (arrowLollipopColor == null)
			this.arrowLollipopColor = backgroundColor;

		this.pragma = pragma;
		this.bibliotekon = bibliotekon;
		this.stringBounder = stringBounder;
		this.ltail = ltail;
		this.lhead = lhead;

		this.lineColor = colorSequence.getValue();
		this.noteLabelColor = colorSequence.getValue();
		this.startTailColor = colorSequence.getValue();
		this.endHeadColor = colorSequence.getValue();

		TextBlock labelOnly;
		if (Display.isNull(link.getLabel())) {
			labelOnly = TextBlockUtils.EMPTY_TEXT_BLOCK;
			if (getLinkArrow() != LinkArrow.NONE_OR_SEVERAL)
				labelOnly = StringWithArrow.addMagicArrow(labelOnly, this, font);

		} else {
			final HorizontalAlignment alignment = getMessageTextAlignment(type, skinParam);
			final boolean hasSeveralGuideLines = link.getLabel().hasSeveralGuideLines();
			final TextBlock block;
			if (hasSeveralGuideLines)
				block = StringWithArrow.addSeveralMagicArrows(link.getLabel(), this, font, alignment, skinParam);
			else
				block = link.getLabel().create0(font, alignment, skinParam, skinParam.maxMessageSize(),
						CreoleMode.SIMPLE_LINE, null, null);

			labelOnly = addVisibilityModifier(block, link, skinParam);
			if (getLinkArrow() != LinkArrow.NONE_OR_SEVERAL && hasSeveralGuideLines == false)
				labelOnly = StringWithArrow.addMagicArrow(labelOnly, this, font);

		}

		final CucaNote note = link.getNote();
		if (note == null) {
			labelText = labelOnly;
		} else {
			final TextBlock noteOnly = new EntityImageNoteLink(note.getDisplay(), note.getColors(), skinParam,
					link.getStyleBuilder());
			if (note.getStrategy() == NoteLinkStrategy.HALF_NOT_PRINTED
					|| note.getStrategy() == NoteLinkStrategy.HALF_PRINTED_FULL)
				divideLabelWidthByTwo = true;

			if (note.getPosition() == Position.LEFT)
				labelText = TextBlockUtils.mergeLR(noteOnly, labelOnly, VerticalAlignment.CENTER);
			else if (note.getPosition() == Position.RIGHT)
				labelText = TextBlockUtils.mergeLR(labelOnly, noteOnly, VerticalAlignment.CENTER);
			else if (note.getPosition() == Position.TOP)
				labelText = TextBlockUtils.mergeTB(noteOnly, labelOnly, HorizontalAlignment.CENTER);
			else
				labelText = TextBlockUtils.mergeTB(labelOnly, noteOnly, HorizontalAlignment.CENTER);

		}

		if (link.getQuantifier1() == null)
			startTailText = null;
		else
			startTailText = Display.getWithNewlines(link.getQuantifier1()).create(font, HorizontalAlignment.CENTER,
					skinParam);

		if (link.getQuantifier2() == null)
			endHeadText = null;
		else
			endHeadText = Display.getWithNewlines(link.getQuantifier2()).create(font, HorizontalAlignment.CENTER,
					skinParam);

		if (link.getType().getMiddleDecor() == LinkMiddleDecor.NONE)
			this.labelShield = 0;
		else
			this.labelShield = 7;

	}

	private Kal kal1;
	private Kal kal2;

	private TextBlock addVisibilityModifier(TextBlock block, Link link, ISkinParam skinParam) {
		final VisibilityModifier visibilityModifier = link.getVisibilityModifier();
		if (visibilityModifier != null) {
			final Rose rose = new Rose();
			final HColor fore = rose.getHtmlColor(skinParam, visibilityModifier.getForeground());
			TextBlock visibility = visibilityModifier.getUBlock(skinParam.classAttributeIconSize(), fore, null, false);
			visibility = TextBlockUtils.withMargin(visibility, 0, 1, 2, 0);
			block = TextBlockUtils.mergeLR(visibility, block, VerticalAlignment.CENTER);
		}
		final double marginLabel = startUid.equalsId(endUid) ? 6 : 1;
		return TextBlockUtils.withMargin(block, marginLabel, marginLabel);
	}

	private HorizontalAlignment getMessageTextAlignment(UmlDiagramType umlDiagramType, ISkinParam skinParam) {
		if (umlDiagramType == UmlDiagramType.STATE)
			return skinParam.getHorizontalAlignment(AlignmentParam.stateMessageAlignment, null, false, null);

		return skinParam.getDefaultTextAlignment(HorizontalAlignment.CENTER);
	}

	public boolean hasNoteLabelText() {
		return labelText != null && labelText != TextBlockUtils.EMPTY_TEXT_BLOCK;
	}

	private LinkArrow getLinkArrow() {
		return link.getLinkArrow();
	}

	// ::comment when __CORE__
	public void appendLine(GraphvizVersion graphvizVersion, StringBuilder sb, DotMode dotMode, DotSplines dotSplines) {
		// Log.println("inverted=" + isInverted());
		// if (isInverted()) {
		// sb.append(endUid);
		// sb.append("->");
		// sb.append(startUid);
		// } else {
		sb.append(startUid.getFullString());
		sb.append("->");
		sb.append(endUid.getFullString());
		// }
		sb.append("[");
		final LinkType linkType = link.getTypePatchCluster();
		String decoration = linkType.getSpecificDecorationSvek();
		if (decoration.length() > 0 && decoration.endsWith(",") == false)
			decoration += ",";

		sb.append(decoration);

		int length = link.getLength();
		if (graphvizVersion.ignoreHorizontalLinks() && length == 1)
			length = 2;

		if (useRankSame) {
			if (pragma.horizontalLineBetweenDifferentPackageAllowed() || link.isInvis() || length != 1) {
				// if (graphvizVersion.isJs() == false) {
				sb.append("minlen=" + (length - 1));
				sb.append(",");
				// }
			}
		} else {
			sb.append("minlen=" + (length - 1));
			sb.append(",");
		}
		sb.append("color=\"" + StringUtils.sharp000000(lineColor) + "\"");
		if (hasNoteLabelText() || link.getLinkConstraint() != null) {
			sb.append(",");
			if (graphvizVersion.useXLabelInsteadOfLabel() || dotMode == DotMode.NO_LEFT_RIGHT_AND_XLABEL
					|| dotSplines == DotSplines.ORTHO) {
				sb.append("xlabel=<");
			} else {
				sb.append("label=<");
			}
			XDimension2D dimNote = hasNoteLabelText() ? labelText.calculateDimension(stringBounder) : CONSTRAINT_SPOT;
			dimNote = dimNote.delta(2 * labelShield);

			appendTable(sb, eventuallyDivideByTwo(dimNote), noteLabelColor, graphvizVersion);
			sb.append(">");
		}

		if (startTailText != null) {
			sb.append(",");
			sb.append("taillabel=<");
			appendTable(sb, startTailText.calculateDimension(stringBounder), startTailColor, graphvizVersion);
			sb.append(">");
		}
		if (endHeadText != null) {
			sb.append(",");
			sb.append("headlabel=<");
			appendTable(sb, endHeadText.calculateDimension(stringBounder), endHeadColor, graphvizVersion);
			sb.append(">");
		}

		if (link.isInvis()) {
			sb.append(",");
			sb.append("style=invis");
		}

		if (link.isConstraint() == false || link.hasTwoEntryPointsSameContainer())
			sb.append(",constraint=false");

		if (link.getSametail() != null)
			sb.append(",sametail=" + link.getSametail());

		sb.append("];");
		SvekUtils.println(sb);
	}
	// ::done

	private XDimension2D eventuallyDivideByTwo(XDimension2D dim) {
		if (divideLabelWidthByTwo)
			return new XDimension2D(dim.getWidth() / 2, dim.getHeight());

		return dim;
	}

	public String rankSame() {
		// if (graphvizVersion == GraphvizVersion.V2_34_0) {
		// return null;
		// }
		if (pragma.horizontalLineBetweenDifferentPackageAllowed() == false && link.getLength() == 1
		/* && graphvizVersion.isJs() == false */) {
			return "{rank=same; " + getStartUidPrefix() + "; " + getEndUidPrefix() + "}";
		}
		return null;
	}

	public static void appendTable(StringBuilder sb, XDimension2D dim, int col, GraphvizVersion graphvizVersion) {
		final int w = (int) dim.getWidth();
		final int h = (int) dim.getHeight();
		appendTable(sb, w, h, col);
	}

	public static void appendTable(StringBuilder sb, int w, int h, int col) {
		sb.append("<TABLE ");
		sb.append("BGCOLOR=\"" + StringUtils.sharp000000(col) + "\" ");
		sb.append("FIXEDSIZE=\"TRUE\" WIDTH=\"" + w + "\" HEIGHT=\"" + h + "\">");
		sb.append("<TR");
		sb.append(">");
		sb.append("<TD");
		// sb.append(" FIXEDSIZE=\"TRUE\" WIDTH=\"" + 0 + "\" HEIGHT=\"" + 0 +
		// "\"");
		sb.append(">");
		sb.append("</TD>");
		sb.append("</TR>");
		sb.append("</TABLE>");
	}

	public final String getStartUidPrefix() {
		return startUid.getPrefix();
	}

	public final String getEndUidPrefix() {
		return endUid.getPrefix();
	}

	private UDrawable getExtremitySpecial(final XPoint2D center, LinkDecor decor, double angle, Cluster cluster,
			SvekNode nodeContact) {
		final ExtremityFactory extremityFactory = decor.getExtremityFactory(backgroundColor);
		return extremityFactory.createUDrawable(center, angle, null);
	}

	private UDrawable getExtremity(final XPoint2D center, LinkDecor decor, PointListIterator pointListIterator,
			double angle, Cluster cluster, SvekNode nodeContact) {
		final ExtremityFactory extremityFactory = decor.getExtremityFactory(backgroundColor);

		if (cluster != null) {
			if (extremityFactory != null) {
				// System.err.println("angle=" + angle * 180 / Math.PI);
				return extremityFactory.createUDrawable(center, angle, null);
			}
			if (decor == LinkDecor.EXTENDS)
				return new ExtremityFactoryExtends(backgroundColor).createUDrawable(center, angle, null);

			return null;
		}

		if (extremityFactory != null) {
			final List<XPoint2D> points = pointListIterator.next();
			if (points.size() == 0)
				throw new IllegalStateException();
			// return extremityFactory.createUDrawable(center, angle, null);

			final XPoint2D p0 = points.get(0);
			final XPoint2D p1 = points.get(1);
			final XPoint2D p2 = points.get(2);

			Side side = null;
			if (nodeContact != null)
				side = nodeContact.getRectangleArea().getClosestSide(p1);

			return extremityFactory.createUDrawable(p0, p1, p2, side);
		} else if (decor == LinkDecor.NONE) {
			final UPolygon sh = new UPolygon(pointListIterator.cloneMe().next());
			final XPoint2D contact = sh.checkMiddleContactForSpecificTriangle(center);
			if (contact != null) {
				return new UDrawable() {
					public void drawU(UGraphic ug) {
						ULine line = new ULine(contact.getX() - center.getX(), contact.getY() - center.getY());
						ug = ug.apply(UTranslate.point(center));
						ug.draw(line);
					}
				};
			}
		} else if (decor != LinkDecor.NONE) {
			final UPolygon sh = new UPolygon(pointListIterator.next());
			return new ExtremityOther(sh);
		}
		return null;

	}

	public void solveLine(SvgResult fullSvg) {
		if (this.link.isInvis())
			return;

		int idx = fullSvg.getIndexFromColor(this.lineColor);
		if (idx == -1) {
			return;
			// throw new IllegalStateException();
		}
		idx = fullSvg.indexOf("d=\"", idx);
		if (idx == -1)
			throw new IllegalStateException();

		final int end = fullSvg.indexOf("\"", idx + 3);
		final SvgResult path = fullSvg.substring(idx + 3, end);

		if (path.isPathConsistent() == false)
			return;

		dotPath = path.toDotPath();

		if (projectionCluster != null) {
			// System.err.println("Line::solveLine1 projectionCluster=" +
			// projectionCluster.getClusterPosition());
			projectionCluster.manageEntryExitPoint(stringBounder);
			// System.err.println("Line::solveLine2 projectionCluster=" +
			// projectionCluster.getClusterPosition());
			// if (lhead != null)
			// System.err.println("Line::solveLine ltail=" + lhead.getClusterPosition());
			// if (ltail != null)
			// System.err.println("Line::solveLine ltail=" + ltail.getClusterPosition());
		}
		dotPath = dotPath.simulateCompound(lhead == null ? null : lhead.getRectangleArea(),
				ltail == null ? null : ltail.getRectangleArea());

		final SvgResult lineSvg = fullSvg.substring(end);
		PointListIterator pointListIterator = lineSvg.getPointsWithThisColor(lineColor);

		final LinkType linkType = link.getType();
		if (link.getLength() == 1 && isThereTwo(linkType) && count(pointListIterator.cloneMe()) == 2) {
			// Sorry, this is ugly because of
			// https://github.com/plantuml/plantuml/issues/1353

			final List<XPoint2D> points = pointListIterator.next();
			final XPoint2D p1 = points.get(1);

			XPoint2D startPoint = dotPath.getStartPoint();
			XPoint2D endPoint = dotPath.getEndPoint();
			if (p1.distance(startPoint) < p1.distance(endPoint))
				startPoint = p1;
			else
				endPoint = p1;

			this.extremity1 = getExtremitySpecial(startPoint, linkType.getDecor2(), dotPath.getStartAngle() + Math.PI,
					ltail, getSvekNode1());
			this.extremity2 = getExtremitySpecial(endPoint, linkType.getDecor1(), dotPath.getEndAngle(), lhead,
					getSvekNode2());
		} else {
			this.extremity1 = getExtremity(dotPath.getStartPoint(), linkType.getDecor2(), pointListIterator,
					dotPath.getStartAngle() + Math.PI, ltail, getSvekNode1());
			this.extremity2 = getExtremity(dotPath.getEndPoint(), linkType.getDecor1(), pointListIterator,
					dotPath.getEndAngle(), lhead, getSvekNode2());
		}

		if (link.getEntity1().getLeafType() == LeafType.LOLLIPOP_HALF)
			getSvekNode1().addImpact(dotPath.getStartAngle() + Math.PI);

		if (link.getEntity2().getLeafType() == LeafType.LOLLIPOP_HALF)
			getSvekNode2().addImpact(dotPath.getEndAngle());

		if (extremity1 instanceof Extremity && extremity2 instanceof Extremity) {
			final XPoint2D p1 = ((Extremity) extremity1).somePoint();
			final XPoint2D p2 = ((Extremity) extremity2).somePoint();
			if (p1 != null && p2 != null) {
				// http://plantuml.sourceforge.net/qa/?qa=4240/some-relations-point-wrong-direction-when-the-linetype-ortho
				final double dist1start = p1.distance(dotPath.getStartPoint());
				final double dist1end = p1.distance(dotPath.getEndPoint());
				final double dist2start = p2.distance(dotPath.getStartPoint());
				final double dist2end = p2.distance(dotPath.getEndPoint());
				if (dist1start > dist1end && dist2end > dist2start) {
					pointListIterator = lineSvg.getPointsWithThisColor(lineColor);
					this.extremity2 = getExtremity(dotPath.getEndPoint(), linkType.getDecor1(), pointListIterator,
							dotPath.getEndAngle(), lhead, getSvekNode2());
					this.extremity1 = getExtremity(dotPath.getStartPoint(), linkType.getDecor2(), pointListIterator,
							dotPath.getStartAngle() + Math.PI, ltail, getSvekNode1());
				}
			}

		}

		if (hasNoteLabelText() || link.getLinkConstraint() != null) {
			final XPoint2D pos = getXY(fullSvg, this.noteLabelColor);
			if (pos != null) {
//				corner1.manage(pos);
				this.labelXY = hasNoteLabelText() ? TextBlockUtils.asPositionable(labelText, stringBounder, pos)
						: TextBlockUtils.asPositionable(CONSTRAINT_SPOT, stringBounder, pos);
			}
		}

		if (this.startTailText != null) {
			final XPoint2D pos = getXY(fullSvg, this.startTailColor);
			if (pos != null) {
//				corner1.manage(pos);
				this.startTailLabelXY = TextBlockUtils.asPositionable(startTailText, stringBounder, pos);
			}
		}

		if (this.endHeadText != null) {
			final XPoint2D pos = getXY(fullSvg, this.endHeadColor);
			if (pos != null) {
//				corner1.manage(pos);
				this.endHeadLabelXY = TextBlockUtils.asPositionable(endHeadText, stringBounder, pos);
//				corner1.manage(pos.getX() - 15, pos.getY());
			}
		}

		if (isOpalisable() == false)
			setOpale(false);

	}

	private boolean isThereTwo(final LinkType linkType) {
		return linkType.getDecor2().getExtremityFactory(backgroundColor) != null
				&& linkType.getDecor1().getExtremityFactory(backgroundColor) != null;
	}

	private int count(PointListIterator it) {
		int nb = 0;
		while (it.hasNext()) {
			it.next();
			nb++;
		}
		return nb;
	}

	private SvekNode getSvekNode2() {
		return bibliotekon.getNode(link.getEntity2());
	}

	private SvekNode getSvekNode1() {
		return bibliotekon.getNode(link.getEntity1());
	}

	private boolean isOpalisable() {
		return dotPath.getBeziers().size() <= 1;
	}

	private XPoint2D getXY(SvgResult svgResult, int color) {
		final int idx = svgResult.getIndexFromColor(color);
		if (idx == -1)
			return null;

		return SvekUtils.getMinXY(svgResult.substring(idx).extractList(SvgResult.POINTS_EQUALS));

	}

	public void drawU(UGraphic ug, Set<String> ids, UStroke suggestedStroke, Rainbow rainbow) {
		if (opale)
			return;

		if (link.isInvis())
			return;

		if (dotPath == null) {
			Log.info("DotPath is null for " + this);
			return;
		}

		ug.draw(link.commentForSvg());
		final Map<UGroupType, String> typeIDent = new EnumMap<>(UGroupType.class);
		typeIDent.put(UGroupType.CLASS,
				"link " + link.getEntity1().getName() + " " + link.getEntity2().getName() + " selected");
		typeIDent.put(UGroupType.ID, "link_" + link.getEntity1().getName() + "_" + link.getEntity2().getName());
		ug.startGroup(typeIDent);
		double x = 0;
		double y = 0;
		final Url url = link.getUrl();
		if (url != null)
			ug.startUrl(url);

		if (link.isAutoLinkOfAGroup()) {
			final Cluster cl = bibliotekon.getCluster((Entity) link.getEntity1());
			if (cl != null) {
				x += cl.getRectangleArea().getWidth();
				x -= dotPath.getStartPoint().getX() - cl.getRectangleArea().getMinX();
			}
		}

		x += dx;
		y += dy;

		HColor arrowHeadColor = rainbow.getArrowHeadColor();
		HColor color = rainbow.getColor();

		if (this.link.getColors() != null) {
			final HColor newColor = this.link.getColors().getColor(ColorType.ARROW, ColorType.LINE);
			if (newColor != null) {
				color = newColor;
				arrowHeadColor = color;
			}
		} else if (this.link.getSpecificColor() != null) {
			color = this.link.getSpecificColor();
			arrowHeadColor = color;
		}

		ug = ug.apply(HColors.none().bg()).apply(color);
		final LinkType linkType = link.getType();
		UStroke stroke;
		if (suggestedStroke == null || linkType.getStyle().isNormal() == false)
			stroke = linkType.getStroke3(defaultThickness);
		else
			stroke = linkType.getStroke3(suggestedStroke);

		if (link.getColors() != null && link.getColors().getSpecificLineStroke() != null)
			stroke = link.getColors().getSpecificLineStroke();

		ug = ug.apply(stroke);
		// double moveEndY = 0;

		DotPath todraw = dotPath.copy();

		UTranslate magneticForce1 = UTranslate.none();
		if (getSvekNode1() != null) {
			final MagneticBorder magneticBorder1 = getSvekNode1().getMagneticBorder();
			magneticForce1 = magneticBorder1.getForceAt(ug.getStringBounder(), todraw.getStartPoint());
			todraw.moveStartPoint(magneticForce1);
		}

		UTranslate magneticForce2 = UTranslate.none();
		if (getSvekNode2() != null) {
			final MagneticBorder magneticBorder2 = getSvekNode2().getMagneticBorder();
			magneticForce2 = magneticBorder2.getForceAt(ug.getStringBounder(), todraw.getEndPoint());
			todraw.moveEndPoint(magneticForce2);
		}

//		final MagneticBorder magneticBorder2 = getSvekNode2().getMagneticBorder();

//		if (link.getEntity2().isGroup() && link.getEntity2().getUSymbol() instanceof USymbolFolder) {
//			final Cluster endCluster = bibliotekon.getCluster((Entity) link.getEntity2());
//			if (endCluster != null) {
//				final double deltaFolderH = endCluster.checkFolderPosition(dotPath.getEndPoint(),
//						ug.getStringBounder());
//				todraw = dotPath.copy();
//				todraw.moveEndPoint(0, deltaFolderH);
//				// moveEndY = deltaFolderH;
//			}
//		}

//		if (extremity1 instanceof Extremity && extremity2 instanceof Extremity) {
//			// http://forum.plantuml.net/9421/arrow-inversion-with-skinparam-linetype-ortho-missing-arrow
//			final XPoint2D p1 = ((Extremity) extremity1)
//					.isTooSmallSoGiveThePointCloserToThisOne(todraw.getStartPoint());
//			if (p1 != null)
//				todraw.forceStartPoint(p1.getX(), p1.getY());
//
//			final XPoint2D p2 = ((Extremity) extremity2).isTooSmallSoGiveThePointCloserToThisOne(todraw.getEndPoint());
//			if (p2 != null)
//				todraw.forceEndPoint(p2.getX(), p2.getY());
//
//		}

		final String comment = link.idCommentForSvg();
		final String tmp = uniq(ids, comment);
		todraw.setCommentAndCodeLine(tmp, link.getCodeLine());

		drawRainbow(ug.apply(new UTranslate(x, y)), color, arrowHeadColor, todraw, link.getSupplementaryColors(),
				stroke, magneticForce1, magneticForce2);

		ug = ug.apply(UStroke.simple()).apply(color);

		if (hasNoteLabelText() && this.labelXY != null
				&& (link.getNote() == null || link.getNote().getStrategy() != NoteLinkStrategy.HALF_NOT_PRINTED))
			this.labelText.drawU(ug.apply(new UTranslate(x + this.labelXY.getPosition().getX() + labelShield,
					y + this.labelXY.getPosition().getY() + labelShield)));

		if (this.startTailText != null && this.startTailLabelXY != null && this.startTailLabelXY.getPosition() != null)
			this.startTailText.drawU(ug.apply(new UTranslate(x + this.startTailLabelXY.getPosition().getX(),
					y + this.startTailLabelXY.getPosition().getY())));

		if (this.endHeadText != null && this.endHeadLabelXY != null && this.endHeadLabelXY.getPosition() != null)
			this.endHeadText.drawU(ug.apply(new UTranslate(x + this.endHeadLabelXY.getPosition().getX(),
					y + this.endHeadLabelXY.getPosition().getY())));

		if (linkType.getMiddleDecor() != LinkMiddleDecor.NONE) {
			final PointAndAngle middle = dotPath.getMiddle();
			final double angleRad = middle.getAngle();
			final double angleDeg = -angleRad * 180.0 / Math.PI;
			final UDrawable mi = linkType.getMiddleDecor().getMiddleFactory(arrowLollipopColor, backgroundColor)
					.createUDrawable(angleDeg - 45);
			mi.drawU(ug.apply(new UTranslate(x + middle.getX(), y + middle.getY())));
		}

		if (url != null)
			ug.closeUrl();

		if (link.getLinkConstraint() != null) {
			final double xConstraint = x + this.labelXY.getPosition().getX();
			final double yConstraint = y + this.labelXY.getPosition().getY();
//			ug.apply(new UTranslate(xConstraint, yConstraint)).draw(URectangle.build(10, 10));
			final List<XPoint2D> square = getSquare(xConstraint, yConstraint);
			final Set<XPoint2D> bez = todraw.sample();
			XPoint2D minPt = null;
			double minDist = Double.MAX_VALUE;
			for (XPoint2D pt : square)
				for (XPoint2D pt2 : bez) {
					final double distance = pt2.distance(pt);
					if (minPt == null || distance < minDist) {
						minPt = pt;
						minDist = distance;
					}
				}

			link.getLinkConstraint().setPosition(link, minPt);
			link.getLinkConstraint().drawMe(ug, skinParam);
		}

		ug.closeGroup();
	}

	public void computeKal() {
		if (kal1 != null) {
			final UTranslate tr = UTranslate.point(dotPath.getStartPoint()).compose(new UTranslate(dx, dy));
			kal1.setTranslate(tr, extremity1);
		}
		if (kal2 != null) {
			final UTranslate tr = UTranslate.point(dotPath.getEndPoint()).compose(new UTranslate(dx, dy));
			kal2.setTranslate(tr, extremity2);
		}
	}

	private List<XPoint2D> getSquare(double x, double y) {
		final List<XPoint2D> result = new ArrayList<>();
		result.add(new XPoint2D(x, y));
		result.add(new XPoint2D(x + 5, y));
		result.add(new XPoint2D(x + 10, y));
		result.add(new XPoint2D(x, y + 5));
		result.add(new XPoint2D(x + 10, y + 5));
		result.add(new XPoint2D(x, y + 10));
		result.add(new XPoint2D(x + 5, y + 10));
		result.add(new XPoint2D(x + 10, y + 10));
		return result;
	}

	private String uniq(final Set<String> ids, final String comment) {
		boolean changed = ids.add(comment);
		if (changed)
			return comment;

		int i = 1;
		while (true) {
			final String candidate = comment + "-" + i;
			changed = ids.add(candidate);
			if (changed)
				return candidate;

			i++;
		}
	}

	private void drawRainbow(UGraphic ug, HColor color, HColor headColor, DotPath todraw,
			List<Colors> supplementaryColors, UStroke stroke, UTranslate magneticForce1, UTranslate magneticForce2) {
		ug.draw(todraw);
		final LinkType linkType = link.getType();

		if (headColor.isTransparent()) {
			if (this.extremity1 instanceof ExtremityArrow) {
				final UGraphic ugHead = ug.apply(color).apply(stroke.onlyThickness());
				((ExtremityArrow) this.extremity1).drawLineIfTransparent(ugHead.apply(magneticForce1));
			}
		} else if (this.extremity1 != null) {
			UGraphic ugHead = ug.apply(headColor).apply(stroke.onlyThickness());
			if (linkType.getDecor2().isFill())
				ugHead = ugHead.apply(color.bg());
			else
				ugHead = ugHead.apply(HColors.none().bg());
			this.extremity1.drawU(ugHead.apply(magneticForce1));
		}

		if (headColor.isTransparent()) {
			if (this.extremity2 instanceof ExtremityArrow) {
				final UGraphic ugHead = ug.apply(color).apply(stroke.onlyThickness());
				((ExtremityArrow) this.extremity2).drawLineIfTransparent(ugHead.apply(magneticForce2));
			}
		} else if (this.extremity2 != null) {
			UGraphic ugHead = ug.apply(headColor).apply(stroke.onlyThickness());
			if (linkType.getDecor1().isFill())
				ugHead = ugHead.apply(color.bg());
			else
				ugHead = ugHead.apply(HColors.none().bg());
			this.extremity2.drawU(ugHead.apply(magneticForce2));
		}

		int i = 0;
		for (Colors colors : supplementaryColors) {
			ug.apply(new UTranslate(2 * (i + 1), 2 * (i + 1))).apply(colors.getColor(ColorType.LINE)).draw(todraw);
			i++;
		}
	}

	public boolean isInverted() {
		return link.isInverted();
	}

	private double getDecorDzeta() {
		final LinkType linkType = link.getType();
		final int size1 = linkType.getDecor1().getMargin();
		final int size2 = linkType.getDecor2().getMargin();
		return size1 + size2;
	}

	public double getHorizontalDzeta(StringBounder stringBounder) {
		if (startUid.equalsId(endUid))
			return getDecorDzeta();

		final ArithmeticStrategy strategy;
		if (isHorizontal())
			strategy = new ArithmeticStrategySum();
		else
			return 0;

		if (hasNoteLabelText())
			strategy.eat(labelText.calculateDimension(stringBounder).getWidth());

		if (startTailText != null)
			strategy.eat(startTailText.calculateDimension(stringBounder).getWidth());

		if (endHeadText != null)
			strategy.eat(endHeadText.calculateDimension(stringBounder).getWidth());

		return strategy.getResult() + getDecorDzeta();
	}

	private boolean isHorizontal() {
		return link.getLength() == 1;
	}

	public double getVerticalDzeta(StringBounder stringBounder) {
		if (startUid.equalsId(endUid))
			return getDecorDzeta();

		if (isHorizontal())
			return 0;

		final ArithmeticStrategy strategy = new ArithmeticStrategySum();
		if (hasNoteLabelText())
			strategy.eat(labelText.calculateDimension(stringBounder).getHeight());

		if (startTailText != null)
			strategy.eat(startTailText.calculateDimension(stringBounder).getHeight());

		if (endHeadText != null)
			strategy.eat(endHeadText.calculateDimension(stringBounder).getHeight());

		return strategy.getResult() + getDecorDzeta();
	}

	public void manageCollision(Collection<SvekNode> allNodes) {
		for (SvekNode sh : allNodes) {
			final Positionable cl = PositionableUtils.addMargin(sh, 8, 8);
			if (startTailText != null && startTailLabelXY != null && PositionableUtils.intersect(cl, startTailLabelXY))
				startTailLabelXY = PositionableUtils.moveAwayFrom(cl, startTailLabelXY);

			if (endHeadText != null && endHeadLabelXY != null && PositionableUtils.intersect(cl, endHeadLabelXY))
				endHeadLabelXY = PositionableUtils.moveAwayFrom(cl, endHeadLabelXY);

		}

	}

	private XPoint2D avoid2(XPoint2D move, Positionable pos, SvekNode sh) {
		final Oscillator oscillator = new Oscillator();
		final XPoint2D orig = new XPoint2D(move.x, move.y);
		while (cut(pos, sh)) {
			final XPoint2D m = oscillator.nextPosition();
			move = new XPoint2D(orig.x + m.x, orig.y + m.y);
		}
		return move;
	}

	private boolean cut(Positionable pos, SvekNode sh) {
		return BezierUtils.intersect(pos, sh) || tooClose(pos);
	}

	private boolean tooClose(Positionable pos) {
		final double dist = dotPath.getMinDist(BezierUtils.getCenter(pos));
		final XDimension2D dim = pos.getSize();
		// Log.println("dist=" + dist);
		return dist < (dim.getWidth() / 2 + 2) || dist < (dim.getHeight() / 2 + 2);
	}

	public void moveSvek(double deltaX, double deltaY) {
		this.dx += deltaX;
		this.dy += deltaY;
	}

	public final DotPath getDotPath() {
		final DotPath result = dotPath.copy();
		result.moveSvek(dx, dy);
		return result;
	}

	public int getLength() {
		return link.getLength();
	}

	public void setOpale(boolean opale) {
		this.link.setOpale(opale);
		this.opale = opale;

	}

	public boolean isOpale() {
		return opale;
	}

	public boolean isHorizontalSolitary() {
		return link.isHorizontalSolitary();
	}

	public boolean isLinkFromOrTo(Entity group) {
		return link.getEntity1() == group || link.getEntity2() == group;
	}

	public boolean hasEntryPoint() {
		return link.hasEntryPoint();
	}

	public void setProjectionCluster(Cluster cluster) {
		this.projectionCluster = cluster;

	}

	public boolean isHidden() {
		return link.isHidden();
	}

	public boolean sameConnections(SvekLine other) {
		return link.sameConnections(other.link);
	}

	private boolean isAutolink() {
		return link.getEntity1() == link.getEntity2();
	}

	public XPoint2D getMyPoint(Entity entity) {
		if (link.getEntity1() == entity)
			return moveDelta(dotPath.getStartPoint());

		if (link.getEntity2() == entity)
			return moveDelta(dotPath.getEndPoint());

		throw new IllegalArgumentException();
	}

	private XPoint2D moveDelta(XPoint2D pt) {
		return new XPoint2D(pt.getX() + dx, pt.getY() + dy);
	}

	public boolean isLink(Link link) {
		return this.link == link;
	}

	public XPoint2D getStartContactPoint() {
		if (dotPath == null)
			return null;
		final XPoint2D start = dotPath.getStartPoint();
		if (start == null)
			return null;

		return new XPoint2D(dx + start.getX(), dy + start.getY());
	}

	public XPoint2D getEndContactPoint() {
		final XPoint2D end = dotPath.getEndPoint();
		if (end == null)
			return null;

		return new XPoint2D(dx + end.getX(), dy + end.getY());
	}

//	public Entity getOther(Entity entity) {
//		if (link.contains(entity))
//			return link.getOther(entity);
//
//		return null;
//	}

	public StyleBuilder getCurrentStyleBuilder() {
		return link.getStyleBuilder();
	}

	public Stereotype getStereotype() {
		return link.getStereotype();
	}

	public void moveStartPoint(double dx, double dy) {
		dotPath.moveStartPoint(dx, dy);
	}

	public void moveEndPoint(double dx, double dy) {
		dotPath.moveEndPoint(dx, dy);
	}

}
