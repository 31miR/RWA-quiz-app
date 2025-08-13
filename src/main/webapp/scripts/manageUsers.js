$(document).ready(function () {
    $('.add-user-button').click(function () {
        window.location.href = 'addNewUser';
    });

    function fetchAndPopulateTable() {
        $.ajax({
            url: '/kviz/api/superadmin/admin?offset=0&limit=10',     
            type: 'GET',
            dataType: 'json',
            success: function (data) {
                populateTable(data);
            },
            error: function (xhr, status, error) {
                console.error('Error fetching data:', status, error);
            }
        });
    }

    function populateTable(data) {
        var tableBody = $('#user-table tbody');
        tableBody.empty();     

        data.forEach(function (user) {
            var row = $('<tr></tr>');
            row.append('<td>' + user.id + '</td>');
            row.append('<td>' + user.username + '</td>');
            row.append('<td>' + user.password + '</td>');
            row.append('<td>' + user.role + '</td>');
            row.append('<td><button class="action-button edit-button" data-id="' + user.id + '">Edit</button> <button class="action-button delete-button" data-id="' + user.id + '">Delete</button></td>');
            tableBody.append(row);
        });

        $('.delete-button').click(function () {
            var userId = $(this).data('id');
            deleteUser(userId);
        });

        $('.edit-button').click(function () {
            var userId = $(this).data('id');
            window.location.href = 'addNewUser.html?userId=' + userId;
        });

    }

    function deleteUser(userId) {
        $.ajax({
            url: `/kviz/api/superadmin/admin?id=${userId}`,     
            type: 'DELETE',
            contentType: 'application/json',
            data: JSON.stringify({id: userId}),
            success: function (response, status, xhr) {
                if (xhr.status === 200) {
                    alert(response.message || 'User deleted successfully');
                    fetchAndPopulateTable();
                } else {
                    alert('Failed to delete user: ' + (response.message || 'Unknown error'));
                }
            },
            error: function (xhr, status, error) {
                console.error('Error deleting user:', status, error);
                console.log(xhr);
                alert('An error occurred while deleting the user.');
            }
        });
    }

    
    fetchAndPopulateTable();
});
