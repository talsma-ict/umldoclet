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
package net.sourceforge.plantuml.svek;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import net.sourceforge.plantuml.AlignmentParam;
import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.Direction;
import net.sourceforge.plantuml.Hideable;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.LineParam;
import net.sourceforge.plantuml.Log;
import net.sourceforge.plantuml.Pragma;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.UmlDiagramType;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.awt.geom.Dimension2D;
import net.sourceforge.plantuml.command.Position;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.EntityPort;
import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.cucadiagram.IGroup;
import net.sourceforge.plantuml.cucadiagram.LeafType;
import net.sourceforge.plantuml.cucadiagram.Link;
import net.sourceforge.plantuml.cucadiagram.LinkArrow;
import net.sourceforge.plantuml.cucadiagram.LinkDecor;
import net.sourceforge.plantuml.cucadiagram.LinkMiddleDecor;
import net.sourceforge.plantuml.cucadiagram.LinkType;
import net.sourceforge.plantuml.cucadiagram.NoteLinkStrategy;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.cucadiagram.dot.DotSplines;
import net.sourceforge.plantuml.cucadiagram.dot.GraphvizVersion;
import net.sourceforge.plantuml.descdiagram.command.StringWithArrow;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.graphic.USymbolFolder;
import net.sourceforge.plantuml.graphic.VerticalAlignment;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.posimo.BezierUtils;
import net.sourceforge.plantuml.posimo.DotPath;
import net.sourceforge.plantuml.posimo.Moveable;
import net.sourceforge.plantuml.posimo.Positionable;
import net.sourceforge.plantuml.posimo.PositionableUtils;
import net.sourceforge.plantuml.skin.VisibilityModifier;
import net.sourceforge.plantuml.skin.rose.Rose;
import net.sourceforge.plantuml.style.StyleBuilder;
import net.sourceforge.plantuml.svek.extremity.Extremity;
import net.sourceforge.plantuml.svek.extremity.ExtremityFactory;
import net.sourceforge.plantuml.svek.extremity.ExtremityFactoryExtends;
import net.sourceforge.plantuml.svek.extremity.ExtremityOther;
import net.sourceforge.plantuml.svek.image.EntityImageNoteLink;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UGroupType;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UPolygon;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorNone;

public class SvekLine implements Moveable, Hideable, GuideLine {

	private static final Dimension2DDouble CONSTRAINT_SPOT = new Dimension2DDouble(10, 10);

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

