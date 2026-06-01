import {useEffect, useState} from 'react';
import {useAuth} from '../../context/AuthContext.js';
import { problemsAPI,getSubmissionsAPI } from '../../services/api.js';
import styles from './ProblemList.module.css';
import { useNavigate } from 'react-router-dom';


export function Problems(){
    const [problems, setProblems] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const {user, logout} = useAuth();
    const navigate = useNavigate();
    // const [isSolved,setIsSolved] = useState(false);

    useEffect(()=>{
        async function fetchProblems() {
            try {
                const response = await problemsAPI.getAllProblems();
                setProblems(response.data);
                // problems = [];
                // for (const key in response) {
                //     const submissionResponse = await getSubmissionsAPI(key.id);
                //     if(submissions.some(s=> s.status=='ACCEPTED')){
                        
                //     }
                // }
                
                // const submissions = submissionResponse.data;
                // setIsSolved(submissions.some(s=> s.status=='ACCEPTED'));

            } catch (err) {
                setError('Failed to load problems. Please try again.');
            } finally {
                setLoading(false);
            }
        }
        fetchProblems();
    },[]);

    function getDifficultyClass(difficulty) {
        if (difficulty === 'EASY') return styles.easy;
        if (difficulty === 'MEDIUM') return styles.medium;
        if (difficulty === 'HARD') return styles.hard;
        return '';
    }

    if (loading) {
        return (
            <div className={styles.container}>
                <div className={styles.loading}>Loading problems...</div>
            </div>
        );
    }

    return (
        <div className={styles.container}>
            <div className={styles.header}>
                <div>
                    <div className={styles.title}>Problems</div>
                    <div className={styles.subtitle}>
                        {problems.length} problems available
                    </div>
                </div>
                <button className={styles.logoutBtn} onClick={logout}>
                    Log out
                </button>
            </div>

            {error && <div className={styles.error}>{error}</div>}

            {problems.length === 0 && !error ? (
                <div className={styles.empty}>No problems found.</div>
            ) : (
                <div className={styles.grid}>
                    {problems.map(p => (
                        <div key={p.problemId} className={styles.card} onClick={()=> navigate(`/problems/${p.problemId}`)}>
                            <div className={styles.cardLeft}>
                                <span className={styles.problemNumber}>
                                    {p.problemId}
                                </span>
                                <span className={styles.problemTitle}>
                                    {p.title}
                                </span>
                                {/* {isSolved && <span className={styles.solvedBadge}>Solved </span>} */}
                            </div>
                            <div className={styles.cardRight}>
                                <span className={`${styles.badge} ${getDifficultyClass(p.difficulty)}`}>
                                    {p.difficulty}
                                </span>
                                <span className={styles.score}>
                                    {p.score} pts
                                </span>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}
