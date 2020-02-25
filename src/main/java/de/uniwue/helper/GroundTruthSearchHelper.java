package de.uniwue.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.w3c.dom.Document;

import de.uniwue.config.ProjectConfiguration;
import de.uniwue.feature.ProcessConflictDetector;
import de.uniwue.feature.pageXML.PageXMLWriter;

public class GroundTruthSearchHelper {
    /**
     * Object to access project configuration
     */
    private ProjectConfiguration projConf;

    /**
     * Image type of the project
     * Possible values: { Binary, Gray }
     */
    private String projectImageType;


    /**
     * Status of the SegmentationLarex progress
     */
    private int progress = -1;

    /**
     * Indicates if the process should be cancelled
     */
    private boolean stop = false;

    /**
     * Constructor
     *
     * @param projDir Path to the project directory
     * @param projectImageType Type of the project (binary,gray)
     */
    public GroundTruthSearchHelper(String projDir, String projectImageType) {
        this.projectImageType = projectImageType;
        projConf = new ProjectConfiguration(projDir);
    }

    /**
     * Moves the extracted files of the segmentation process to the OCR project folder
     *
     * @param pageIds Identifiers of the pages (e.g 0002,0003)
     * @param segmentationImageType Image type of the segmentation (binary, despeckled)
     * @throws IOException
     * @throws TransformerException
     * @throws ParserConfigurationException
     */
    public void execute(List<String> pageIds, String segmentationImageType) throws IOException, ParserConfigurationException, TransformerException {
        stop = false;
        progress = 0;

        List<String> command = new ArrayList<String>();
        command.add("--input");
        File corpusFolder = new File(projConf.PROJ_CORPUS_DIR);

        ArrayList<File> corpusFiles = new ArrayList<File>();
        // File depth of 1 -> no recursive (file)listing
        Files.walk(Paths.get(projConf.PROJ_CORPUS_DIR), 1)
                .map(Path::toFile)
                .filter(fileEntry -> fileEntry.isFile())
                .filter(fileEntry -> fileEntry.getName().endsWith(".txt"))
                .sorted()
                .forEach(
                        fileEntry -> { imageFiles.add(fileEntry); }
                );

        int fileNo = corpusFiles.size();
        int filesSearched = 0;
        for(File file : corpusFiles) {



            filesSearched++;
            progress=(int)(filesSearched/fileNo);
        }

        progress = 100;
    }

    /**
     * Returns the progress of the job
     *
     * @return Progress of preprocessAllPages function
     */
    public int getProgress() {
        return progress;
    }

    /**
     * Resets the progress (use if an error occurs)
     */
    public void resetProgress() {
        progress = -1;
    }

    /**
     * Cancels the process
     */
    public void cancelProcess() {
        stop = true;
    }

    //TODO: add conflict behaviour
    /**
     * Determines conflicts with the process
     * @param currentProcesses Processes that are currently running
     * @param inProcessFlow Indicates if the process is executed within the ProcessFlow
     * @return Type of process conflict
     */
    public int getConflictType(List<String> currentProcesses, boolean inProcessFlow) {
        return ProcessConflictDetector.groundTruthConflict(currentProcesses, inProcessFlow);
    }
}
