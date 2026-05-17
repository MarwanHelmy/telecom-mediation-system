const nodesTableBody = document.getElementById('nodesTableBody');
const totalNodesSpan = document.getElementById('totalNodes');
const activeNodesSpan = document.getElementById('activeNodes');
const inactiveNodesSpan = document.getElementById('inactiveNodes');
const searchInput = document.getElementById('searchInput');
const addNodeBtn = document.getElementById('addNodeBtn');
const nodeModal = document.getElementById('nodeModal');
const deleteModal = document.getElementById('deleteModal');
const modalTitle = document.getElementById('modalTitle');
const nodeForm = document.getElementById('nodeForm');
const toast = document.getElementById('toast');
const loadingOverlay = document.getElementById('loadingOverlay');

const nodeIdField = document.getElementById('nodeId');
const nodeNameField = document.getElementById('nodeName');
const nodeIpField = document.getElementById('nodeIp');
const nodePortField = document.getElementById('nodePort');
const nodeUsernameField = document.getElementById('nodeUsername');
const nodePasswordField = document.getElementById('nodePassword');
const nodeStatusCheckbox = document.getElementById('nodeStatus');
const statusLabel = document.getElementById('statusLabel');

const protocolRadios = document.querySelectorAll('input[name="protocol"]');
const typeRadios = document.querySelectorAll('input[name="nodeType"]');

let currentDeleteId = null;

function getSelectedProtocol() {
    let selected = 'SFTP';
    protocolRadios.forEach(radio => {
        if (radio.checked) {
            selected = radio.value;
        }
    });
    return selected;
}

function getSelectedType() {
    let selected = 'UPSTREAM';
    typeRadios.forEach(radio => {
        if (radio.checked) {
            selected = radio.value;
        }
    });
    return selected;
}

function showToast(message, type = 'success') {
    toast.textContent = message;
    toast.className = `toast show ${type}`;
    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}

function escapeHtml(str) {
    if (!str)
        return '';
    return String(str).replace(/[&<>]/g, function (m) {
        if (m === '&')
            return '&amp;';
        if (m === '<')
            return '&lt;';
        if (m === '>')
            return '&gt;';
        return m;
    });
}

function isNodeActive(status) {
    return status === true || status === 'true' || status === 't' || status === 'ACTIVE' || status === 'active' || status === 1;
}

function getStatusText(status) {
    return isNodeActive(status) ? 'ACTIVE' : 'INACTIVE';
}

function getStatusClass(status) {
    return isNodeActive(status) ? 'active' : 'inactive';
}

function getStatusIcon(status) {
    return isNodeActive(status) ? 'circle-check' : 'circle-x';
}

function validateNodeName(name) {
    return name && name.trim().length > 0 && name.trim().length <= 100;
}

function validateIP(ip) {
    const ipRegex = /^172\.30\.(\d{1,3})\.(\d{1,3})$/;
    if (!ipRegex.test(ip))
        return false;
    const parts = ip.split('.');
    const third = parseInt(parts[2]);
    const fourth = parseInt(parts[3]);
    if (third < 0 || third > 255 || fourth < 0 || fourth > 255)
        return false;
    if (ip === '172.30.0.1')
        return false;
    if (ip === '172.30.255.255')
        return false;
    return true;
}

function validatePort(port) {
    const p = parseInt(port);
    return !isNaN(p) && p >= 1 && p <= 65535;
}

function validateUsername(username) {
    return username && username.trim().length > 0 && username.trim().length <= 50;
}

function validatePassword(password) {
    return password && password.trim().length > 0;
}

function validateForm() {
    let isValid = true;

    if (!validateNodeName(nodeNameField.value)) {
        nodeNameField.classList.add('invalid');
        document.getElementById('nodeNameError').classList.add('show');
        isValid = false;
    } else {
        nodeNameField.classList.remove('invalid');
        document.getElementById('nodeNameError').classList.remove('show');
    }

    if (!validateIP(nodeIpField.value)) {
        nodeIpField.classList.add('invalid');
        document.getElementById('nodeIpError').classList.add('show');
        isValid = false;
    } else {
        nodeIpField.classList.remove('invalid');
        document.getElementById('nodeIpError').classList.remove('show');
    }

    if (!validatePort(nodePortField.value)) {
        nodePortField.classList.add('invalid');
        document.getElementById('nodePortError').classList.add('show');
        isValid = false;
    } else {
        nodePortField.classList.remove('invalid');
        document.getElementById('nodePortError').classList.remove('show');
    }

    if (!validateUsername(nodeUsernameField.value)) {
        nodeUsernameField.classList.add('invalid');
        document.getElementById('nodeUsernameError').classList.add('show');
        isValid = false;
    } else {
        nodeUsernameField.classList.remove('invalid');
        document.getElementById('nodeUsernameError').classList.remove('show');
    }

    if (!validatePassword(nodePasswordField.value)) {
        nodePasswordField.classList.add('invalid');
        document.getElementById('nodePasswordError').classList.add('show');
        isValid = false;
    } else {
        nodePasswordField.classList.remove('invalid');
        document.getElementById('nodePasswordError').classList.remove('show');
    }

    return isValid;
}

