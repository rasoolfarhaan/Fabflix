$(document).ready(function () {
    // When the document is ready, load the shopping cart data
    loadShoppingCart();

    // Attach a click event handler to the "Add to Cart" buttons
    $("#star_table_body").on("click", ".add-to-cart", function () {
        const movieId = jQuery(this).data("movie-title");
        const decrease = jQuery(this).data("decrease");
        const del = jQuery(this).data("delete");

        // Send an AJAX request to add the selected movie to the cart
        jQuery.ajax({
            method: "POST",  // You can use POST or another HTTP method
            url: "AddToCartServlet?movieId=" + movieId + "&decrease=" + decrease + "&delete=" + del , // Replace with the actual URL to your Servlet
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
        alert("SUCCESS");
        location.reload();
    });
});

function loadShoppingCart() {
    // Send an AJAX request to get the shopping cart data from the server
    $.ajax({
        type: "GET",
        url: "GetShoppingCartServlet", // Replace with the actual URL of your Servlet
        dataType: "json",
        success: function (cartData) {
            // Clear the table body
            $("#star_table_body").empty();

            var total = 0; // Initialize total price

            // Populate the table with cart items
            $.each(cartData, function (movieTitle, quantity) {
                // You can fetch the movie price and other details based on the movie title here
                var price = 10.00; // Replace with the actual price

                var rowHTML = "<tr>";
                rowHTML += "<td>" + movieTitle + "</td>";
                rowHTML += "<td>" + quantity + "</td>";
                rowHTML += "<td>$" + price + "</td>"; // Use the actual price
                rowHTML += "<td>$" + (quantity * price) + "</td>";
                rowHTML += "    <td>\n" +
                    '        <button id="success" class="add-to-cart" data-movie-title="' + movieTitle + '">+</button>' +
                    "        <button id=\"success\" class=\"add-to-cart\" data-movie-title=\"" + movieTitle + "\" data-decrease=\"true\">-</button>" +
                    "        <button id=\"success\" class=\"add-to-cart\" data-movie-title=\"" + movieTitle + "\" data-delete=\"true\">delete</button>" +
                    "    </td>";
                rowHTML += "</tr>";


                total += (quantity * price); // Add the item's total to the overall total

                $("#star_table_body").append(rowHTML);
            });

            // Add a row for the total price across all items
            var totalRowHTML = "<tr>";
            totalRowHTML += "<td colspan='3'>Total:</td>";
            totalRowHTML += "<td>$" + total + "</td>";
            totalRowHTML += "</tr>";
            $("#star_table_body").append(totalRowHTML);

            var checkoutButtonHTML = '<a href="payment.html?price=' + total + '"><button>Checkout</button></a>';
            $("#star_table_body").after(checkoutButtonHTML);
        },
        error: function () {
            // Handle the error, e.g., display an error message
            $("#star_table_body").html("<tr><td colspan='4'>Error loading shopping cart.</td></tr>");
        }
    });
}

