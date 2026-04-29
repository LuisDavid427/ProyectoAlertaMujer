// src/components/ErrorModal.jsx

export default function ErrorModal({ isOpen, mensaje, onClose }) {
    // Si isOpen es falso, no renderiza absolutamente nada (Bajo acoplamiento perfecto)
    if (!isOpen) return null;

    // Estilos en línea básicos para la capa oscura 
    const capaOscuraStyle = {
        position: 'fixed',
        top: 0, left: 0, width: '100%', height: '100%',
        backgroundColor: 'rgba(0,0,0,0.6)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        zIndex: 9999
    };

    const modalStyle = {
        backgroundColor: 'white',
        padding: '30px',
        borderRadius: '12px',
        maxWidth: '400px',
        width: '90%',
        textAlign: 'center',
        boxShadow: '0 4px 15px rgba(0,0,0,0.2)'
    };

    return (
        <div style={capaOscuraStyle} className="capa-oscura">
            <div style={modalStyle} className="pantalla-error">
                <h3 style={{ color: 'var(--primary-color)', marginBottom: '15px' }}>
                    ⚠️ Atención
                </h3>
                
                <p id="texto-error" style={{ marginBottom: '25px', color: 'var(--text-main)' }}>
                    {mensaje}
                </p>
                
                <button 
                    onClick={onClose} 
                    className="btn-primary" 
                    style={{ width: '100%', border: 'none', cursor: 'pointer' }}
                >
                    ENTENDIDO
                </button>
            </div>
        </div>
    );
}