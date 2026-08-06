import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.tsx';
import './RegisterPage.css';

function RegisterPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirm, setConfirm] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const { register } = useAuth();
  const navigate = useNavigate();

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError('');

    if (password.length < 8) {
      setError('La contraseña debe tener al menos 8 caracteres');
      return;
    }
    if (password !== confirm) {
      setError('Las contraseñas no coinciden');
      return;
    }

    setLoading(true);
    try {
      await register({ email, password });
      navigate('/');
    } catch {
      setError('No se ha podido completar el registro. Revisa los datos e inténtalo de nuevo.');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="register-container">
      <div className="register-card">
        <div className="register-logo">
          <div className="register-logo-mark">O</div>
          <h1>Oikos</h1>
        </div>
        <p className="register-subtitle">Crea tu cuenta</p>

        <form onSubmit={handleSubmit}>
          {error && <div className="register-error">{error}</div>}

          <div className="register-field">
            <label htmlFor="email">Email</label>
            <input
              id="email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="tu@email.com"
              required
            />
          </div>

          <div className="register-field">
            <label htmlFor="password">Contraseña</label>
            <input
              id="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Mínimo 8 caracteres"
              required
            />
          </div>

          <div className="register-field">
            <label htmlFor="confirm">Repetir contraseña</label>
            <input
              id="confirm"
              type="password"
              value={confirm}
              onChange={(e) => setConfirm(e.target.value)}
              placeholder="••••••••"
              required
            />
          </div>

          <button type="submit" className="register-button" disabled={loading}>
            {loading ? 'Creando cuenta...' : 'Crear cuenta'}
          </button>
        </form>

        <p className="register-footer">
          ¿Ya tienes cuenta? <Link to="/login">Inicia sesión</Link>
        </p>
        <div className="register-disclaimer">
          <strong>Proyecto de demostración.</strong> Usa solo datos de prueba, no información real.
          Las cuentas se eliminan periódicamente.
        </div>
      </div>
    </div>
  );
}

export default RegisterPage;