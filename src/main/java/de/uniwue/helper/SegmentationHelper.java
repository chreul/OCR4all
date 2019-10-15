package de.uniwue.helper;

import java.io.File;
import java.io.IOException;
import java.util.List;

import de.uniwue.config.ProjectConfiguration;
import de.uniwue.feature.ProcessStateCollector;

public class SegmentationHelper {
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
     * Object to determine process states
     */
    private ProcessStateCollector procStateCol;

    /**
     * Constructor
     *
     * @param projectDir Path to the project directory
     * @param projectImageType Type of the project (binary,gray)
     */
    public SegmentationHelper(String projDir, String projectImageType) {
        this.projectImageType = projectImageType;
        projConf = new ProjectConfiguration(projDir);
        procStateCol = new ProcessStateCollector(projConf, projectImageType);
    }

    /**
     * Checks if process related files already exist
     *
     * @param pageIds Identifiers of the pages (e.g 0002,0003)
     * @return Information if files exist
     */
    public boolean doOldFilesExist(String[] pageIds) {
        for (String pageId : pageIds) {
            if (procStateCol.segmentationState(pageId) == true)
                return true;
        }
        return false;
    }

    /**
     * Deletion of old process related files
     *
     * @param pageIds Identifiers of the pages (e.g 0002,0003)
     * @throws IOException
     */
    public void deleteOldFiles(List<String> pageIds) throws IOException {
        if (!new File(projConf.OCR_DIR).exists())
            return;

        // Delete image and PageXML files
        for (String pageId : pageIds) {
            File segXml = new File(projConf.OCR_DIR + pageId + projConf.CONF_EXT);
            if (segXml.exists())
                segXml.delete();
        }
    }
}
