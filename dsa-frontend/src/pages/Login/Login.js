import {useState} from 'react';
import {useAuth} from '../../context/AuthContext.js';
import styles from './Login.module.css';
import { useNavigate } from 'react-router-dom';

export default function Login(){
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [isloading, setLoading] = useState(false);
    const navigate = useNavigate();
    const {login} = useAuth();

    async function handleClick(e){
        console.log("clicked");
        e.preventDefault();
        
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
        <div className ={styles.container}>
            <form onSubmit={handleClick} className={styles.form}>
                <h2> Login to Dsa App</h2>
                <input value={username} 
                onChange={(e)=> setUsername(e.target.value)}
                placeholder="username"
                />
                <input value={password} 
                onChange={(e)=> setPassword(e.target.value)}
                placeholder="password"
                type="password"
                />
                <button type="submit" disabled={isloading}>
                    {isloading ? "Login..." : "Log in"}
                </button>
            </form>
        </div>
    )
}