function getContextPath() {
    const pathname = window.location.pathname;
    const parts = pathname.split('/');
    if (parts.length > 1 && parts[1] && !parts[1].includes('.')) {
        return '/' + parts[1];
    }
    return '';
}

async function loadNodesFromJSON() {
    loadingOverlay.classList.add('show');
    try {
        const contextPath = getContextPath();
        const url = contextPath + '/GetAllNodesServlet';
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error('HTTP ERROR: ' + response.status);
        }
        const data = await response.json();
        window.nodesData = data.map(node => ({
                NODE_ID: node.NODE_ID || node.id,
                NODE_NAME: node.NODE_NAME || node.name,
                NODE_IP: node.NODE_IP || node.ip,
                NODE_PORT: node.NODE_PORT || node.port,
                NODE_PROTOCOL: node.NODE_PROTOCOL || node.protocol || 'SFTP',
                NODE_TYPE: node.NODE_TYPE || node.type || 'UPSTREAM',
                NODE_USER_NAME: node.NODE_USER_NAME || node.username,
                NODE_PASSWORD: node.NODE_PASSWORD || node.password,
                NODE_STATUS: node.NODE_STATUS ?? node.isactive ?? false
            }));
        renderTable();
    } catch (error) {
        console.error(error);
        showToast('Failed To Load Nodes', 'error');
        window.nodesData = [];
        renderTable();
    } finally {
        loadingOverlay.classList.remove('show');
    }
}

function updateStats() {
    const nodes = window.nodesData || [];
    const active = nodes.filter(n => isNodeActive(n.NODE_STATUS)).length;
    const inactive = nodes.length - active;
    totalNodesSpan.textContent = nodes.length;
    activeNodesSpan.textContent = active;
    inactiveNodesSpan.textContent = inactive;
}

function renderTable(filter = '') {
    let nodes = window.nodesData || [];
    if (filter) {
        const search = filter.toLowerCase();
        nodes = nodes.filter(node =>
            (node.NODE_NAME && node.NODE_NAME.toLowerCase().includes(search)) ||
                    (node.NODE_IP && node.NODE_IP.includes(search)) ||
                    (node.NODE_TYPE && node.NODE_TYPE.toLowerCase().includes(search))
        );
    }
    if (nodes.length === 0) {
        nodesTableBody.innerHTML = `<tr class="empty-row"><td colspan="7">No nodes found</td></tr>`;
        updateStats();
        return;
    }
    nodesTableBody.innerHTML = nodes.map(node => {
        const protocol = (node.NODE_PROTOCOL || 'SFTP').toLowerCase();
        const protocolClass = protocol === 'ftp' ? 'ftp' : 'sftp';
        const type = (node.NODE_TYPE || 'UPSTREAM').toLowerCase();
        const typeClass = type === 'upstream' ? 'upstream' : 'downstream';
        const typeIcon = type === 'upstream' ? 'arrow-up' : 'arrow-down';
        const statusClass = getStatusClass(node.NODE_STATUS);
        const statusIcon = getStatusIcon(node.NODE_STATUS);
        const statusText = getStatusText(node.NODE_STATUS);
        return `
        <tr>
            <td>${node.NODE_ID || ''}</td>
            <td><strong>${escapeHtml(node.NODE_NAME || '')}</strong></td>
            <td>${escapeHtml(node.NODE_IP || '')}:${node.NODE_PORT || ''}</td>
            <td><span class="protocol-badge ${protocolClass}">${escapeHtml((node.NODE_PROTOCOL || 'SFTP').toUpperCase())}</span></td>
            <td><span class="type-badge ${typeClass}"><i class="ti ti-${typeIcon}"></i>${(node.NODE_TYPE || 'UPSTREAM').toUpperCase()}</span></td>
            <td><span class="status-badge ${statusClass}"><i class="ti ti-${statusIcon}"></i>${statusText}</span></td>
            <td>
    <div class="action-buttons">

        <button class="action-btn edit-btn"
                onclick="editNode(${node.NODE_ID})">
            <i class="ti ti-edit"></i>
        </button>

        ${
                isNodeActive(node.NODE_STATUS)
                ?
                `
            <button class="action-btn stop-btn"
                    onclick="toggleContainer(${node.NODE_ID}, 'STOP')">
                <i class="ti ti-player-stop"></i>
            </button>
            `
                :
                `
            <button class="action-btn start-btn"
                    onclick="toggleContainer(${node.NODE_ID}, 'START')">
                <i class="ti ti-player-play"></i>
            </button>
            `
                }

        <button class="action-btn delete-btn"
                onclick="confirmDelete(${node.NODE_ID})">
            <i class="ti ti-trash"></i>
        </button>

    </div>
</td>
        </tr>
        `;
    }).join('');
    updateStats();
}

