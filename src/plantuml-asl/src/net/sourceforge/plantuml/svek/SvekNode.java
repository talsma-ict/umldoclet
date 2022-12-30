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

import java.util.List;

import net.sourceforge.plantuml.Direction;
import net.sourceforge.plantuml.Hideable;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.awt.geom.XPoint2D;
import net.sourceforge.plantuml.baraye.EntityImp;
import net.sourceforge.plantuml.baraye.IGroup;
import net.sourceforge.plantuml.baraye.ILeaf;
import net.sourceforge.plantuml.cucadiagram.EntityPosition;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.posimo.Positionable;
import net.sourceforge.plantuml.svek.image.EntityImageDescription;
import net.sourceforge.plantuml.svek.image.EntityImageLollipopInterface;
import net.sourceforge.plantuml.svek.image.EntityImagePort;
import net.sourceforge.plantuml.svek.image.EntityImageStateBorder;
import net.sourceforge.plantuml.ugraphic.Shadowable;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UPolygon;

public class SvekNode implements Positionable, Hideable {

	private final ShapeType type;
	private XDimension2D dimImage;

	private final String uid;
	private final int color;

	private double minX;
	private double minY;
	private Margins shield;

	private final EntityPosition entityPosition;
	private final IEntityImage image;
	private final StringBounder stringBounder;

	public EntityPosition getEntityPosition() {
		return entityPosition;
	}

	private Cluster cluster;

	public final Cluster getCluster() {
		return cluster;
	}

	public final void setCluster(Cluster cluster) {
		this.cluster = cluster;
	}

	@Override
	public String toString() {
		return super.toString() + " " + image + " " + type;
	}

	private final ILeaf leaf;
	private final IGroup group;

	SvekNode(ILeaf ent, IEntityImage image, ColorSequence colorSequence, StringBounder stringBounder) {
		this.stringBounder = stringBounder;
		this.entityPosition = ent.getEntityPosition();
		this.image = image;
		this.type = image.getShapeType();

		this.color = colorSequence.getValue();
		this.uid = String.format("sh%04d", color);

		if (((EntityImp) ent).getOriginalGroup() == null) {
			this.group = null;
			this.leaf = ent;
		} else {
			this.group = ((EntityImp) ent).getOriginalGroup();
			this.leaf = null;
		}
	}

	private XDimension2D getDimImage() {
		if (dimImage == null)
			this.dimImage = image.calculateDimension(stringBounder);
		return dimImage;
	}

	public final ShapeType getType() {
		return type;
	}

	public final double getWidth() {
		return getDimImage().getWidth();
	}

	public final double getHeight() {
		return getDimImage().getHeight();
	}

	public void appendShape(StringBuilder sb, StringBounder stringBounder) {
		if (type == ShapeType.RECTANGLE_HTML_FOR_PORTS) {
			appendLabelHtmlSpecialForLink(sb, stringBounder);
			SvekUtils.println(sb);
			return;
		}
		if (type == ShapeType.RECTANGLE_PORT) {
			appendLabelHtmlSpecialForPort(sb, stringBounder);
			SvekUtils.println(sb);
			return;
		}
		if (type == ShapeType.RECTANGLE_WITH_CIRCLE_INSIDE) {
			appendHtml(sb);
			SvekUtils.println(sb);
			return;
		}
		if (type == ShapeType.RECTANGLE && shield().isZero() == false) {
			appendHtml(sb);
			SvekUtils.println(sb);
			return;
		}
		sb.append(uid);
		sb.append(" [");
		appendShapeInternal(sb);
		sb.append(",");
		sb.append("label=\"\"");
		sb.append(",");
		sb.append("width=" + SvekUtils.pixelToInches(getWidth()));
		sb.append(",");
		sb.append("height=" + SvekUtils.pixelToInches(getHeight()));
		sb.append(",");
		sb.append("color=\"" + StringUtils.sharp000000(color) + "\"");
		sb.append("];");
		SvekUtils.println(sb);
	}

	private double getMaxWidthFromLabelForEntryExit(StringBounder stringBounder) {
		if (image instanceof EntityImagePort) {
			final EntityImagePort im = (EntityImagePort) image;
			return im.getMaxWidthFromLabelForEntryExit(stringBounder);
		}
		if (image instanceof EntityImageStateBorder) {
			final EntityImageStateBorder im = (EntityImageStateBorder) image;
			return im.getMaxWidthFromLabelForEntryExit(stringBounder);
		}
		throw new UnsupportedOperationException();
	}

	private void appendLabelHtmlSpecialForPort(StringBuilder sb, StringBounder stringBounder) {
		final int width1 = (int) getWidth();
		final int width2 = (int) getMaxWidthFromLabelForEntryExit(stringBounder);
		if (width2 > 40)
			appendLabelHtmlSpecialForPortHtml(sb, stringBounder, width2 - 40);
		else
			appendLabelHtmlSpecialForPortBasic(sb, stringBounder);
	}

