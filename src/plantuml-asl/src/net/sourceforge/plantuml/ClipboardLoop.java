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
package net.sourceforge.plantuml;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.sourceforge.plantuml.core.DiagramDescription;

public class ClipboardLoop {

	public static void runLoop() throws IOException, InterruptedException {
		final ClipboardLoop clipboardLoop = new ClipboardLoop();
		while (true) {
			final String text = clipboardLoop.getClipboardText();
			if (clipboardLoop.isTextOk(text)) {
				clipboardLoop.runText(text);
			}
			Thread.sleep(10000L);
		}
	}

	public static void runOnce() throws IOException, InterruptedException {
		final ClipboardLoop clipboardLoop = new ClipboardLoop();
		final String text = clipboardLoop.getClipboardText();
		if (clipboardLoop.isTextOk(text)) {
			clipboardLoop.runText(text);
		} else {
			clipboardLoop.setClipboardImage(new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB));
		}

	}

	private boolean isTextOk(String text) {
		if (text == null) {
			return false;
		}
		return text.startsWith("@start");
	}

	private void runText(String text) throws IOException, InterruptedException {
		Log.info("Getting some text from clipboard");
		final SourceStringReader source = new SourceStringReader(text);
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final DiagramDescription desc = source.outputImage(baos);
		if (desc == null) {
			Log.info("No image generated");
		} else {
			Log.info("Image ok " + desc.getDescription());
			final byte[] data = baos.toByteArray();
			baos.close();
			final ByteArrayInputStream bais = new ByteArrayInputStream(data);
			final BufferedImage image = ImageIO.read(bais);
			setClipboardImage(image);
			bais.close();
			Log.info("Image copied in clipboard");
		}
	}

	private String getClipboardText() {
		final Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
		try {
			if (t != null && t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				final String text = (String) t.getTransferData(DataFlavor.stringFlavor);
				return text;
			}
		} catch (UnsupportedFlavorException e) {
			Log.error(e.toString());
		} catch (IOException e) {
			Log.error(e.toString());
		}
		return null;
	}

	private void setClipboardImage(BufferedImage image) {
		final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(new ImageSelection(image), null);
	}

}