async function saveNodeToServlet(nodeData, isEdit, nodeId) {
    loadingOverlay.classList.add('show');
    try {
        const contextPath = getContextPath();
        const url = contextPath + '/SaveNodeServlet';
        const formData = new URLSearchParams();
        if (isEdit && nodeId) {
            formData.append('nodeId', nodeId);
        }
        formData.append('nodeName', nodeData.nodeName);
        formData.append('nodeIp', nodeData.nodeIp);
        formData.append('nodePort', nodeData.nodePort);
        formData.append('nodeProtocol', nodeData.nodeProtocol);
        formData.append('nodeType', nodeData.nodeType);
        formData.append('nodeUserName', nodeData.nodeUserName);
        formData.append('nodePassword', nodeData.nodePassword);
        formData.append('nodeStatus', nodeData.status);
        const response = await fetch(url, {
            method: 'POST',
            body: formData,
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            }
        });
        const result = await response.json();
        console.log('Save response:', result);
        if (result.success) {
            showToast(isEdit ? 'Node Updated Successfully' : 'Node Added Successfully');
            await loadNodesFromJSON();
            return true;
        } else {
            if (result.errorType === 'DUPLICATE_IP') {
                showToast(result.message, 'error');
                console.log('Duplicate IP detected:', result);
            } else {
                showToast(result.message || 'Failed To Save Node', 'error');
            }
            return false;
        }
    } catch (error) {
        console.error(error);
        showToast('Failed To Save Node', 'error');
        return false;
    } finally {
        loadingOverlay.classList.remove('show');
    }
}

async function deleteNodeFromServlet(nodeId) {
    loadingOverlay.classList.add('show');
    try {
        const contextPath = getContextPath();
        const url = contextPath + '/DeleteNodeServlet';
        const formData = new URLSearchParams();
        formData.append('nodeId', nodeId);
        const response = await fetch(url, {
            method: 'POST',
            body: formData,
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            }
        });
        const result = await response.json();
        if (result.success) {
            showToast('Node Deleted Successfully');
            await loadNodesFromJSON();
            return true;
        }
        showToast(result.message || 'Delete Failed', 'error');
        return false;
    } catch (error) {
        console.error(error);
        showToast('Failed To Delete Node', 'error');
        return false;
    } finally {
        loadingOverlay.classList.remove('show');
    }
}

async function toggleContainer(nodeId, action)
{
    loadingOverlay.classList.add('show');

    try
    {
        const contextPath = getContextPath();

        const url =
            contextPath + '/ToggleDockerContainerServlet';

        const formData = new URLSearchParams();

        formData.append('nodeId', nodeId);

        formData.append('action', action);

        const response = await fetch(url,
        {
            method: 'POST',
            body: formData,
            headers:
            {
                'Content-Type':
                'application/x-www-form-urlencoded'
            }
        });

        const result = await response.json();

        if(result.success)
        {
            showToast(result.message);

            await loadNodesFromJSON();
        }
        else
        {
            showToast(result.message, 'error');
        }
    }
    catch(error)
    {
        console.error(error);

        showToast(
            'Failed To Toggle Container',
            'error'
        );
    }
    finally
    {
        loadingOverlay.classList.remove('show');
    }
}

function openAddModal() {
    modalTitle.textContent = 'Add New Node';
    nodeForm.reset();
    nodeIdField.value = '';
    nodeStatusCheckbox.checked = true;
    statusLabel.textContent = 'ACTIVE';
    statusLabel.className = 'status-label active';
    document.querySelector('input[name="protocol"][value="SFTP"]').checked = true;
    document.querySelector('input[name="nodeType"][value="UPSTREAM"]').checked = true;
    nodePasswordField.type = 'password';
    const icon = document.querySelector('#togglePassword i');
    if (icon) {
        icon.className = 'ti ti-eye';
    }
    nodeModal.style.display = 'flex';
}

