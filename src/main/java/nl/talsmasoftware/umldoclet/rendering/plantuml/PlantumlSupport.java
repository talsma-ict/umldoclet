package nl.talsmasoftware.umldoclet.rendering.plantuml;

import java.io.OutputStream;

/**
 * Created by talsma.s on 03-05-2016.
 */
public class PlantumlSupport {

    public static boolean isPlantumlDetected() {
        return false; // TODO one-time reflection to detect required PlantUML features on the classpath.
    }

    public static void writePngImage(String umlDiagramContent, OutputStream pngOutput) {
        // TODO delegate to PlantUML library.
    }

}
