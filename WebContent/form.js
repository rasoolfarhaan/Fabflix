/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to URL encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Use a regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into HTML elements
 * @param resultData jsonObject
 */
function handleResult(resultData) {
    if (resultData && resultData.error) {
        // Check if the resultData contains an "error" property
        // This indicates an error response from the servlet
        alert(resultData.error); // Display the error message in a popup
    }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Get id from URL
let starId = getParameterByName('id');

// Handles the form submission
$('form').submit(function(event) {
    event.preventDefault(); // Prevent the default form submission

    // Serialize the form data to send it as POST data
    let formData = $(this).serialize();

    // Makes the HTTP POST request and registers success and error callback functions
    $.ajax({
        dataType: "json",  // Setting return data type
        method: "POST", // Setting request method
        url: "form", // Setting request URL, which is mapped by FormServlet in your Java code
        data: formData, // Send the form data
        success: (resultData) => handleResult(resultData), // Success callback
        error: function (xhr, textStatus, errorThrown) {
            if (xhr.status === 401) {
                // Handle a 401 error (Unauthorized) by showing an error message
                alert("Invalid username or password");
            } else {
                // Handle other errors as needed
                window.location.href='main.html';
            }
        }
    });
});
