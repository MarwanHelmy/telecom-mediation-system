// Dashboard.js

// ===============================
// DOM ELEMENTS
// ===============================

const nodeCountEl     = document.getElementById('nodeCount');
const ruleCountEl     = document.getElementById('ruleCount');

const logoutBtn       = document.getElementById('logoutBtn');
const userNameDisplay = document.getElementById('userNameDisplay');

const liveCore        = document.getElementById('liveCore');
const liveBadge       = document.getElementById('liveBadge');
const badgeText       = document.getElementById('badgeText');
const coreVersion     = document.getElementById('coreVersion');


// ===============================
// LIVE CORE STATE
// ===============================

function setCoreState(isActive)
{
    if (isActive)
    {
        liveCore.classList.remove('inactive');
        liveCore.classList.add('active');

        liveBadge.classList.remove('inactive');
        liveBadge.classList.add('active');

        badgeText.textContent = 'ACTIVE';

        coreVersion.innerHTML =
                'v5.2.1 | <span style="color:#00E5A0;">Live</span>';
    }
    else
    {
        liveCore.classList.remove('active');
        liveCore.classList.add('inactive');

        liveBadge.classList.remove('active');
        liveBadge.classList.add('inactive');

        badgeText.textContent = 'INACTIVE';

        coreVersion.innerHTML =
                'v5.2.1 | <span style="color:#888;">Sleeping</span>';
    }
}


// ===============================
// LOAD COUNTS
// ===============================

function loadCounts()
{
    fetch("../GetInfoServlet")

            .then(response => response.json())

            .then(data =>
            {
                if (data.success)
                {
                    nodeCountEl.textContent =
                            data.activeNodes;

                    ruleCountEl.textContent =
                            data.ruleCount;
                }
                else
                {
                    nodeCountEl.textContent = '0';
                    ruleCountEl.textContent = '0';

                    console.error(data.message);
                }
            })

            .catch(error =>
            {
                nodeCountEl.textContent = '0';
                ruleCountEl.textContent = '0';

                console.error(error);
            });
}

// ===============================
// USER NAME
// ===============================

const storedUsername =
        sessionStorage.getItem('username')
        || localStorage.getItem('username');

if (storedUsername)
{
    userNameDisplay.textContent = storedUsername;
}


// ===============================
// LOGOUT
// ===============================

logoutBtn.addEventListener('click', () =>
{
    sessionStorage.clear();

    localStorage.removeItem('isLoggedIn');

    window.location.href = '../HTML/Main.html';
});


// ===============================
// CARD NAVIGATION
// ===============================

document
        .querySelectorAll('.management-card')
        .forEach(card =>
        {
            card.addEventListener('click', () =>
            {
                const page = card.getAttribute('data-page');

                if (page)
                {
                    card.style.transform = 'scale(0.98)';

                    setTimeout(() =>
                    {
                        window.location.href = page;
                    }, 150);
                }
            });
        });


// ===============================
// INITIALIZATION
// ===============================

loadCounts();

setCoreState(true);