<%--
  Created by IntelliJ IDEA.
  User: chadbourne
  Date: 25.02.20
  Time: 17:07
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" tagdir="/WEB-INF/tags/settings" %>
<t:html>
    <t:head imageList="true" processHandler="true">
        <title>OCR4All - Ground Truth Search</title>

        <script type="text/javascript">
            $(document).ready(function() {
                initializeProcessUpdate("groundTruthSearch", [ 0 ], [ 1 ], true);

                $('#imageType').on('change', function() {
                    $('#bookname').val($('#imageType').val());
                    // Change ImageList depending on the imageType selected
                    reloadImageList($('#imageType').val(), true);
                });
                // Initialize image list
                $('#imageType').change();

                // Process handling (execute for all pages with current settings)
                $('button[data-id="execute"]').click(function() {
                    var selectedPages = getSelectedPages();
                    if( selectedPages.length === 0 ) {
                        $('#modal_errorhandling').modal('open');
                        return;
                    }
                    $.post( "ajax/groundTruthSearch/exists", { "pageIds[]" : selectedPages } )
                        .done(function( data ) {
                            if(data === false) {
                                var ajaxParams =  { "pageIds[]" : selectedPages, "ngram" : $('#n-gram').val()};
                                // Execute segmentation process
                                executeProcess(ajaxParams);
                            }
                            else{
                                $('#modal_exists').modal('open');
                            }
                        })
                        .fail(function( data ) {
                            $('#modal_exists_failed').modal('open');
                        });
                });
                $('#agree').click(function() {
                    var selectedPages = getSelectedPages();
                    var ajaxParams =  { "pageIds[]" : selectedPages, "imageType" : $('#imageType').val()};
                    // Execute segmentation process
                    executeProcess(ajaxParams);
                });
                //checking if n-gram input value si valid and disabling execute button if not
                $('#n-gram').on('input', function(e) {
                    if(!this.checkValidity()){
                        $('#execute').addClass("disabled");
                    }else{
                        $('#execute').removeClass("disabled");
                    }
                });
            });
        </script>
    </t:head>
    <t:body heading="Ground Truth Search" imageList="true" processModals="true">
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
                            <s:groundTruthSearch></s:groundTruthSearch>
                        </div>
                    </li>
                    <li>
                        <div class="collapsible-header"><i class="material-icons">info_outline</i>Status</div>
                        <div class="collapsible-body">
                            <div class="status"><p>Status: <span>No Search process running</span></p></div>
                            <div class="progress">
                                <div class="determinate"></div>
                            </div>
                        </div>
                        <div class="console">
                            <ul class="tabs">
                                <li class="tab" data-refid="consoleOut" class="active"><a href="#consoleOut">Console Output</a></li>
                                <li class="tab" data-refid="consoleErr"><a href="#consoleErr">Console Error</a></li>
                            </ul>
                            <div id="consoleOut"><pre></pre></div>
                            <div id="consoleErr"><pre></pre></div>
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

        <div id="modal_alert" class="modal">
            <div class="modal-content red-text">
                <h4>Error</h4>
                <p>
                    The  directory for selected image type does not exist.<br />
                    Use appropriate modules to create these images.
                </p>
            </div>
            <div class="modal-footer">
                <a href="#!" class="modal-action modal-close waves-effect waves-green btn-flat">Agree</a>
            </div>
        </div>
    </t:body>
</t:html>
