package de.uniwue.controller;


import java.io.IOException;
import java.util.Arrays;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import de.uniwue.helper.RecognitionHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import de.uniwue.helper.GroundTruthSearchHelper;

/**
 * Controller class for pages of segmentation dummy module
 * Use response.setStatus to trigger AJAX fail (and therefore show errors)
 */
@Controller
public class GroundTruthSearchController {

    /**
     * Manages the helper object and stores it in the session
     *
     * @param session Session of the user
     * @param response Response to the request
     * @return Returns the helper object of the process
     */
    public GroundTruthSearchHelper provideHelper(HttpSession session, HttpServletResponse response) {
        if (GenericController.isSessionValid(session, response) == false)
            return null;

        // Keep a single helper object in session
        GroundTruthSearchHelper groundTruthSearchHelper = (GroundTruthSearchHelper) session.getAttribute("groundTruthSearchHelper");
        if (groundTruthSearchHelper == null) {
            groundTruthSearchHelper = new GroundTruthSearchHelper(
                    session.getAttribute("projectDir").toString(),
                    session.getAttribute("imageType").toString()
            );
            session.setAttribute("groundTruthSearchHelper", groundTruthSearchHelper);
        }
        return groundTruthSearchHelper;
    }

    /**
     * Response to the request to send the content of the /SegmentationDummy page
     *
     * @param session Session of the user
     * @return Returns the content of the /SegmentationDummy page
     */
    @RequestMapping("/GroundTruthSearch")
    public ModelAndView show(HttpSession session, HttpServletResponse response) {
        ModelAndView mv = new ModelAndView("groundTruthSearch");

        GroundTruthSearchHelper segmentationDummyHelper = provideHelper(session, response);
        if(segmentationDummyHelper == null) {
            mv.addObject("error", "Session expired.\nPlease return to the Project Overview page.");
            return mv;
        }
        return mv;
    }

    /**
     * Response to the request to execute the process
     *
     * @param pageIds Ids of specified pages
     * @param segmentationImageType Type of the images (binary,despeckled)
     * @param session Session of the user
     * @param response Response to the request
     * @param inProcessFlow Indicates if the process is executed within the ProcessFlow
     */
    @RequestMapping(value = "/ajax/groundTruthSearch/execute", method = RequestMethod.POST)
    public @ResponseBody void execute(
            @RequestParam("pageIds[]") String[] pageIds,
            @RequestParam("imageType") String segmentationImageType,
            HttpSession session, HttpServletResponse response,
            @RequestParam(value = "inProcessFlow", required = false, defaultValue = "false") boolean inProcessFlow
    ) {
        GroundTruthSearchHelper groundTruthSearchHelper = provideHelper(session, response);
        if (groundTruthSearchHelper == null)
            return;

        int conflictType = groundTruthSearchHelper.getConflictType(GenericController.getProcessList(session), inProcessFlow);
        if (GenericController.hasProcessConflict(session, response, conflictType))
            return;

        GenericController.addToProcessList(session, "segmentationDummy");
        try {
            groundTruthSearchHelper.execute(Arrays.asList(pageIds), segmentationImageType);
        } catch (IOException | ParserConfigurationException | TransformerException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            groundTruthSearchHelper.resetProgress();
            e.printStackTrace();
        }
        GenericController.removeFromProcessList(session, "segmentationDummy");
    }

    /**
     * Response to the request to return the progress status of the segmentation dummy service
     *
     * @param session Session of the user
     * @return Current progress (range: 0 - 100)
     */
    @RequestMapping(value = "/ajax/groundTruthSearch/progress" , method = RequestMethod.GET)
    public @ResponseBody int progress(HttpSession session, HttpServletResponse response) {
        GroundTruthSearchHelper segmentationDummyHelper = provideHelper(session, response);
        if (segmentationDummyHelper == null)
            return -1;

        return segmentationDummyHelper.getProgress();
    }

    @RequestMapping(value = "/ajax/groundTruthSearch/console" , produces = "text/plain;charset=UTF-8", method = RequestMethod.GET)
    public @ResponseBody String console(
            @RequestParam("streamType") String streamType,
            HttpSession session, HttpServletResponse response
    ) {
        GroundTruthSearchHelper groundTruthSearchHelper = provideHelper(session, response);
        if (groundTruthSearchHelper == null)
            return "";

        if (streamType.equals("err"))
            return groundTruthSearchHelper.getProcessHandler().getConsoleErr();
        return groundTruthSearchHelper.getProcessHandler().getConsoleOut();
    }

    /**
     * Response to the request to cancel the segmentation dummy process
     *
     * @param session Session of the user
     * @param response Response to the request
     */
    @RequestMapping(value = "/ajax/groundTruthSearch/cancel", method = RequestMethod.POST)
    public @ResponseBody void cancel(HttpSession session, HttpServletResponse response) {
        GroundTruthSearchHelper groundTruthSearchHelper = provideHelper(session, response);
        if (groundTruthSearchHelper == null)
            return;

        groundTruthSearchHelper.cancelProcess();
    }

    /**
     * Response to the request to check if old process related files exist
     *
     * @param pageIds Identifiers of the pages (e.g 0002,0003)
     * @param session Session of the user
     * @param response Response to the request
     * @return Information if files exist
     */
    @RequestMapping(value = "/ajax/groundTruthSearch/exists" , method = RequestMethod.POST)
    public @ResponseBody boolean filesExists(
            @RequestParam("pageIds[]") String[] pageIds,
            HttpSession session, HttpServletResponse response
    ) {
        GroundTruthSearchHelper groundTruthSearchHelper = provideHelper(session, response);
        if (groundTruthSearchHelper == null)
            return false;
        //TODO: check if there are files
        return groundTruthSearchHelper.doOldFilesExist();
    }

}

