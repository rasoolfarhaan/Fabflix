function handleResult(resultData) {
    if (resultData && resultData.error) {
        alert(resultData.error);
    }
}

$('form').submit(function(event) {
    event.preventDefault(); // Prevent the default form submission

    let formData = $(this).serialize();

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
