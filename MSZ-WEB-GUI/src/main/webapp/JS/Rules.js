const rulesTableBody = document.getElementById('rulesTableBody');
const totalRulesSpan = document.getElementById('totalRules');
const activeRulesSpan = document.getElementById('activeRules');
const inactiveRulesSpan = document.getElementById('inactiveRules');
const searchInput = document.getElementById('searchInput');
const addRuleBtn = document.getElementById('addRuleBtn');
const ruleModal = document.getElementById('ruleModal');
const deleteModal = document.getElementById('deleteModal');
const modalTitle = document.getElementById('modalTitle');
const ruleForm = document.getElementById('ruleForm');
const toast = document.getElementById('toast');
const loadingOverlay = document.getElementById('loadingOverlay');

const ruleIdField = document.getElementById('ruleId');
const sourceNodeSelect = document.getElementById('sourceNodeId');
const destinationNodeSelect = document.getElementById('destinationNodeId');
const ruleStatusCheckbox = document.getElementById('ruleStatus');
const statusLabel = document.getElementById('statusLabel');

let upstreamNodes = [];
let downstreamNodes = [];
let rulesData = [];
let currentDeleteId = null;

function showToast(message, type = 'success') {
    toast.textContent = message;
    toast.className = `toast show ${type}`;
    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}

function getContextPath() {
    const pathname = window.location.pathname;
    const parts = pathname.split('/');
    if (parts.length > 1 && parts[1] && !parts[1].includes('.')) {
        return '/' + parts[1];
    }
    return '';
}

function escapeHtml(str) {
    if (!str) return '';
    return String(str).replace(/[&<>]/g, function(m) {
        if (m === '&') return '&amp;';
        if (m === '<') return '&lt;';
        if (m === '>') return '&gt;';
        return m;
    });
}

function isRuleActive(status) {
    return status === true || status === 'true' || status === 't' || status === 'ACTIVE' || status === 'active' || status === 1;
}

function getStatusText(status) {
    return isRuleActive(status) ? 'ACTIVE' : 'INACTIVE';
}

function getStatusClass(status) {
    return isRuleActive(status) ? 'active' : 'inactive';
}

function getStatusIcon(status) {
    return isRuleActive(status) ? 'circle-check' : 'circle-x';
}

function getNodeNameById(nodeId, type) {
    const nodes = type === 'source' ? upstreamNodes : downstreamNodes;
    const node = nodes.find(n => n.id == nodeId);
    return node ? node.name : `Node ID: ${nodeId}`;
}

async function loadUpstreamNodes() {
    try {
        const contextPath = getContextPath();
        const url = contextPath + '/GetUpstreamNodesServlet';
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error('HTTP ERROR: ' + response.status);
        }
        const data = await response.json();
        upstreamNodes = data;
        populateSourceSelect();
    } catch (error) {
        console.error(error);
        showToast('Failed to load upstream nodes', 'error');
        upstreamNodes = [];
    }
}

async function loadDownstreamNodes() {
    try {
        const contextPath = getContextPath();
        const url = contextPath + '/GetDownstreamNodesServlet';
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error('HTTP ERROR: ' + response.status);
        }
        const data = await response.json();
        downstreamNodes = data;
        populateDestinationSelect();
    } catch (error) {
        console.error(error);
        showToast('Failed to load downstream nodes', 'error');
        downstreamNodes = [];
    }
}

function populateSourceSelect() {
    const options = upstreamNodes.map(node => 
        `<option value="${node.id}">${escapeHtml(node.name)} (${node.ip}:${node.port})</option>`
    ).join('');
    
    sourceNodeSelect.innerHTML = '<option value="">Select Source Node...</option>' + options;
}

function populateDestinationSelect() {
    const options = downstreamNodes.map(node => 
        `<option value="${node.id}">${escapeHtml(node.name)} (${node.ip}:${node.port})</option>`
    ).join('');
    
    destinationNodeSelect.innerHTML = '<option value="">Select Destination Node...</option>' + options;
}

async function loadRules() {
    loadingOverlay.classList.add('show');
    try {
        const contextPath = getContextPath();
        const url = contextPath + '/GetAllRulesServlet';
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error('HTTP ERROR: ' + response.status);
        }
        const data = await response.json();
        rulesData = data;
        renderTable();
    } catch (error) {
        console.error(error);
        showToast('Failed To Load Rules', 'error');
        rulesData = [];
        renderTable();
    } finally {
        loadingOverlay.classList.remove('show');
    }
}

