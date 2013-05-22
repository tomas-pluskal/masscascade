package uk.ac.ebi.masscascade;

import org.junit.Test;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import uk.ac.ebi.masscascade.core.container.memory.MemoryContainerBuilder;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.Scan;
import uk.ac.ebi.masscascade.interfaces.container.RawContainer;
import uk.ac.ebi.masscascade.io.PsiMzmlReader;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.threed.ThreeDChart;
import uk.ac.ebi.masscascade.threed.ThreeDParser;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ThreeDTest {

    @Test
    public void testThreeD() throws InterruptedException {

        // get mzML resource
        URL url = this.getClass().getResource("C:\\Users\\stephan\\Code\\IdeaProjects\\MassCascade\\MassCascadeCore" +
                "\\src\\test\\resources\\uk\\ac\\ebi\\masscascade\\data\\Sample.mzML");
        File file = new File("C:\\Users\\stephan\\Code\\IdeaProjects\\MassCascade\\MassCascadeCore" +
                "\\src\\test\\resources\\uk\\ac\\ebi\\masscascade\\data\\Qc1.mzML");

        // build the output data container
        RawContainer container = MemoryContainerBuilder.getInstance().newInstance(RawContainer.class, file.getName());

        // build the parameter map for the reader task
        ParameterMap params = new ParameterMap();
        params.put(Parameter.DATA_FILE, file);
        params.put(Parameter.RAW_CONTAINER, container);

        // create and run the task to read the file
        CallableTask task = new PsiMzmlReader(params);
        RawContainer outContainer = (RawContainer) task.call();

        ThreeDChart chart = new ThreeDChart(new Range(0, 1000), 1.5, new Range(70, 900), .25);
        chart.plot(outContainer);
        Thread.sleep(60000);
    }
}
