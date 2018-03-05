<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:html>
    <t:head imageList="false" processHandler="true">
        <title>OCR4All - Segmentation</title>

        <script type="text/javascript">
            $(document).ready(function() {
                initializeProcessUpdate("segmentation", [ 0 ], [ 1 ], false);
                $('.collapsible').collapsible('open', 0);

                $('#imageType').on('change', function() {
                    $('#bookname').val($('#imageType').val());
                });
                $('#imageType').change();
                // Process handling (execute for all pages with current settings)
                $('button[data-id="execute"]').click(function() {
                    var ajaxParams =  { "imageType" : $('#imageType').val()};
                    executeProcess(ajaxParams);
                    });
            });
        </script>
    </t:head>
    <t:body heading="Segmentation" processModals="true">
        <div class="container">
            <div class="section">
                <button data-id="execute" class="btn waves-effect waves-light">
                    Apply Segmentation results
                    <i class="material-icons right">chevron_right</i>
                </button>
                <button data-id="cancel" class="btn waves-effect waves-light">
                    Cancel
                    <i class="material-icons right">cancel</i>
                </button>

                <ul class="collapsible" data-collapsible="expandable">
                    <li>
                        <div class="collapsible-header"><i class="material-icons">line_style</i>Segmentation</div>
                        <div class="collapsible-body">
                            <table class="compact">
                                <tbody>
                                    <tr>
                                        <td><p>Segmentation image type</p></td>
                                        <td>
                                            <div class="input-field">
                                                <i class="material-icons prefix">image</i>
                                                <select id="imageType" name="imageType" class="suffix">
                                                    <option value="Despeckled">Despeckled</option>
                                                    <option value="Binary">Binary</option>
                                                </select>
                                                <label></label>
                                            </div>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>
                                            <form action="/Larex/direct" method="POST" target="_blank">
                                                <input type="hidden" id="bookpath" name="bookpath" value="${projectDir}PreProc" />
                                                <input type="hidden" id="bookname" name="bookname" value="" />
                                                <input type="hidden" id="websave" name="websave" value="false" />
                                                <input type="hidden" id="localsave" name="localsave" value="bookpath" />
                                                <button data-id="move" class="btn waves-effect waves-light" type="submit" name="action">
                                                    Open LAREX
                                                    <i class="material-icons right">chevron_right</i>
                                                </button>
                                            </form>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </li>
                    <li>
                        <div class="collapsible-header"><i class="material-icons">info_outline</i>Status</div>
                        <div class="collapsible-body">
                            <div class="status"><p>Status: <span>No segmentation process running</span></p></div>
                            <div class="progress">
                                <div class="determinate"></div>
                            </div>
                        </div>
                    </li>
                </ul>

                <button data-id="execute" class="btn waves-effect waves-light">
                    Apply Segmentation results
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