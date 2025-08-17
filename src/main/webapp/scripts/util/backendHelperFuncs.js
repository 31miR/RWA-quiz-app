export async function getLoggedInAdminData() {
    const response = await fetch("/kviz/api/whoami");
    const adminData = await response.json();
    return adminData;
}

export async function isLoggedIn() {
    const adminData = await getLoggedInAdminData();
    if (adminData.id == null) {
        return false;
    }
    return true;
}

export async function isLoggedInSuperAdmin() {
    const adminData = await getLoggedInAdminData();
    if (adminData.isSuperAdmin) {
        return true;
    }
    return false;
}

export async function logoutAdmin() {
    await fetch("/kviz/api/admin/logout", {method: "POST"});
}

export async function redirectToPage(url) {
    window.location.href = url;
}

export async function logoutAndRedirectToLanding() {
    await logoutAdmin();
    redirectToPage('/kviz/landingPage.html');
}

export async function getProfileDataById(id) {
    const response = await fetch(`/kviz/api/superadmin/admin?id=${id}`);
    const profile = await response.json();
    return profile;
}

export async function createNewProfile(profileData) {
    const response = await fetch('/kviz/api/superadmin/admin', {method: 'POST', body: JSON.stringify(profileData)});
    return response;
}

export async function updateExistingProfile(newProfileData) {
    const response = await fetch(`/kviz/api/superadmin/admin?id=${newProfileData.id}`, {method: 'PUT', body: JSON.stringify(newProfileData)});
    return response;
}

export async function getQuizById(id) {
    const response = await fetch(`/kviz/api/admin/quiz?id=${id}`);
    const quizData = await response.json();
    return quizData;
}

export async function deleteQuizById(id) {
    const response = await fetch(`/kviz/api/admin/quiz?id=${id}`, {method: "DELETE"});
    return response;
}

export async function updateExistingQuiz(quizRaw, image) {
  const formData = new FormData();

  const jsonBlob = new Blob([JSON.stringify(quizRaw)], {
    type: "application/json"
  });
  formData.append("quiz", jsonBlob);

  if (image instanceof File) {
    formData.append("image", image, image.name);
  }

  const response = await fetch("/kviz/api/admin/quiz", {
    method: "PUT",
    body: formData
  });

  if (!response.ok) {
    throw new Error(`Failed to create quiz: ${response.status}`);
  }

  return await response.json();
}


export async function createNewQuiz(quizRaw, image) {
  const formData = new FormData();

  const jsonBlob = new Blob([JSON.stringify(quizRaw)], {
    type: "application/json"
  });
  formData.append("quiz", jsonBlob);

  if (image instanceof File) {
    formData.append("image", image, image.name);
  }

  const response = await fetch("/kviz/api/admin/quiz", {
    method: "POST",
    body: formData
  });

  console.log("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
  console.log(quizRaw)
  console.log("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")

  if (!response.ok) {
    throw new Error(`Failed to create quiz: ${response.status}`);
  }

  
  console.log("~~~~~~~~~~~~~~~~~~~YAY~~~~~~~~~~~~~~~~~~~~")

  return await response.json();
}

export async function getQuizEventIdFromPin(pin) {
  const response = await fetch(`/kviz/api/quizEvent?pin=${pin}`);
  const data = await response.json();
  return data;
}