/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */


/**
 * Sorts the movie list based on the selected option from the dropdown.
 */



/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
jQuery.noConflict();

function handleLookup(query, doneCallback) {
    console.log("autocomplete initiated");
    let cachedSuggestions = localStorage.getItem("autocomplete_" + query);
    if (cachedSuggestions) {
        console.log("Using cached results for query: " + query);
        handleLookupAjaxSuccess(cachedSuggestions, query, doneCallback);
        return;
    }

    // Sending AJAX request to MovieSearchServlet
    jQuery.ajax({
        "method": "GET",
        "url": "api/movie-search?query=" + encodeURIComponent(query),
        "success": function(data) {
            handleLookupAjaxSuccess(data, query, doneCallback);
        },
        "error": function(errorData) {
            console.error("lookup ajax error");
            console.error(errorData);
        }
    });
}

// Function to handle the success of the AJAX request
function handleLookupAjaxSuccess(data, query, doneCallback) {
    console.log("lookup ajax successful");
    localStorage.setItem("autocomplete_" + query, data);
    var jsonData = JSON.parse(data);
    console.log(jsonData);

    // Call the callback function provided by the autocomplete library
    doneCallback({ suggestions: jsonData });
}

// Function to handle when a suggestion is selected
function handleSelectSuggestion(suggestion) {
    console.log("You selected " + suggestion["value"] + " with ID " + suggestion["data"]["movieId"]);
    window.location = 'single-movie.html?id=' + suggestion["data"]["movieId"];
}
jQuery(document).ready(function($) {
    // Bind the autocomplete library to the input box
    $('#autocomplete').autocomplete({
        lookup: function (query, doneCallback) {
            handleLookup(query, doneCallback);
        },
        onSelect: function(suggestion) {
            handleSelectSuggestion(suggestion);
        },
        deferRequestBy: 300,
        minChars: 3
    });

    // Function to handle normal full-text search
    function handleNormalSearch(query) {
        console.log("doing normal search with query: " + query);
        window.location = 'movie-list.html?title=' + encodeURIComponent(query);
    }

    // Bind pressing enter key to normal search handler
    $('#autocomplete').keypress(function(event) {
        if (event.keyCode === 13) {
            handleNormalSearch($('#autocomplete').val());
        }
    });

    // If a search button is present, bind the click event to normal search
    $('#searchButton').click(function() {
        handleNormalSearch($('#autocomplete').val());
    });

    function handleStarResult(resultData) {
        console.log("handleStarResult: populating star table from resultData");

        // Populate the star table
        let starTableBodyElement = jQuery("#star_table_body");

        for (let i = 0; i < Math.min(20, resultData.length); i++) {

            let rowHTML = `
            <tr>
            <th><a href="single-movie.html?id=${resultData[i]["movie_id"]}">${resultData[i]["movie_title"]}</a></th>
            <th>${resultData[i]["movie_year"]}</th>
            <th>${resultData[i]["movie_director"]}</th>
            <th>
            <ul>
                ${resultData[i]["movie_genres"].map(genre =>
                        `<li><a href="movie-list.html?genre=${genre}">${genre}</a></li>`
                    ).join("")}
              </ul>
            </th>
            <th>
              <ul>
                ${resultData[i]["movie_stars"].map(star => {
                    
                    const splitResult = star.split("@")
                    console.log(splitResult)
                    const starName = splitResult[0];
                    const starId = splitResult[1];
                    return `<li><a href="single-star.html?id=${starId}">${starName}</a></li>`
                }
                    ).join("")}
              </ul>
            </th> 
            <th>${resultData[i]["movie_rating"]}</th>`

            // Add the "Add to Cart" button with a data-movie-id attribute to store the movie ID
            rowHTML += '<th><button class="add-to-cart" data-movie-title="' + resultData[i]["movie_title"].substring(30) + '">Add to Cart</button></th>';

            rowHTML += "</tr>";
            starTableBodyElement.append(rowHTML);
        }

        // Attach a click event handler to the "Add to Cart" buttons
        jQuery(".add-to-cart").click(function () {
            const movieId = jQuery(this).data("movie-title");

            // Send an AJAX request to add the selected movie to the cart
            jQuery.ajax({
                method: "POST",  // You can use POST or another HTTP method
                url: "AddToCartServlet?movieId=" + movieId + "&decrease=n&delete=n", // Replace with the actual URL to your Servlet
                data: { movieId: movieId },
                success: function (response) {
                    // Handle the response from the server, if needed
                    console.log("Movie added to cart: " + movieId);
                },
                error: function (error) {
                    // Handle any errors, if needed
                    console.error("Error adding movie to cart: " + movieId);
                }
            });
        });
    }



    /**
     * Once this .js is loaded, following scripts will be executed by the browser
     */
    const queryString = window.location.search.substring(1);

    // Makes the HTTP GET request and registers on success callback function handleStarResult
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/movies?" + queryString, // Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
    });
});