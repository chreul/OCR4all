<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" tagdir="/WEB-INF/tags/settings" %>
<t:html>
    <t:head imageList="true" processHandler="true">
        <title>OCR4all - Result Generation</title>

        <script type="text/javascript">
            $(document).ready(function() {
                // Load image list with fetched static page Ids (pages valid for result)
                $.get( "ajax/resultGeneration/getValidPageIds")
                .done(function( data ) {
                    initializeImageList("OCR", false, data);
                });

                function resetResultGeneration(){
                    $(".determinate").width('0%');
                    const status = $(".status");
                    status.find("span").removeClass().addClass("grey-text").text("No Result process running")
                }

                // Initialize process update and set options
                initializeProcessUpdate("resultGeneration", [ 0 ], [ 1 ], false);

                $('button[data-id="execute"]').click(function() {
                    resetResultGeneration();
                    var selectedPages = getSelectedPages();
                    var ajaxParams = { "pageIds[]" : selectedPages, "resultType" : $('#resultType').val(), "resultStrategy": $('#resultStrategy').val() };
                    if( selectedPages.length === 0 ) {
                        $('#modal_errorhandling').modal('open');
                        return;
                    }
                    $.post( "ajax/resultGeneration/exists", { "pageIds[]" : selectedPages, "resultType" : $('#resultType').val() } )
                    .done(function( data ){
                        if(data === false){
                            // Execute result process
                            executeProcess(ajaxParams);
                        }
                        else{
                            $('#modal_exists').modal('open');
                        }
                    });
                });

                $('button[data-id="cancel"]').click(function() {
                    cancelProcess();
                });

                $('#agree').click(function() {
                    var selectedPages = getSelectedPages();
                    var ajaxParams = { "pageIds[]" : selectedPages, "resultType" : $('#resultType').val() };
                    // Execute result process
                    executeProcess(ajaxParams);
                });

                $('#resultType').change(function() {
                    const resultType = $(this).val();
                    const $strategyRow = $("#strategy-row");
                    resultType === "xml" ? $strategyRow.hide() : $strategyRow.show();
                })
            });
        </script>
    </t:head>
    <t:body heading="Result Generation" imageList="true" processModals="true">
        <div class="container includes-list">
            <div class="section">
                <button data-id="execute" class="btn waves-effect waves-light">
                    Execute
                    <i class="material-icons right">chevron_right</i>
                </button>
                <button data-id="cancel" class="btn waves-effect waves-light">
                    Cancel
                    <i class="material-icons right">cancel</i>
                </button>

                <ul class="collapsible" data-collapsible="expandable">
                    <li>
                        <div class="collapsible-header"><i class="material-icons">settings</i>Settings</div>
                        <div class="collapsible-body">
                            <s:resultGeneration></s:resultGeneration>
                        </div>
                    </li>
                    <li>
                        <div class="collapsible-header"><i class="material-icons">info_outline</i>Status</div>
                        <div class="collapsible-body">
                            <div class="status"><p>Status: <span>No Result process running</span></p></div>
                            <div class="progress">
                                <div class="determinate"></div>
                            </div>
                        </div>
                    </li>
                </ul>

                <button data-id="execute" class="btn waves-effect waves-light">
                    Execute
                    <i class="material-icons right">chevron_right</i>
                </button>
                <button data-id="cancel" class="btn waves-effect waves-light">
                    Cancel
                    <i class="material-icons right">cancel</i>
                </button>
            </div>
        </div>
    </t:body>
</t:html>
