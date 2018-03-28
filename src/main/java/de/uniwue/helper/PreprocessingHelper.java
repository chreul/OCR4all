package de.uniwue.helper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import de.uniwue.config.ProjectConfiguration;
import de.uniwue.feature.ProcessConflictDetector;
import de.uniwue.feature.ProcessHandler;
import de.uniwue.feature.ProcessStateCollector;

/**
 * Helper class for preprocessing pages, which also calls the ocrubus-nlbin program 
 */
public class PreprocessingHelper {
    /**
     * Object to access project configuration
     */
    private ProjectConfiguration projConf;

    /**
     * Object to determine process states
     */
    private ProcessStateCollector procStateCol;

    /**
     * Helper object for process handling
     */
    private ProcessHandler processHandler;

    /**
     * Progress of the Preprocessing process
     */
    private int progress = -1;

    /**
     * Indicates if a Preprocessing process is already running
     */
    private boolean preprocessingRunning = false;

    /** 
     * Pages to preprocess
     */
    private List<String> preprocPages = new ArrayList<String>();

    /**
     * Constructor
     *
     * @param projectDir Path to the project directory
     * @param projectImageType Type of the project (binary, gray)
     */
    public PreprocessingHelper(String projectDir, String projectImageType) {
        projConf = new ProjectConfiguration(projectDir);
        procStateCol = new ProcessStateCollector(projConf, projectImageType);
        processHandler = new ProcessHandler();
    }

    /**
     * Returns the progress of the process
     *
     * @return Progress percentage
     */
    public int getProgress() {
        // Prevent function from calculation progress if process is not running
        if (preprocessingRunning == false)
            return progress;

        File preprocDir = new File(projConf.PREPROC_DIR);
        File[] binFiles = preprocDir.listFiles((d, name) -> name.endsWith(projConf.BINR_IMG_EXT));
        // Calculate the progress of the Preprocessing process 
        // Maximum progress = 90%, since the preprocessed files still need to be moved
        if (binFiles.length != 0)
            progress =  (int) ((double) binFiles.length / preprocPages.size() * 0.9 * 100);
        return progress;
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
     * Create necessary Preprocessing directories if they do not exist
     */
    private void initializePreprocessingDirectories() {
        File preprocDir = new File(projConf.PREPROC_DIR);
        if (!preprocDir.exists())
            preprocDir.mkdir();

        File binDir = new File(projConf.BINR_IMG_DIR);
        if (!binDir.exists())
            binDir.mkdir();

        File grayDir = new File(projConf.GRAY_IMG_DIR);
        if (!grayDir.exists())
            grayDir.mkdir();
    }

    /**
     * Move the images of the given type to the appropriate Preprocessing folder
     *
     * @param imageType Type of the image
     */
    private void moveImageFiles(String imageType) {
        File preprocDir = new File(projConf.PREPROC_DIR);
        File[] filesToMove = preprocDir.listFiles((d, name) -> name.endsWith(projConf.getImageExtensionByType(imageType)));
        Arrays.sort(filesToMove);
        for (File image : filesToMove) {
            int pageArrayIndex = Integer.parseInt(FilenameUtils.removeExtension(FilenameUtils.removeExtension(image.getName()))) - 1;
            image.renameTo(new File(projConf.getImageDirectoryByType(imageType) + preprocPages.get(pageArrayIndex) + projConf.IMG_EXT));
        }
    }

    /**
     * Executes image Preprocessing of all pages
     * Achieved with the help of the external python program "ocropus-nlbin"
     * This function also creates the preprocessed directory structure
     *
     * @param pageIds Identifiers of the pages (e.g 0002,0003)
     * @param cmdArgs Command line arguments for "ocropus-nlbin"
     * @throws IOException
     */
    public void preprocessPages(List<String> pageIds, List<String> cmdArgs) throws IOException {
        preprocessingRunning = true;

        progress = 0;

        File origDir = new File(projConf.ORIG_IMG_DIR);
        if (!origDir.exists())
            return;

        initializePreprocessingDirectories();
        deleteOldFiles(pageIds);

        preprocPages = pageIds;

        List<String> command = new ArrayList<String>();
        for (String pageId : pageIds) {
            // Add affected pages with their absolute path to the command list
            command.add(projConf.ORIG_IMG_DIR + pageId + projConf.IMG_EXT);
        }
        command.add("-o");
        command.add(projConf.PREPROC_DIR);
        command.addAll(cmdArgs);

        processHandler = new ProcessHandler();
        processHandler.setFetchProcessConsole(true);
        processHandler.startProcess("ocropus-nlbin", command, false);

        // Workaround in case that some images could not be preprocessed successfully
        if (progress < 90)
            progress = 90;

        // Move preprocessed pages to projDirConf.PREPROC_DIR
        moveImageFiles("Binary");
        moveImageFiles("Gray");

        preprocessingRunning = false;
        progress = 100;
    }

    /**
     * Resets the progress (use if an error occurs)
     */
    public void resetProgress() {
        preprocessingRunning = false;
        progress = -1;
    }

    /**
     * Cancels the process
     */
    public void cancelProcess() {
        if (processHandler != null)
            processHandler.stopProcess();
    }

    /**
     * Deletion of old process related files
     *
     * @param pageIds Identifiers of the pages (e.g 0002,0003)
     * @throws IOException 
     */
    public void deleteOldFiles(List<String> pageIds) throws IOException {
        for (String pageId : pageIds) {
            File binImg = new File(projConf.BINR_IMG_DIR + pageId + projConf.IMG_EXT);
            if(binImg.exists())
                binImg.delete();

            File grayImg = new File(projConf.GRAY_IMG_DIR + pageId + projConf.IMG_EXT);
            if(grayImg.exists())
                grayImg.delete();
        }
    }

    /**
     * Checks if process depending files already exist
     *
     * @param pageIds Identifiers of the pages (e.g 0002,0003)
     * @return Information if files exist
     */
    public boolean doOldFilesExist(String[] pageIds) {
        for(String pageId : pageIds) {
            if (procStateCol.preprocessingState(pageId) == true)
                return true;
        }
        return false;
    }

    /**
     * Determines conflicts with the process
     *
     * @param currentProcesses Processes that are currently running
     * @return Type of process conflict
     */
    public int getConflictType(List<String> currentProcesses) {
        return ProcessConflictDetector.preprocessingConflict(currentProcesses);
    }
}
