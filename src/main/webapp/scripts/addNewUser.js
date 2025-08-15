document.addEventListener('DOMContentLoaded', function () {
    const urlParams = new URLSearchParams(window.location.search);
    const userId = urlParams.get('userId');

    const form = document.getElementById('user-form');
    const submitButton = document.querySelector('.submit-buttons button[type="submit"]');
    const cancelButton = document.querySelector('.submit-buttons button[type="button"]');

    // Ako postoji userId, popuni formu podacima korisnika
    if (userId != null) {
        fetchUserData(userId);
    }

    // Klik na Cancel dugme – preusmjeri na manageUser.html
    cancelButton.addEventListener('click', function () {
        window.location.href = 'manageUsers.html';
    });

    // Klik na Submit dugme
    submitButton.addEventListener('click', function (event) {
        event.preventDefault();

        const name = form.querySelector('input[name="name"]').value.trim();
        const username = form.querySelector('input[name="username"]').value.trim();
        const password = form.querySelector('input[name="password"]').value.trim();

        if (!name || !username || !password) {
            alert('Molimo popunite sva polja prije slanja.');
            return;
        }

        const user = {
            id: userId,
            fullName: name,
            username: username,
            password: password,
        };

        $.ajax({
            url: '/kviz/api/superadmin/admin',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(user),
            success: function (response, status, xhr) {
                if (xhr.status === 200) {
                    alert(response.message || 'User added successfully');
                    window.location.href = 'manageUsers.html'; // Preusmjeri nakon uspjeha
                } else {
                    alert('Failed to add user: ' + (response.message || 'Unknown error'));
                }
            },
            error: function (xhr, status, error) {
                console.error('Error adding user:', status, error);
                alert('Došlo je do greške prilikom dodavanja korisnika.');
            }
        });
    });

    // Funkcija za dohvat podataka korisnika po ID-u
    function fetchUserData(userId) {
        $.ajax({
            url: '/kviz/admin/fetchUserById',
            type: 'GET',
            data: { id: userId },
            dataType: 'json',
            success: function (user) {
                form.querySelector('input[name="name"]').value = user.name || '';
                form.querySelector('input[name="username"]').value = user.username || '';
            },
            error: function (xhr, status, error) {
                console.error('Error fetching user data:', status, error);
            }
        });
    }

    // Opcionalna funkcija za kapitalizaciju (ako treba)
    function capitalizeFirstLetter(word) {
        if (!word) return word;
        return word.charAt(0).toUpperCase() + word.slice(1).toLowerCase();
    }
});

