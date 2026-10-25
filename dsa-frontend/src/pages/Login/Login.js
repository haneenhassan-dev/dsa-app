import {useState} from 'react';
import {useAuth} from '../../context/AuthContext.js';
import styles from './Login.module.css';
import { useNavigate, Link } from 'react-router-dom';

export default function Login(){
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [isloading, setLoading] = useState(false);
    const navigate = useNavigate();
    const {login} = useAuth();

    async function handleClick(e){
        e.preventDefault();
        setError('');

        if(!username || !password){
            setError('Please enter both username and password');
            return;
        }

        // Clear any expired token before login attempt
        localStorage.removeItem('token');
        localStorage.removeItem('user');

        setLoading(true);
        const result = await login(username,password);
        setLoading(false);
        if(result.success){
            navigate('/problems');
        }
        else{
            setError(result.message);
        }
    }

    return (
        <div className={styles.container}>
            <form onSubmit={handleClick} className={styles.form}>
                <h2>Login to Diyesay</h2>
                {error.message && <div className={styles.error}>{error.message}</div>}
                <input value={username}
                onChange={(e)=> setUsername(e.target.value)}
                placeholder="Username"
                />
                <input value={password}
                onChange={(e)=> setPassword(e.target.value)}
                placeholder="Password"
                type="password"
                />
                <button type="submit" disabled={isloading}>
                    {isloading ? "Logging in..." : "Log in"}
                </button>
                <div className={styles.signupLink}>
                    Don't have an account? <Link to="/signup">Sign up</Link>
                </div>
            </form>
        </div>
    )
}