import {useState} from 'react';
import { authAPI } from '../../services/api.js';
import { useNavigate, Link } from 'react-router-dom';
import styles from './Signup.module.css';

export default function Signup(){
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [email, setEmail] = useState('');
    const [error, setError] = useState('');
    const [isloading, setLoading] = useState(false);
    const navigate = useNavigate();

    async function handleSubmit(e){
        e.preventDefault();
        setError('');

        if(!username || !password || !email){
            setError('Please fill in all fields');
            return;
        }

        setLoading(true);
        try {
            await authAPI.register(username, password, email);
            navigate('/login');
        } catch(err) {
            setError(err.response?.data.message || 'Registration failed. Please try again.');
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className={styles.container}>
            <form onSubmit={handleSubmit} className={styles.form}>
                <h2>Sign up for Diyesay</h2>
                {error && <div className={styles.error}>{error}</div>}
                <input value={username}
                onChange={(e)=> setUsername(e.target.value)}
                placeholder="Username"
                />
                <input value={email}
                onChange={(e)=> setEmail(e.target.value)}
                placeholder="Email"
                type="email"
                />
                <input value={password}
                onChange={(e)=> setPassword(e.target.value)}
                placeholder="Password"
                type="password"
                />
                <button type="submit" disabled={isloading}>
                    {isloading ? "Creating account..." : "Sign up"}
                </button>
                <div className={styles.loginLink}>
                    Already have an account? <Link to="/login">Log in</Link>
                </div>
            </form>
        </div>
    )
}
