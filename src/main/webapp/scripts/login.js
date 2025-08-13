document.addEventListener('DOMContentLoaded', function () {
    const loginForm = document.getElementById('login-form');
    const loginError = document.getElementById('loginError');

    loginForm.addEventListener('submit', function (event) {
        event.preventDefault();    

        const username = loginForm.username.value;
        const password = loginForm.password.value;

           
        $.ajax({
            url: '/kviz/api/admin/login',
            type: 'POST',
            data: JSON.stringify({username, password}),
            contentType: 'application/json',
            dataType: 'json',
            success: function(response) {
            // Ako je login uspješan, redirectaj na admin početnu stranicu
                window.location.href = "/kviz/admin/index.html";
            },
            error: function(xhr) {
                const body = JSON.parse(xhr.responseText);
                loginError.innerText= body.errorMessage;
                }
        });
    });
});