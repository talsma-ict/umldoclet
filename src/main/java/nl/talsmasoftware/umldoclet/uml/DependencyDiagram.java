package nl.talsmasoftware.umldoclet.uml;

import nl.talsmasoftware.umldoclet.configuration.Configuration;

import java.io.File;

public class DependencyDiagram extends Diagram {

    private File pumlFile = null;

    public DependencyDiagram(Configuration config) {
        super(config);
    }

    @Override
    protected File getPlantUmlFile() {
        if (pumlFile == null) {
            pumlFile = new File(getConfiguration().destinationDirectory(), "overview-dependencies.puml");
        }
        return pumlFile;
    }

}