	private void appendLabelHtmlSpecialForPortHtml(StringBuilder sb, StringBounder stringBounder, int fullWidth) {
		if (fullWidth < 10)
			fullWidth = 10;
		sb.append(uid);
		sb.append(" [");
		sb.append("shape=plaintext");
		sb.append(",");
		sb.append("label=<");
		sb.append("<TABLE BORDER=\"0\" CELLBORDER=\"0\" CELLSPACING=\"0\" CELLPADDING=\"0\">");
		sb.append("<TR><TD WIDTH=\"" + fullWidth + "\" HEIGHT=\"1\" COLSPAN=\"3\"></TD></TR>");
		sb.append("<TR><TD></TD><TD FIXEDSIZE=\"TRUE\" PORT=\"P\"  BORDER=\"1\" COLOR=\""
				+ StringUtils.sharp000000(color) + "\" WIDTH=\"" + (int) getWidth() + "\" HEIGHT=\"" + (int) getHeight()
				+ "\"></TD><TD></TD></TR>");
		sb.append("<TR><TD WIDTH=\"" + fullWidth + "\" HEIGHT=\"1\" COLSPAN=\"3\"></TD></TR>");
		sb.append("</TABLE>");
		sb.append(">];");
	}

	private void appendLabelHtmlSpecialForPortBasic(StringBuilder sb, StringBounder stringBounder) {
		sb.append(uid);
		sb.append(" [");
		sb.append("shape=rect");
		sb.append(",");
		sb.append("label=\"\"");
		sb.append(",");
		sb.append("width=" + SvekUtils.pixelToInches(getWidth()));
		sb.append(",");
		sb.append("height=" + SvekUtils.pixelToInches(getHeight()));
		sb.append(",");
		sb.append("color=\"" + StringUtils.sharp000000(color) + "\"");
		sb.append("];");
	}

	private Margins shield() {
		if (shield == null) {
			this.shield = image.getShield(stringBounder);
			if (shield.isZero() == false && type != ShapeType.RECTANGLE && type != ShapeType.RECTANGLE_HTML_FOR_PORTS
					&& type != ShapeType.RECTANGLE_WITH_CIRCLE_INSIDE)
				throw new IllegalStateException();
		}

		return shield;
	}

	private void appendHtml(StringBuilder sb) {
		sb.append(uid);
		sb.append(" [");
		sb.append("shape=plaintext,");
		sb.append("label=<");
		appendLabelHtml(sb);
		sb.append(">");
		sb.append("];");
		SvekUtils.println(sb);

	}

	private void appendLabelHtml(StringBuilder sb) {
		// Log.println("shield=" + shield);
		sb.append("<TABLE BORDER=\"0\" CELLBORDER=\"0\" CELLSPACING=\"0\" CELLPADDING=\"0\">");
		sb.append("<TR>");
		appendTd(sb);
		appendTd(sb, 1, shield().getY1());
		appendTd(sb);
		sb.append("</TR>");
		sb.append("<TR>");
		appendTd(sb, shield().getX1(), 1);
		sb.append("<TD BGCOLOR=\"" + StringUtils.sharp000000(color) + "\"");
		sb.append(" FIXEDSIZE=\"TRUE\" WIDTH=\"" + getWidth() + "\" HEIGHT=\"" + getHeight() + "\"");
		sb.append(" PORT=\"h\">");
		sb.append("</TD>");
		appendTd(sb, shield().getX2(), 1);
		sb.append("</TR>");
		sb.append("<TR>");
		appendTd(sb);
		appendTd(sb, 1, shield().getY2());
		appendTd(sb);
		sb.append("</TR>");
		sb.append("</TABLE>");
	}

	private void appendLabelHtmlSpecialForLink(StringBuilder sb, StringBounder stringBounder) {
		final Ports ports = ((WithPorts) this.image).getPorts(stringBounder);

		sb.append(uid);
		sb.append(" [");
		sb.append("shape=plaintext,");
		// sb.append("color=\"" + StringUtils.getAsHtml(color) + "\",");
		sb.append("label=<");
		sb.append("<TABLE BGCOLOR=\"" + StringUtils.sharp000000(color)
				+ "\" BORDER=\"0\" CELLBORDER=\"0\" CELLSPACING=\"0\" CELLPADDING=\"0\">");
		double position = 0;
		for (PortGeometry geom : ports.getAllPortGeometry()) {
			final String portId = geom.getId();
			final double missing = geom.getPosition() - position;
			appendTr(sb, null, missing);
			appendTr(sb, portId, geom.getHeight());
			position = geom.getLastY();
		}
		appendTr(sb, null, getHeight() - position);
		sb.append("</TABLE>");
		sb.append(">");
		sb.append("];");
		SvekUtils.println(sb);
	}

	private void appendTr(StringBuilder sb, String portId, double height) {
		if (height <= 0)
			return;

		sb.append("<TR>");
		sb.append("<TD ");
		sb.append(" FIXEDSIZE=\"TRUE\" WIDTH=\"" + getWidth() + "\" HEIGHT=\"" + height + "\"");
		if (portId != null)
			sb.append(" PORT=\"" + portId + "\"");

		sb.append(">");
		sb.append("</TD>");
		sb.append("</TR>");
	}