function updateStats() {
    const active = rulesData.filter(r => isRuleActive(r.is_active)).length;
    const inactive = rulesData.length - active;
    totalRulesSpan.textContent = rulesData.length;
    activeRulesSpan.textContent = active;
    inactiveRulesSpan.textContent = inactive;
}

function renderTable(filter = '') {
    let rules = [...rulesData];
    if (filter) {
        const search = filter.toLowerCase();
        rules = rules.filter(rule => {
            const sourceName = getNodeNameById(rule.source_node_id, 'source').toLowerCase();
            const destName = getNodeNameById(rule.destination_node_id, 'destination').toLowerCase();
            return rule.id.toString().includes(search) || 
                   sourceName.includes(search) || 
                   destName.includes(search);
        });
    }
    
    if (rules.length === 0) {
        rulesTableBody.innerHTML = `<tr class="empty-row"><td colspan="5">No rules found</td></tr>`;
        updateStats();
        return;
    }
    
    rulesTableBody.innerHTML = rules.map(rule => {
        const sourceName = getNodeNameById(rule.source_node_id, 'source');
        const destName = getNodeNameById(rule.destination_node_id, 'destination');
        const statusClass = getStatusClass(rule.is_active);
        const statusIcon = getStatusIcon(rule.is_active);
        const statusText = getStatusText(rule.is_active);
        
        return `
        <tr>
            <td>${rule.id}</td>
            <td>
                <div class="rule-flow">
                    <span class="rule-source"><i class="ti ti-arrow-up"></i> ${escapeHtml(sourceName)}</span>
                    <i class="ti ti-arrow-right rule-arrow"></i>
                    <span class="rule-destination"><i class="ti ti-arrow-down"></i> ${escapeHtml(destName)}</span>
                </div>
            </td>
            <td>${escapeHtml(destName)}</td>
            <td><span class="status-badge ${statusClass}"><i class="ti ti-${statusIcon}"></i>${statusText}</span></td>
            <td>
                <div class="action-buttons">
                    <button class="action-btn edit-btn" onclick="editRule(${rule.id})">
                        <i class="ti ti-edit"></i>
                    </button>
                    <button class="action-btn delete-btn" onclick="confirmDelete(${rule.id})">
                        <i class="ti ti-trash"></i>
                    </button>
                </div>
            </td>
        </tr>
        `;
    }).join('');
    updateStats();
}

function validateForm() {
    let isValid = true;

    if (!sourceNodeSelect.value) {
        sourceNodeSelect.classList.add('invalid');
        document.getElementById('sourceNodeError').classList.add('show');
        isValid = false;
    } else {
        sourceNodeSelect.classList.remove('invalid');
        document.getElementById('sourceNodeError').classList.remove('show');
    }

    if (!destinationNodeSelect.value) {
        destinationNodeSelect.classList.add('invalid');
        document.getElementById('destinationNodeError').classList.add('show');
        isValid = false;
    } else {
        destinationNodeSelect.classList.remove('invalid');
        document.getElementById('destinationNodeError').classList.remove('show');
    }

    if (sourceNodeSelect.value && destinationNodeSelect.value && 
        sourceNodeSelect.value === destinationNodeSelect.value) {
        showToast('Source and destination nodes cannot be the same', 'error');
        destinationNodeSelect.classList.add('invalid');
        document.getElementById('destinationNodeError').classList.add('show');
        document.getElementById('destinationNodeError').textContent = 'Cannot route to same node';
        isValid = false;
    }

    return isValid;
}

async function saveRuleToServlet(ruleData, isEdit, ruleId) {
    loadingOverlay.classList.add('show');
    try {
        const contextPath = getContextPath();
        const url = contextPath + '/SaveRuleServlet';
        const formData = new URLSearchParams();
        if (isEdit && ruleId) {
            formData.append('ruleId', ruleId);
        }
        formData.append('sourceNodeId', ruleData.sourceNodeId);
        formData.append('destinationNodeId', ruleData.destinationNodeId);
        formData.append('isActive', ruleData.isActive);
        
        const response = await fetch(url, {
            method: 'POST',
            body: formData,
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            }
        });
        const result = await response.json();
        
        if (result.success) {
            showToast(isEdit ? 'Rule Updated Successfully' : 'Rule Added Successfully');
            await loadRules();
            return true;
        } else {
            if (result.errorType === 'DUPLICATE_RULE') {
                showToast(result.message || 'This rule already exists! The same source and destination combination is already configured.', 'error');
            } else {
                showToast(result.message || 'Failed To Save Rule', 'error');
            }
            return false;
        }
    } catch (error) {
        console.error(error);
        showToast('Failed To Save Rule', 'error');
        return false;
    } finally {
        loadingOverlay.classList.remove('show');
    }
}

