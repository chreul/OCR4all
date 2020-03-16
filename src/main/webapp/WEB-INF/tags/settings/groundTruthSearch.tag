<%@ tag description="GroundTruthSearch settings" pageEncoding="UTF-8" %>
<table class="compact">
    <tbody>
    <tr>
        <!TODO: change body layout and add function>
        <td><p>N-Gram</p></td>
        <td>
            <div class="input-field">
                <i class="material-icons prefix">linear_scale</i>
                <input id="n-gram" type="number" value="5" min="4" max="10" step="1"/>
                <label for="n-gram" data-type="int" data-error="Has to be integer">n value for n-gram</label>
            </div>
        </td>
    </tr>
    </tbody>
</table>
