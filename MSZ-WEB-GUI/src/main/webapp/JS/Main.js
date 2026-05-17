(function () {
    const container = document.getElementById('networkContainer');
    const svgCanvas = document.getElementById('linkCanvas');

    const leftNodes = ['msc', 'pgw', 'smsc', 'ggsn'];
    const rightNodes = ['billing', 'fraud', 'dwh'];
    const storageId = 'storage';
    const archiveId = 'archive';
    const coreId = 'core';

    function getNodeCenter(nodeId) {
        const el = document.getElementById(nodeId);
        if (!el)
            return {x: 0, y: 0};
        const containerRect = container.getBoundingClientRect();
        const rect = el.getBoundingClientRect();
        return {
            x: rect.left + rect.width / 2 - containerRect.left,
            y: rect.top + rect.height / 2 - containerRect.top
        };
    }

    function getCoreCenter() {
        const coreEl = document.getElementById(coreId);
        if (!coreEl)
            return {x: 0, y: 0};
        const containerRect = container.getBoundingClientRect();
        const rect = coreEl.getBoundingClientRect();
        return {x: rect.left + rect.width / 2 - containerRect.left, y: rect.top + rect.height / 2 - containerRect.top};
    }

    function drawDashedLines() {
        if (!svgCanvas || !container)
            return;
        const rect = container.getBoundingClientRect();
        svgCanvas.setAttribute('width', rect.width);
        svgCanvas.setAttribute('height', rect.height);
        svgCanvas.innerHTML = '';
        const core = getCoreCenter();
        if (core.x === 0 && core.y === 0)
            return;

        function addLine(x1, y1, x2, y2, color = '#059EC3') {
            const line = document.createElementNS("http://www.w3.org/2000/svg", "line");
            line.setAttribute('x1', x1);
            line.setAttribute('y1', y1);
            line.setAttribute('x2', x2);
            line.setAttribute('y2', y2);
            line.setAttribute('stroke', color);
            line.setAttribute('stroke-width', '2');
            line.setAttribute('stroke-dasharray', '8 6');
            line.setAttribute('stroke-opacity', '0.5');
            svgCanvas.appendChild(line);
        }
        leftNodes.forEach(n => {
            const p = getNodeCenter(n);
            if (p.x || p.y)
                addLine(p.x, p.y, core.x, core.y, '#059EC3');
        });
        rightNodes.forEach(n => {
            const p = getNodeCenter(n);
            if (p.x || p.y)
                addLine(core.x, core.y, p.x, p.y, '#41D3E1');
        });
        const s = getNodeCenter(storageId);
        if (s.x || s.y)
            addLine(core.x, core.y, s.x, s.y, '#93E3EB');
        const a = getNodeCenter(archiveId);
        if (a.x || a.y)
            addLine(core.x, core.y, a.x, a.y, '#097D99');
    }

    function lightNode(nodeId, duration = 420) {
        const el = document.getElementById(nodeId);
        if (!el)
            return;
        el.classList.add('active');
        setTimeout(() => el.classList.remove('active'), duration);
    }

    function flyPacketWithGlow(fromId, toId, colorHex, duration = 800) {
        lightNode(fromId, 380);
        if (toId === coreId || fromId === coreId) {
            lightNode(coreId, 400);
        }
        setTimeout(() => {
            if (toId !== coreId)
                lightNode(toId, 340);
        }, duration - 60);

        const start = getNodeCenter(fromId);
        const end = getNodeCenter(toId);
        if ((start.x === 0 && start.y === 0) || (end.x === 0 && end.y === 0))
            return;

        const icon = document.createElement('i');
        icon.className = 'packet ti ti-file-description';
        icon.style.color = colorHex;
        icon.style.fontSize = '22px';
        icon.style.position = 'absolute';
        icon.style.left = start.x + 'px';
        icon.style.top = start.y + 'px';
        icon.style.zIndex = '60';
        icon.style.textShadow = `0 0 10px ${colorHex}`;
        container.appendChild(icon);

        const startTime = performance.now();
        function step(now) {
            let t = Math.min((now - startTime) / duration, 1);
            const eased = 1 - Math.pow(1 - t, 2.2);
            const x = start.x + (end.x - start.x) * eased;
            const y = start.y + (end.y - start.y) * eased - Math.sin(t * Math.PI) * 12;
            icon.style.left = x + 'px';
            icon.style.top = y + 'px';
            if (t < 1)
                requestAnimationFrame(step);
            else if (icon.remove)
                icon.remove();
        }
        requestAnimationFrame(step);
    }

    let intervals = [];
    function clearFlows() {
        intervals.forEach(clearInterval);
        intervals = [];
        document.querySelectorAll('.packet').forEach(p => p.remove());
    }

    function startVibrantFlows() {
        clearFlows();
        const upstream = [
            {id: 'msc', col: '#059EC3', delay: 0},
            {id: 'pgw', col: '#0E5573', delay: 280},
            {id: 'smsc', col: '#2B2B5A', delay: 560},
            {id: 'ggsn', col: '#097D99', delay: 840}
        ];
        upstream.forEach(({id, col, delay}) => {
            setTimeout(() => {
                flyPacketWithGlow(id, 'core', col, 760);
                const interval = setInterval(() => flyPacketWithGlow(id, 'core', col, 760), 2800);
                intervals.push(interval);
            }, delay);
        });

        const downstream = [
            {id: 'billing', col: '#41D3E1', delay: 400},
            {id: 'fraud', col: '#2B2B5A', delay: 1100},
            {id: 'dwh', col: '#059EC3', delay: 1800}
        ];
        downstream.forEach(({id, col, delay}) => {
            setTimeout(() => {
                flyPacketWithGlow('core', id, col, 780);
                const interval = setInterval(() => flyPacketWithGlow('core', id, col, 780), 3100);
                intervals.push(interval);
            }, delay);
        });

        setTimeout(() => {
            flyPacketWithGlow('core', 'storage', '#93E3EB', 740);
            const storeInt = setInterval(() => flyPacketWithGlow('core', 'storage', '#93E3EB', 740), 3600);
            intervals.push(storeInt);
        }, 700);

        setTimeout(() => {
            flyPacketWithGlow('core', 'archive', '#097D99', 760);
            const archInt = setInterval(() => flyPacketWithGlow('core', 'archive', '#097D99', 760), 4000);
            intervals.push(archInt);
        }, 1300);

        setTimeout(() => {
            const revInt = setInterval(() => flyPacketWithGlow('storage', 'core', '#41D3E1', 720), 5200);
            intervals.push(revInt);
        }, 2200);
    }

    const resizeObserver = new ResizeObserver(() => drawDashedLines());
    if (container)
        resizeObserver.observe(container);
    window.addEventListener('resize', () => setTimeout(drawDashedLines, 80));
    setTimeout(() => {
        drawDashedLines();
        startVibrantFlows();
    }, 180);
    window.addEventListener('load', drawDashedLines);
    window.addEventListener('beforeunload', () => {
        clearFlows();
        resizeObserver.disconnect();
    });
})();