	private void appendTd(StringBuilder sb, double w, double h) {
		sb.append("<TD");
		sb.append(" FIXEDSIZE=\"TRUE\" WIDTH=\"" + w + "\" HEIGHT=\"" + h + "\"");
		sb.append(">");
		sb.append("</TD>");
	}

	private void appendTd(StringBuilder sb) {
		sb.append("<TD>");
		sb.append("</TD>");
	}

	private void appendShapeInternal(StringBuilder sb) {
		if (type == ShapeType.RECTANGLE && shield().isZero() == false)
			throw new UnsupportedOperationException();
		else if (type == ShapeType.RECTANGLE || type == ShapeType.RECTANGLE_WITH_CIRCLE_INSIDE
				|| type == ShapeType.FOLDER)
			sb.append("shape=rect");
		else if (type == ShapeType.RECTANGLE_HTML_FOR_PORTS)
			throw new UnsupportedOperationException();
		else if (type == ShapeType.OCTAGON)
			sb.append("shape=octagon");
		else if (type == ShapeType.HEXAGON)
			sb.append("shape=hexagon");
		else if (type == ShapeType.DIAMOND)
			sb.append("shape=diamond");
		else if (type == ShapeType.CIRCLE)
			sb.append("shape=circle");
		else if (type == ShapeType.OVAL)
			sb.append("shape=ellipse");
		else if (type == ShapeType.ROUND_RECTANGLE)
			sb.append("shape=rect,style=rounded");
		else
			throw new IllegalStateException(type.toString());

	}

	public final String getUid() {
		if (uid == null)
			throw new IllegalStateException();

		return uid;
	}

	public final double getMinX() {
		return minX;
	}

	public final double getMinY() {
		return minY;
	}

	public IEntityImage getImage() {
		return image;
	}

	public XPoint2D getPosition() {
		return new XPoint2D(minX, minY);
	}

	public XDimension2D getSize() {
		return getDimImage();
	}

	public ClusterPosition getClusterPosition() {
		return new ClusterPosition(minX, minY, minX + getWidth(), minY + getHeight());
	}

	public boolean isShielded() {
		return shield().isZero() == false;
	}

	public void moveSvek(double deltaX, double deltaY) {
		this.minX += deltaX;
		this.minY += deltaY;
	}

	public boolean isHidden() {
		return image.isHidden();
	}

	private Shadowable polygon;

	public void setPolygon(double minX, double minY, List<XPoint2D> points) {
		this.polygon = new UPolygon(points).translate(-minX, -minY);
	}

	public Shadowable getPolygon() {
		return polygon;
	}

	public XPoint2D getPoint2D(double x, double y) {
		return new XPoint2D(minX + x, minY + y);
	}

	public XPoint2D projection(XPoint2D pt, StringBounder stringBounder) {
		if (getType() != ShapeType.FOLDER)
			return pt;

		final ClusterPosition clusterPosition = new ClusterPosition(minX, minY, minX + getWidth(), minY + getHeight());
		if (clusterPosition.isPointJustUpper(pt)) {
			final XDimension2D dimName = ((EntityImageDescription) image).getNameDimension(stringBounder);
			if (pt.getX() < minX + dimName.getWidth())
				return pt;

			return new XPoint2D(pt.getX(), pt.getY() + dimName.getHeight() + 4);
		}
		return pt;
	}

	public double getOverscanX(StringBounder stringBounder) {
		return image.getOverscanX(stringBounder);
	}

	public void addImpact(double angle) {
		((EntityImageLollipopInterface) image).addImpact(angle);
	}

	public void drawKals(UGraphic ug) {
		if (leaf instanceof EntityImp == false)
			return;

		drawList(ug, ((EntityImp) leaf).getKals(Direction.DOWN));
		drawList(ug, ((EntityImp) leaf).getKals(Direction.UP));
		drawList(ug, ((EntityImp) leaf).getKals(Direction.LEFT));
		drawList(ug, ((EntityImp) leaf).getKals(Direction.RIGHT));

	}

	public void fixOverlap() {
		if (leaf instanceof EntityImp == false)
			return;

		fixHoverlap(((EntityImp) leaf).getKals(Direction.DOWN));
		fixHoverlap(((EntityImp) leaf).getKals(Direction.UP));
	}

	private void fixHoverlap(final List<Kal> list) {
		final LineOfSegments los = new LineOfSegments();
		for (Kal kal : list)
			los.addSegment(kal.getX1(), kal.getX2());

		final double[] res = los.solveOverlaps();
		for (int i = 0; i < list.size(); i++) {
			final Kal kal = list.get(i);
			final double diff = res[i] - kal.getX1();
			kal.moveX(diff);
		}
	}

	private void drawList(UGraphic ug, final List<Kal> list) {
		for (Kal kal : list)
			kal.drawU(ug);
	}
}