async function deleteRuleFromServlet(ruleId) {
    loadingOverlay.classList.add('show');
    try {
        const contextPath = getContextPath();
        const url = contextPath + '/DeleteRuleServlet';
        const formData = new URLSearchParams();
        formData.append('ruleId', ruleId);
        
        const response = await fetch(url, {
            method: 'POST',
            body: formData,
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            }
        });
        const result = await response.json();
        
        if (result.success) {
            showToast('Rule Deleted Successfully');
            await loadRules();
            return true;
        }
        showToast(result.message || 'Delete Failed', 'error');
        return false;
    } catch (error) {
        console.error(error);
        showToast('Failed To Delete Rule', 'error');
        return false;
    } finally {
        loadingOverlay.classList.remove('show');
    }
}

function openAddModal() {
    modalTitle.textContent = 'Add New Rule';
    ruleForm.reset();
    ruleIdField.value = '';
    ruleStatusCheckbox.checked = true;
    statusLabel.textContent = 'ACTIVE';
    statusLabel.className = 'status-label active';
    sourceNodeSelect.value = '';
    destinationNodeSelect.value = '';
    ruleModal.style.display = 'flex';
}

window.editRule = async function(id) {
    const rule = rulesData.find(r => r.id == id);
    if (!rule) {
        showToast('Rule Not Found', 'error');
        return;
    }
    
    // Ensure nodes are loaded
    if (upstreamNodes.length === 0) {
        await loadUpstreamNodes();
    }
    if (downstreamNodes.length === 0) {
        await loadDownstreamNodes();
    }
    
    modalTitle.textContent = 'Edit Rule';
    ruleIdField.value = rule.id;
    sourceNodeSelect.value = rule.source_node_id;
    destinationNodeSelect.value = rule.destination_node_id;
    const active = isRuleActive(rule.is_active);
    ruleStatusCheckbox.checked = active;
    statusLabel.textContent = active ? 'ACTIVE' : 'INACTIVE';
    statusLabel.className = active ? 'status-label active' : 'status-label inactive';
    ruleModal.style.display = 'flex';
};

window.confirmDelete = function(id) {
    const rule = rulesData.find(r => r.id == id);
    if (!rule) return;
    currentDeleteId = id;
    document.getElementById('deleteRuleId').textContent = `ID: #${id}`;
    const sourceName = getNodeNameById(rule.source_node_id, 'source');
    const destName = getNodeNameById(rule.destination_node_id, 'destination');
    document.getElementById('deleteRulePath').textContent = `${sourceName} → ${destName}`;
    deleteModal.style.display = 'flex';
};

function closeModals() {
    ruleModal.style.display = 'none';
    deleteModal.style.display = 'none';
    currentDeleteId = null;
}

ruleForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    if (!validateForm()) {
        showToast('Please fix validation errors', 'error');
        return;
    }
    
    const ruleData = {
        sourceNodeId: parseInt(sourceNodeSelect.value),
        destinationNodeId: parseInt(destinationNodeSelect.value),
        isActive: ruleStatusCheckbox.checked
    };
    
    const isEdit = ruleIdField.value !== '';
    const success = await saveRuleToServlet(ruleData, isEdit, ruleIdField.value);
    if (success) {
        closeModals();
    }
});

document.getElementById('confirmDeleteBtn').addEventListener('click', async () => {
    if (currentDeleteId) {
        const success = await deleteRuleFromServlet(currentDeleteId);
        if (success) {
            closeModals();
        }
    }
});

ruleStatusCheckbox.addEventListener('change', () => {
    if (ruleStatusCheckbox.checked) {
        statusLabel.textContent = 'ACTIVE';
        statusLabel.className = 'status-label active';
    } else {
        statusLabel.textContent = 'INACTIVE';
        statusLabel.className = 'status-label inactive';
    }
});

addRuleBtn.addEventListener('click', openAddModal);
searchInput.addEventListener('input', (e) => {
    renderTable(e.target.value);
});

document.getElementById('closeModalBtn').addEventListener('click', closeModals);
document.getElementById('cancelModalBtn').addEventListener('click', closeModals);
document.getElementById('closeDeleteModalBtn').addEventListener('click', closeModals);
document.getElementById('cancelDeleteBtn').addEventListener('click', closeModals);

window.addEventListener('click', (e) => {
    if (e.target === ruleModal) closeModals();
    if (e.target === deleteModal) closeModals();
});

// Initialize
Promise.all([loadUpstreamNodes(), loadDownstreamNodes()]).then(() => {
    loadRules();
});