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

// Handles the form submission
$('form').submit(function(event) {
    event.preventDefault(); // Prevent the default form submission

    // Serialize the form data to send it as POST data
    let formData = $(this).serialize();

    // Makes the HTTP POST request and registers success and error callback functions
    $.ajax({
        dataType: "json",  // Setting return data type
        method: "POST", // Setting request method
        url: "pay", // Setting request URL, which is mapped by FormServlet in your Java code
        data: formData, // Send the form data
        success: (resultData) => handleResult(resultData), // Success callback
        error: function (xhr, textStatus, errorThrown) {
            if (xhr.status === 401) {
                // Handle a 401 error (Unauthorized) by showing an error message
                alert("Invalid information");
            } else {
                // Handle other errors as needed
                window.location.href='confirmation.html';
            }
        }
    });
});
