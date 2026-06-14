document.addEventListener('DOMContentLoaded', function () {

    const form = document.querySelector('form');
    if (!form) return;

    form.setAttribute('novalidate', true);

    // ── HELPERS ──
    function mostrarError(field, msg) {
        field.classList.add('is-invalid');
        const div = document.createElement('div');
        div.className = 'invalid-feedback';
        div.textContent = msg;
        field.parentNode.insertBefore(div, field.nextSibling);
    }

    function limpiarErrores() {
        document.querySelectorAll('.is-invalid').forEach(el => el.classList.remove('is-invalid'));
        document.querySelectorAll('.invalid-feedback').forEach(el => el.remove());
        document.querySelectorAll('[data-multiselect-border]').forEach(el => {
            el.style.border = '';
            el.removeAttribute('data-multiselect-border');
        });
    }

    // ── VALIDACIÓN SUBMIT ──
    form.addEventListener('submit', function (e) {
        limpiarErrores();
        let valido = true;
        const hoy = new Date();
        hoy.setHours(0, 0, 0, 0);

        // 1. Campos required
        form.querySelectorAll('input[required], select[required], textarea[required]').forEach(field => {
            if (!field.value.trim()) {
                mostrarError(field, getMensaje(field));
                valido = false;
            }
        });

        // 2. Fechas no mayores a hoy
        form.querySelectorAll('input[type="date"]').forEach(field => {
            if (field.value) {
                const fechaIngresada = new Date(field.value + 'T00:00:00');
                if (fechaIngresada > hoy) {
                    mostrarError(field, 'La fecha no puede ser mayor a hoy.');
                    valido = false;
                }
            }
        });

        // 3. Hora fin mayor que hora inicio
        const horaInicioFields = ['motoresInicio', 'produccionInicio', 'horaInicio'];
        const horaFinFields    = ['motoresFin',    'produccionFin',    'horaFin'];
        horaInicioFields.forEach((nombreInicio, i) => {
            const inicio = form.querySelector('[name="' + nombreInicio + '"]');
            const fin    = form.querySelector('[name="' + horaFinFields[i] + '"]');
            if (inicio && fin && inicio.value && fin.value) {
                if (fin.value <= inicio.value) {
                    mostrarError(fin, 'La hora de fin debe ser mayor que la hora de inicio.');
                    valido = false;
                }
            }
        });

        // 4. Campos de producto y operador obligatorios si existen
        const camposObligatorios = ['producto', 'operador'];
        camposObligatorios.forEach(nombre => {
            const field = form.querySelector('[name="' + nombre + '"]');
            if (field && !field.value.trim()) {
                mostrarError(field, getMensaje(field));
                valido = false;
            }
        });

        // 5. Autocomplete operador simple
        const operadorInput = document.getElementById('operadorInput');
        if (operadorInput && !operadorInput.value.trim()) {
            mostrarError(operadorInput, 'El operador es obligatorio.');
            valido = false;
        }

        // 6. Personal calidad
        const personalInput = document.getElementById('personalInput');
        if (personalInput && !personalInput.value.trim()) {
            mostrarError(personalInput, 'El personal de calidad es obligatorio.');
            valido = false;
        }

        // 7. Multiselect operadores
        const operadoresHidden = document.getElementById('operadoresHidden');
        if (operadoresHidden && !operadoresHidden.value.trim()) {
            const div = document.getElementById('operadoresSeleccionados');
            if (div) {
                div.style.border = '1px solid #dc3545';
                div.setAttribute('data-multiselect-border', '1');
                const msg = document.createElement('div');
                msg.className = 'invalid-feedback d-block';
                msg.textContent = 'Debe agregar al menos un operador.';
                div.parentNode.insertBefore(msg, div.nextSibling);
                valido = false;
            }
        }

        // 8. Multiselect envasadores
        const envasadoresHidden = document.getElementById('envasadoresHidden');
        if (envasadoresHidden && !envasadoresHidden.value.trim()) {
            const div = document.getElementById('envasadoresSeleccionados');
            if (div) {
                div.style.border = '1px solid #dc3545';
                div.setAttribute('data-multiselect-border', '1');
                const msg = document.createElement('div');
                msg.className = 'invalid-feedback d-block';
                msg.textContent = 'Debe agregar al menos un envasador.';
                div.parentNode.insertBefore(msg, div.nextSibling);
                valido = false;
            }
        }

        // 9. Al menos un saco con peso en control de pesos
        const sacoInputs = document.querySelectorAll('.saco-input');
        if (sacoInputs.length > 0) {
            let tienePeso = false;
            sacoInputs.forEach(input => {
                const val = parseFloat(input.value);
                if (!isNaN(val) && val > 0) tienePeso = true;
            });
            if (!tienePeso) {
                const grid = document.getElementById('sacoGrid');
                if (grid) {
                    const msg = document.createElement('div');
                    msg.className = 'alert alert-danger mt-2';
                    msg.style.fontSize = '13px';
                    msg.textContent = '⚠️ Debe ingresar el peso de al menos un saco.';
                    grid.parentNode.insertBefore(msg, grid.nextSibling);
                    valido = false;
                }
            }
        }

        // 10. Números negativos
        form.querySelectorAll('input[type="number"]').forEach(field => {
            const val = parseFloat(field.value);
            if (!isNaN(val) && val < 0) {
                mostrarError(field, 'El valor no puede ser negativo.');
                valido = false;
            }
        });

        if (!valido) {
            e.preventDefault();
            const primerError = document.querySelector('.is-invalid, .invalid-feedback, .alert-danger');
            if (primerError) {
                primerError.scrollIntoView({ behavior: 'smooth', block: 'center' });
            }
            return;
        }

        // 11. Confirmación antes de guardar ✨
        const btnSubmit = form.querySelector('button[type="submit"]');
        if (btnSubmit && !btnSubmit.getAttribute('data-confirmado')) {
            e.preventDefault();
            mostrarModalConfirmacion(function() {
                btnSubmit.setAttribute('data-confirmado', 'true');
                btnSubmit.click();
            });
        }
    });

    // ── MODAL DE CONFIRMACIÓN ──
    function mostrarModalConfirmacion(callback) {
        // Remover modal anterior si existe
        const modalAnterior = document.getElementById('modalConfirmGuardar');
        if (modalAnterior) modalAnterior.remove();

        const modal = document.createElement('div');
        modal.id = 'modalConfirmGuardar';
        modal.style.cssText = `
            position: fixed; top: 0; left: 0; width: 100%; height: 100%;
            background: rgba(0,0,0,0.5); z-index: 9999;
            display: flex; align-items: center; justify-content: center;
        `;
        modal.innerHTML = `
            <div style="background:white; border-radius:16px; padding:32px; max-width:400px; width:90%; text-align:center; box-shadow:0 20px 60px rgba(0,0,0,0.3);">
                <div style="font-size:48px; margin-bottom:16px;">💾</div>
                <h5 style="color:#1a2e1a; font-weight:700; margin-bottom:8px;">¿Guardar registro?</h5>
                <p style="color:#888; font-size:13px; margin-bottom:24px;">
                    Verifica que todos los datos estén correctos antes de guardar.
                </p>
                <div style="display:flex; gap:12px; justify-content:center;">
                    <button id="btnCancelarModal" style="background:#f5f0e8; color:#666; border:1.5px solid #e0d8ce; border-radius:8px; padding:10px 24px; font-size:13px; cursor:pointer; font-weight:600;">
                        Revisar
                    </button>
                    <button id="btnConfirmarModal" style="background:#2d5a27; color:white; border:none; border-radius:8px; padding:10px 24px; font-size:13px; cursor:pointer; font-weight:600;">
                        ✅ Sí, guardar
                    </button>
                </div>
            </div>
        `;

        document.body.appendChild(modal);

        document.getElementById('btnConfirmarModal').onclick = function() {
            modal.remove();
            callback();
        };

        document.getElementById('btnCancelarModal').onclick = function() {
            modal.remove();
        };

        modal.onclick = function(e) {
            if (e.target === modal) modal.remove();
        };
    }

    // ── MENSAJE HELPER ──
    function getMensaje(field) {
        const label = field.closest('[class*="col-"]')
            ?.querySelector('label')?.textContent?.trim();
        if (label) return label.replace('*', '').trim() + ' es obligatorio.';
        const name = field.getAttribute('name') || '';
        if (name.includes('fecha')) return 'La fecha es obligatoria.';
        if (name.includes('turno')) return 'Selecciona un turno.';
        if (name.includes('producto')) return 'El producto es obligatorio.';
        if (name.includes('operador')) return 'El operador es obligatorio.';
        return 'Este campo es obligatorio.';
    }
});