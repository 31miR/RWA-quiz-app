document.addEventListener('DOMContentLoaded', function () {
    const urlParams = new URLSearchParams(window.location.search);
    const userId = urlParams.get('userId');

    if (userId != null) {
        fetchUserData(userId);
    }
    const form = document.getElementById('user-form');
    const submitButton = document.querySelector('.submit-buttons button[type="submit"]');

    submitButton.addEventListener('click', function (event) {
        event.preventDefault();

        const name = form.querySelector('input[name="name"]').value;
        const username = form.querySelector('input[name="username"]').value;
        const password = form.querySelector('input[name="password"]').value;

        const user = {
            id: userId,
            fullName: name,
            username: username,
            password: password,
        }

        $.ajax({
            url: '/kviz/api/superadmin/admin',   
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(user),
            success: function (response, status, xhr) {
                if (xhr.status === 200) {
                    alert(response.message || 'User added successfully');
                    window.location.href = 'manageUsers';
                } else {
                    alert('Failed to add user: ' + (response.message || 'Unknown error'));
                }
            },
            error: function (xhr, status, error) {
                console.error('Error adding user:', status, error);
                alert('An error occurred while adding the user.');
            }
        });

    })

    function fetchUserData(userId) {
        $.ajax({
            url: '/kviz/admin/fetchUserById',   
            type: 'GET',
            data: {id: userId},
            dataType: 'json',
            success: function (user) {
                form.querySelector('input[name="name"]').value = user.name;
                form.querySelector('input[name="username"]').value = user.username;
            },
            error: function (xhr, status, error) {
                console.error('Error fetching quiz data:', status, error);
            }
        });
    }
    function capitalizeFirstLetter(word) {
        if (!word) return word;   
        return word.charAt(0).toUpperCase() + word.slice(1).toLowerCase();
    }
})
