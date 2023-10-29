document.addEventListener("DOMContentLoaded", function () {
    const loginForm = document.querySelector("form");

    loginForm.addEventListener("submit", function (event) {
        event.preventDefault(); // Prevent the form from submitting via the default action

        const email = document.querySelector("input[name=email]").value;
        const password = document.querySelector("input[name=password]").value;

        // Perform an AJAX request to your server to check the login credentials
        // Replace 'your-api-endpoint' with the actual endpoint that handles the login
        fetch("/api/movies", {
            method: "POST",
            body: JSON.stringify({ email, password }),
            headers: {
                "Content-Type": "application/json",
            },
        })
            .then((response) => {
                if (response.ok) {
                    // If the login was successful, redirect to the movie list page
                    window.location.href = "movies.html"; // Replace with the actual URL
                } else {
                    alert("Login failed. Please check your credentials.");
                }
            })
            .catch((error) => {
                console.error("Error:", error);
                alert("An error occurred while logging in.");
            });
    });
});