	private UDrawable extremity2;
	private UDrawable extremity1;

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
		final Point2D start = dotPath.getStartPoint();
		final Point2D end = dotPath.getEndPoint();
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
		final Point2D start = dotPath.getStartPoint();
		final Point2D end = dotPath.getEndPoint();
		final double ang = Math.atan2(end.getX() - start.getX(), end.getY() - start.getY());
		return ang;
	}

	private Cluster getCluster2(Bibliotekon bibliotekon, IEntity entityMutable) {
		for (Cluster cl : bibliotekon.allCluster())
			if (cl.getGroups().contains(entityMutable))
				return cl;

		throw new IllegalArgumentException();
	}

	public SvekLine(Link link, ColorSequence colorSequence, ISkinParam skinParam, StringBounder stringBounder,
			FontConfiguration font, Bibliotekon bibliotekon, Pragma pragma) {

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
			final HorizontalAlignment alignment = getMessageTextAlignment(link.getUmlDiagramType(), skinParam);
			final boolean hasSeveralGuideLines = link.getLabel().hasSeveralGuideLines();
			final TextBlock block;
			if (hasSeveralGuideLines)
				block = StringWithArrow.addSeveralMagicArrows(link.getLabel(), this, font, alignment, skinParam);
			else
				block = link.getLabel().create9(font, alignment, skinParam, skinParam.maxMessageSize());

			labelOnly = addVisibilityModifier(block, link, skinParam);
			if (getLinkArrow() != LinkArrow.NONE_OR_SEVERAL && hasSeveralGuideLines == false)
				labelOnly = StringWithArrow.addMagicArrow(labelOnly, this, font);

		}

		final TextBlock noteOnly;
		if (link.getNote() == null) {
			noteOnly = TextBlockUtils.EMPTY_TEXT_BLOCK;
		} else {
			noteOnly = new EntityImageNoteLink(link.getNote(), link.getNoteColors(), skinParam, link.getStyleBuilder());
			if (link.getNoteLinkStrategy() == NoteLinkStrategy.HALF_NOT_PRINTED
					|| link.getNoteLinkStrategy() == NoteLinkStrategy.HALF_PRINTED_FULL)
				divideLabelWidthByTwo = true;

		}

		if (link.getNotePosition() == Position.LEFT)
			labelText = TextBlockUtils.mergeLR(noteOnly, labelOnly, VerticalAlignment.CENTER);
		else if (link.getNotePosition() == Position.RIGHT)
			labelText = TextBlockUtils.mergeLR(labelOnly, noteOnly, VerticalAlignment.CENTER);
		else if (link.getNotePosition() == Position.TOP)
			labelText = TextBlockUtils.mergeTB(noteOnly, labelOnly, HorizontalAlignment.CENTER);
		else
			labelText = TextBlockUtils.mergeTB(labelOnly, noteOnly, HorizontalAlignment.CENTER);

		if (link.getQualifier1() == null)
			startTailText = null;
		else
			startTailText = Display.getWithNewlines(link.getQualifier1()).create(font, HorizontalAlignment.CENTER,
					skinParam);

		if (link.getQualifier2() == null)
			endHeadText = null;
		else
			endHeadText = Display.getWithNewlines(link.getQualifier2()).create(font, HorizontalAlignment.CENTER,
					skinParam);

		if (link.getType().getMiddleDecor() == LinkMiddleDecor.NONE)
			this.labelShield = 0;
		else
			this.labelShield = 7;

	}

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
			Dimension2D dimNote = hasNoteLabelText() ? labelText.calculateDimension(stringBounder) : CONSTRAINT_SPOT;
			dimNote = Dimension2DDouble.delta(dimNote, 2 * labelShield);

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

	private Dimension2D eventuallyDivideByTwo(Dimension2D dim) {
		if (divideLabelWidthByTwo)
			return new Dimension2DDouble(dim.getWidth() / 2, dim.getHeight());

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

	public static void appendTable(StringBuilder sb, Dimension2D dim, int col, GraphvizVersion graphvizVersion) {
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

	private UDrawable getExtremity(LinkDecor decor, PointListIterator pointListIterator, final Point2D center,
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
			final List<Point2D.Double> points = pointListIterator.next();
			if (points.size() == 0)
				return extremityFactory.createUDrawable(center, angle, null);

			final Point2D p0 = points.get(0);
			final Point2D p1 = points.get(1);
			final Point2D p2 = points.get(2);
			Side side = null;
			if (nodeContact != null)
				side = nodeContact.getClusterPosition().getClosestSide(p1);

			return extremityFactory.createUDrawable(p0, p1, p2, side);
		} else if (decor == LinkDecor.NONE) {
			final UPolygon sh = new UPolygon(pointListIterator.cloneMe().next());
			final Point2D contact = sh.checkMiddleContactForSpecificTriangle(center);
			if (contact != null) {
				return new UDrawable() {
					public void drawU(UGraphic ug) {
						ULine line = new ULine(contact.getX() - center.getX(), contact.getY() - center.getY());
						ug = ug.apply(new UTranslate(center));
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

		if (DotPath.isPathConsistent(path.getSvg()) == false)
			return;

		dotPath = new DotPath(path);

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
		dotPath = dotPath.simulateCompound(lhead, ltail);

		final SvgResult lineSvg = fullSvg.substring(end);
		PointListIterator pointListIterator = lineSvg.getPointsWithThisColor(lineColor);

		final LinkType linkType = link.getType();
		this.extremity1 = getExtremity(linkType.getDecor2(), pointListIterator, dotPath.getStartPoint(),
				dotPath.getStartAngle() + Math.PI, ltail, bibliotekon.getNode(link.getEntity1()));
		this.extremity2 = getExtremity(linkType.getDecor1(), pointListIterator, dotPath.getEndPoint(),
				dotPath.getEndAngle(), lhead, bibliotekon.getNode(link.getEntity2()));

		if (link.getEntity1().getLeafType() == LeafType.LOLLIPOP_HALF)
			bibliotekon.getNode(link.getEntity1()).addImpact(dotPath.getStartAngle() + Math.PI);

		if (link.getEntity2().getLeafType() == LeafType.LOLLIPOP_HALF)
			bibliotekon.getNode(link.getEntity2()).addImpact(dotPath.getEndAngle());

		if (extremity1 instanceof Extremity && extremity2 instanceof Extremity) {
			final Point2D p1 = ((Extremity) extremity1).somePoint();
			final Point2D p2 = ((Extremity) extremity2).somePoint();
			if (p1 != null && p2 != null) {
				// http://plantuml.sourceforge.net/qa/?qa=4240/some-relations-point-wrong-direction-when-the-linetype-ortho
				final double dist1start = p1.distance(dotPath.getStartPoint());
				final double dist1end = p1.distance(dotPath.getEndPoint());
				final double dist2start = p2.distance(dotPath.getStartPoint());
				final double dist2end = p2.distance(dotPath.getEndPoint());
				if (dist1start > dist1end && dist2end > dist2start) {
					pointListIterator = lineSvg.getPointsWithThisColor(lineColor);
					this.extremity2 = getExtremity(linkType.getDecor1(), pointListIterator, dotPath.getEndPoint(),
							dotPath.getEndAngle(), lhead, bibliotekon.getNode(link.getEntity2()));
					this.extremity1 = getExtremity(linkType.getDecor2(), pointListIterator, dotPath.getStartPoint(),
							dotPath.getStartAngle() + Math.PI, ltail, bibliotekon.getNode(link.getEntity1()));
				}
			}

		}

		if (hasNoteLabelText() || link.getLinkConstraint() != null) {
			final Point2D pos = getXY(fullSvg, this.noteLabelColor);
			if (pos != null) {
//				corner1.manage(pos);
				this.labelXY = hasNoteLabelText() ? TextBlockUtils.asPositionable(labelText, stringBounder, pos)
						: TextBlockUtils.asPositionable(CONSTRAINT_SPOT, stringBounder, pos);
			}
		}

		if (this.startTailText != null) {
			final Point2D pos = getXY(fullSvg, this.startTailColor);
			if (pos != null) {
//				corner1.manage(pos);
				this.startTailLabelXY = TextBlockUtils.asPositionable(startTailText, stringBounder, pos);
			}
		}

		if (this.endHeadText != null) {
			final Point2D pos = getXY(fullSvg, this.endHeadColor);
			if (pos != null) {
//				corner1.manage(pos);
				this.endHeadLabelXY = TextBlockUtils.asPositionable(endHeadText, stringBounder, pos);
//				corner1.manage(pos.getX() - 15, pos.getY());
			}
		}

		if (isOpalisable() == false)
			setOpale(false);

	}

	private boolean isOpalisable() {
		return dotPath.getBeziers().size() <= 1;
	}

	private Point2D.Double getXY(SvgResult svgResult, int color) {
		final int idx = svgResult.getIndexFromColor(color);
		if (idx == -1)
			return null;

		return SvekUtils.getMinXY(svgResult.substring(idx).extractList(SvgResult.POINTS_EQUALS));

	}

	public void drawU(UGraphic ug, UStroke suggestedStroke, HColor color, Set<String> ids) {
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
				"link " + link.getEntity1().getCode() + " " + link.getEntity2().getCode() + " selected");
		typeIDent.put(UGroupType.ID, "link_" + link.getEntity1().getCode() + "_" + link.getEntity2().getCode());
		ug.startGroup(typeIDent);
		double x = 0;
		double y = 0;
		final Url url = link.getUrl();
		if (url != null)
			ug.startUrl(url);

		if (link.isAutoLinkOfAGroup()) {
			final Cluster cl = bibliotekon.getCluster((IGroup) link.getEntity1());
			if (cl != null) {
				x += cl.getWidth();
				x -= dotPath.getStartPoint().getX() - cl.getMinX();
			}
		}

		x += dx;
		y += dy;

		if (this.link.getColors() != null) {
			final HColor newColor = this.link.getColors().getColor(ColorType.ARROW, ColorType.LINE);
			if (newColor != null)
				color = newColor;
		} else if (this.link.getSpecificColor() != null)
			color = this.link.getSpecificColor();

		ug = ug.apply(new HColorNone().bg()).apply(color);
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

		DotPath todraw = dotPath;
		if (link.getEntity2().isGroup() && link.getEntity2().getUSymbol() instanceof USymbolFolder) {
			final Cluster endCluster = bibliotekon.getCluster((IGroup) link.getEntity2());
			if (endCluster != null) {
				final double deltaFolderH = endCluster.checkFolderPosition(dotPath.getEndPoint(),
						ug.getStringBounder());
				todraw = dotPath.copy();
				todraw.moveEndPoint(0, deltaFolderH);
				// moveEndY = deltaFolderH;
			}
		}

		if (extremity1 instanceof Extremity && extremity2 instanceof Extremity) {
			// http://forum.plantuml.net/9421/arrow-inversion-with-skinparam-linetype-ortho-missing-arrow
			final Point2D p1 = ((Extremity) extremity1).isTooSmallSoGiveThePointCloserToThisOne(todraw.getStartPoint());
			if (p1 != null)
				todraw.forceStartPoint(p1.getX(), p1.getY());

			final Point2D p2 = ((Extremity) extremity2).isTooSmallSoGiveThePointCloserToThisOne(todraw.getEndPoint());
			if (p2 != null)
				todraw.forceEndPoint(p2.getX(), p2.getY());

		}

		final String comment = link.idCommentForSvg();
		final String tmp = uniq(ids, comment);
		todraw.setCommentAndCodeLine(tmp, link.getCodeLine());

		drawRainbow(ug.apply(new UTranslate(x, y)), color, todraw, link.getSupplementaryColors(), stroke);

		ug = ug.apply(new UStroke()).apply(color);

		if (hasNoteLabelText() && this.labelXY != null
				&& link.getNoteLinkStrategy() != NoteLinkStrategy.HALF_NOT_PRINTED)
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
//			ug.apply(new UTranslate(xConstraint, yConstraint)).draw(new URectangle(10, 10));
			final List<Point2D> square = getSquare(xConstraint, yConstraint);
			final Set<Point2D> bez = dotPath.sample();
			Point2D minPt = null;
			double minDist = Double.MAX_VALUE;
			for (Point2D pt : square)
				for (Point2D pt2 : bez) {
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

	private List<Point2D> getSquare(double x, double y) {
		final List<Point2D> result = new ArrayList<>();
		result.add(new Point2D.Double(x, y));
		result.add(new Point2D.Double(x + 5, y));
		result.add(new Point2D.Double(x + 10, y));
		result.add(new Point2D.Double(x, y + 5));
		result.add(new Point2D.Double(x + 10, y + 5));
		result.add(new Point2D.Double(x, y + 10));
		result.add(new Point2D.Double(x + 5, y + 10));
		result.add(new Point2D.Double(x + 10, y + 10));
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

	private void drawRainbow(UGraphic ug, HColor color, DotPath todraw, List<Colors> supplementaryColors,
			UStroke stroke) {
		ug.draw(todraw);
		final LinkType linkType = link.getType();

		if (this.extremity2 != null) {
			UGraphic ug2 = ug.apply(color).apply(stroke.onlyThickness());
			if (linkType.getDecor1().isFill())
				ug2 = ug2.apply(color.bg());
			else
				ug2 = ug2.apply(new HColorNone().bg());

			// System.err.println("Line::draw EXTREMITY1");
			this.extremity2.drawU(ug2);
		}
		if (this.extremity1 != null) {
			UGraphic ug2 = ug.apply(color).apply(stroke.onlyThickness());
			if (linkType.getDecor2().isFill())
				ug2 = ug2.apply(color.bg());
			else
				ug2 = ug2.apply(new HColorNone().bg());

			// System.err.println("Line::draw EXTREMITY2");
			this.extremity1.drawU(ug2);
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

		// final Positionable start = getStartTailPositionnable();
		// if (start != null) {
		// for (Shape sh : allShapes) {
		// if (cut(start, sh)) {
		// avoid(startTailLabelXY, start, sh);
		// }
		// }
		// }
		//
		// final Positionable end = getEndHeadPositionnable();
		// if (end != null) {
		// for (Shape sh : allShapes) {
		// if (cut(end, sh)) {
		// avoid(endHeadLabelXY, end, sh);
		// }
		// }
		// }

	}

	private void avoid(Point2D.Double move, Positionable pos, SvekNode sh) {
		final Oscillator oscillator = new Oscillator();
		final Point2D.Double orig = new Point2D.Double(move.x, move.y);
		while (cut(pos, sh)) {
			final Point2D.Double m = oscillator.nextPosition();
			move.setLocation(orig.x + m.x, orig.y + m.y);
		}
	}

	private boolean cut(Positionable pos, SvekNode sh) {
		return BezierUtils.intersect(pos, sh) || tooClose(pos);
	}

	private boolean tooClose(Positionable pos) {
		final double dist = dotPath.getMinDist(BezierUtils.getCenter(pos));
		final Dimension2D dim = pos.getSize();
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

	public boolean isLinkFromOrTo(IEntity group) {
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

	public Point2D getMyPoint(IEntity entity) {
		if (link.getEntity1() == entity)
			return moveDelta(dotPath.getStartPoint());

		if (link.getEntity2() == entity)
			return moveDelta(dotPath.getEndPoint());

		throw new IllegalArgumentException();
	}

	private Point2D moveDelta(Point2D pt) {
		return new Point2D.Double(pt.getX() + dx, pt.getY() + dy);
	}

	public boolean isLink(Link link) {
		return this.link == link;
	}

	public Point2D getStartContactPoint() {
		if (dotPath == null)
			return null;
		final Point2D start = dotPath.getStartPoint();
		if (start == null)
			return null;

		return new Point2D.Double(dx + start.getX(), dy + start.getY());
	}

	public Point2D getEndContactPoint() {
		final Point2D end = dotPath.getEndPoint();
		if (end == null)
			return null;

		return new Point2D.Double(dx + end.getX(), dy + end.getY());
	}

	public IEntity getOther(IEntity entity) {
		if (link.contains(entity))
			return link.getOther(entity);

		return null;
	}

	public StyleBuilder getCurrentStyleBuilder() {
		return link.getStyleBuilder();
	}

	public Stereotype getStereotype() {
		return link.getStereotype();
	}

}
