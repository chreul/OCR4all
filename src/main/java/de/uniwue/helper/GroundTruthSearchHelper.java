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

import de.uniwue.feature.ProcessHandler;
import de.uniwue.feature.ProcessStateCollector;
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
     * Helper object for process handling
     */
    private ProcessHandler processHandler;
    /**
     * Object to use generic functionalities
     */
    private GenericHelper genericHelper;

    /**
     * Object to determine process states
     */
    private ProcessStateCollector procStateCol;

    private int fileNo = -1;
    private int filesSearched = -1;


    /**
     * Constructor
     *
     * @param projDir Path to the project directory
     * @param projectImageType Type of the project (binary,gray)
     */
    public GroundTruthSearchHelper(String projDir, String projectImageType) {
        this.projectImageType = projectImageType;
        projConf = new ProjectConfiguration(projDir);
        genericHelper = new GenericHelper(projConf);
        procStateCol = new ProcessStateCollector(projConf, projectImageType);
        processHandler = new ProcessHandler();
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
        File corpusFolder = new File(projConf.PROJ_CORPUS_DIR);

        ArrayList<File> corpusFiles = new ArrayList<File>();
        // File depth of 1 -> no recursive (file)listing
        /*Files.walk(Paths.get(projConf.PROJ_CORPUS_DIR), 1)
                .map(Path::toFile)
                .filter(fileEntry -> fileEntry.isFile())
                .filter(fileEntry -> fileEntry.getName().endsWith(".txt"))
                .sorted()
                .forEach(
                        fileEntry -> { corpusFiles.add(fileEntry); }
                );

        */
        fileNo = corpusFiles.size();
        filesSearched = 0;
        processHandler =  new ProcessHandler();
/*
        for(File file : corpusFiles) {
            command.add(file.getAbsolutePath());
            System.out.println(file.getAbsolutePath());
            processHandler.setFetchProcessConsole(true);
            processHandler.startProcess("/opt/OCR4all_helper-scripts/test.py", command, false);
            filesSearched++;
            progress=(int)(filesSearched/fileNo)*100;
        }*/
        command.add(projConf.PROJ_CORPUS_DIR);
        processHandler.setFetchProcessConsole(true);
        processHandler.startProcess("testscript", command, false);
        //getProgress();
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

    /**
     * Gets the process handler object
     *
     * @return Returns the process Helper
     */
    public ProcessHandler getProcessHandler() {
        return processHandler;
    }

    /**
     * Checks if process depending files already exist
     *
     * @param pageIds Identifiers of the pages (e.g 0002,0003)
     * @return Information if files exist
     */
    public boolean doOldFilesExist() {
        return false;
    }
}