window.editNode = function (id) {
    const nodes = window.nodesData || [];
    const node = nodes.find(n => n.NODE_ID == id);
    if (!node) {
        showToast('Node Not Found', 'error');
        return;
    }
    modalTitle.textContent = 'Edit Node';
    nodeIdField.value = node.NODE_ID;
    nodeNameField.value = node.NODE_NAME || '';
    nodeIpField.value = node.NODE_IP || '';
    nodePortField.value = node.NODE_PORT || '';
    nodeUsernameField.value = node.NODE_USER_NAME || '';
    nodePasswordField.value = node.NODE_PASSWORD || '';
    const active = isNodeActive(node.NODE_STATUS);
    nodeStatusCheckbox.checked = active;
    statusLabel.textContent = active ? 'ACTIVE' : 'INACTIVE';
    statusLabel.className = active ? 'status-label active' : 'status-label inactive';
    const protocol = (node.NODE_PROTOCOL || 'SFTP').toUpperCase();
    const protocolRadio = document.querySelector(`input[name="protocol"][value="${protocol}"]`);
    if (protocolRadio) {
        protocolRadio.checked = true;
    }
    const type = (node.NODE_TYPE || 'UPSTREAM').toUpperCase();
    const typeRadio = document.querySelector(`input[name="nodeType"][value="${type}"]`);
    if (typeRadio) {
        typeRadio.checked = true;
    }
    nodePasswordField.type = 'password';
    const icon = document.querySelector('#togglePassword i');
    if (icon) {
        icon.className = 'ti ti-eye';
    }
    nodeModal.style.display = 'flex';
};

window.confirmDelete = function (id) {
    const nodes = window.nodesData || [];
    const node = nodes.find(n => n.NODE_ID == id);
    if (!node)
        return;
    currentDeleteId = id;
    document.getElementById('deleteNodeName').textContent = node.NODE_NAME;
    deleteModal.style.display = 'flex';
};

function closeModals() {
    nodeModal.style.display = 'none';
    deleteModal.style.display = 'none';
    currentDeleteId = null;
}

nodeForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    if (!validateForm()) {
        showToast('Please Fix Validation Errors', 'error');
        return;
    }
    const nodeData = {
        nodeName: nodeNameField.value.trim(),
        nodeIp: nodeIpField.value.trim(),
        nodePort: parseInt(nodePortField.value),
        nodeProtocol: getSelectedProtocol(),
        nodeType: getSelectedType(),
        nodeUserName: nodeUsernameField.value.trim(),
        nodePassword: nodePasswordField.value,
        status: nodeStatusCheckbox.checked ? true : false
    };
    const isEdit = nodeIdField.value !== '';
    const success = await saveNodeToServlet(nodeData, isEdit, nodeIdField.value);
    if (success) {
        closeModals();
    }
});

document.getElementById('confirmDeleteBtn').addEventListener('click', async () => {
    if (currentDeleteId) {
        const success = await deleteNodeFromServlet(currentDeleteId);
        if (success) {
            closeModals();
        }
    }
});

const togglePasswordBtn = document.getElementById('togglePasswordBtn');
if (togglePasswordBtn) {
    togglePasswordBtn.addEventListener('click', function (e) {
        e.preventDefault();
        if (nodePasswordField.type === 'password') {
            nodePasswordField.type = 'text';
        } else {
            nodePasswordField.type = 'password';
        }
        const icon = togglePasswordBtn.querySelector('i');
        if (icon) {
            icon.className = nodePasswordField.type === 'password' ? 'ti ti-eye' : 'ti ti-eye-off';
        }
    });
}

nodeStatusCheckbox.addEventListener('change', () => {
    if (nodeStatusCheckbox.checked) {
        statusLabel.textContent = 'ACTIVE';
        statusLabel.className = 'status-label active';
    } else {
        statusLabel.textContent = 'INACTIVE';
        statusLabel.className = 'status-label inactive';
    }
});

addNodeBtn.addEventListener('click', openAddModal);
searchInput.addEventListener('input', (e) => {
    renderTable(e.target.value);
});
document.getElementById('closeModalBtn').addEventListener('click', closeModals);
document.getElementById('cancelModalBtn').addEventListener('click', closeModals);
document.getElementById('closeDeleteModalBtn').addEventListener('click', closeModals);
document.getElementById('cancelDeleteBtn').addEventListener('click', closeModals);

window.addEventListener('click', (e) => {
    if (e.target === nodeModal) {
        closeModals();
    }
    if (e.target === deleteModal) {
        closeModals();
    }
});

loadNodesFromJSON();