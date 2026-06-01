import { useState, useEffect, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { problemsAPI, submitCodeAPI, draftAPI, getSubmissionsAPI} from '../../services/api.js';
import ReactMarkdown from 'react-markdown';
import Editor from '@monaco-editor/react';
import styles from './ProblemDetail.module.css';

export function ProblemDetail() {
    const { id } = useParams();
    const navigate = useNavigate();
    const [problem, setProblem] = useState(null);
    const [code, setCode] = useState('// Write your solution here\n');
    const [language, setLanguage] = useState('java');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [isSolved,setIsSolved] = useState(false);
    const [result, setResult] = useState(null);
    const [submitting, setSubmitting] = useState(false);

    const codeRef = useRef(code);
    const languageRef = useRef(language);
    const hasEdited = useRef(false);


    useEffect(()=>{
        codeRef.current = code;
    },[code]);
    useEffect(()=>{
        languageRef.current = language;
    },[language]);

    useEffect(()=>{
        
        return ()=>{
            if (hasEdited.current) {
                draftAPI.saveDraft(id, codeRef.current, languageRef.current);
            }
        }
    },[id]);

    useEffect(() => {
        // Create a function that runs when tab is about to close
        const handleBeforeUnload = () => {
            // sendBeacon sends data to your backend even during tab close
            // Regular axios/fetch would get cancelled, sendBeacon won't
            navigator.sendBeacon('http://localhost:8080/draft', JSON.stringify({
                problemId: id,
                code: codeRef.current,
                language: languageRef.current
            }));
        };
    
        // Register: "browser, call my function before closing"
        window.addEventListener('beforeunload', handleBeforeUnload);
    
        // Cleanup: remove the listener when component unmounts normally
        // Without this, the listener stays even after navigating away
        return () => window.removeEventListener('beforeunload', handleBeforeUnload);
    }, [id]);

    useEffect(() => {
        async function fetchProblem() {
            try {
                const response = await problemsAPI.getById(id);
                setProblem(response.data);
                //draftloading
                // const draftResponse = await draftAPI.getDraft(id, language);
                // console.log(draftResponse.data.code);
                // if(draftResponse.data.code && draftResponse.data.code != "No draft found"){
                //     setCode(draftResponse.data.code);
                // }

                const submissionResponse = await getSubmissionsAPI(id);
                const submissions = submissionResponse.data;
                console.log("type:", typeof response.data.submissions);
                console.log("isArray:", Array.isArray(response.data.submissions));
                setIsSolved(submissions.some(s=> s.status=='ACCEPTED'));
                const localDraft = localStorage.getItem(`draft_${id}_${language}`);
                const localTime = Number(localStorage.getItem(`draft_${id}_${language}_time`));
                const draftResponse = await draftAPI.getDraft(id, language);
                if(draftResponse.data.code && draftResponse.data.code != "No draft found"){
                    console.log("Key looking for:", `draft_${id}_${language}`);
                    console.log("LocalStorage value:", localStorage.getItem(`draft_${id}_${language}`));
                    const dbTime = new Date(draftResponse.data.updatedAt).getTime();
                    console.log(dbTime);
                    console.log(localTime);
                    if(localDraft && localTime && new Date(localTime) > new Date (dbTime) ){
                        setCode(localDraft);
                        draftAPI.saveDraft(id, localDraft, language);
                    }
                    else{
                        setCode(draftResponse.data.code);
                    }
                }
                else if(localDraft){
                    setCode(localDraft);
                    console.log("Key looking for:", `draft_${id}_${language}`);
                    console.log("LocalStorage value:", localStorage.getItem(`draft_${id}_${language}`));
                    draftAPI.saveDraft(id, localDraft, language);
                }
                else{
                    setCode("// Write your solution here\n");
                    localStorage.setItem(`draft_${id}_${language}`,"// Write your solution here\n");
                    localStorage.setItem(`draft_${id}_${language}_time`,Date.now().toString());
                }

                

            } catch (err) {
                setError('Failed to load problem.');
            } finally {
                setLoading(false);
            }
        }
        fetchProblem();
    }, [id,language]);

    async function handleSubmit() {
        setSubmitting(true);
        setResult(null);
        try {
            const response = await submitCodeAPI(problem.problemId, code, language);
            setResult(response.data.status);
            if (response.data.status === 'ACCEPTED') {
                setIsSolved(true);
            }
        } catch (err) {
            setResult('ERROR');
        } finally {
            setSubmitting(false);
        }
    }

    if (loading) {
        return (
            <div className={styles.container}>
                <div className={styles.loading}>Loading problem...</div>
            </div>
        );
    }

    if (error) {
        return (
            <div className={styles.container}>
                <div className={styles.error}>{error}</div>
            </div>
        );
    }

    return (
        <div className={styles.container}>
            <div className={styles.header}>
                <button className={styles.backBtn} onClick={() => navigate('/problems')}>
                    Back to Problems
                </button>
                <div className={styles.problemTitle}>
                {problem.title}
                {isSolved && <span className={styles.solvedBadge}>Solved </span>}
                </div>
                <span className={`${styles.badge} ${styles[problem.difficulty.toLowerCase()]}`}>
                    {problem.difficulty}
                </span>
            </div>

            <div className={styles.content}>
                <div className={styles.descriptionPanel}>
                    <ReactMarkdown>{problem.description}</ReactMarkdown>
                </div>

                <div className={styles.editorPanel}>
                    <div className={styles.editorHeader}>
                        <select
                            value={language}
                            onChange={
                                (e) => {
                                    setLanguage(e.target.value);
                            }}
                            className={styles.languageSelect}
                        >
                            <option value="java">Java</option>
                            <option value="python">Python</option>
                            <option value="javascript">JavaScript</option>
                            <option value="cpp">C++</option>
                        </select>
                        <button className={styles.submitBtn} onClick={handleSubmit} disabled={submitting}>
                            {submitting ? 'Submitting...' : 'Submit'}
                        </button>
                    </div>

                    <Editor
                        height="calc(100vh - 200px)"
                        language={language}
                        value={code}
                        onChange={(value) => {
                            setCode(value);
                            hasEdited.current = true;
                            localStorage.setItem(`draft_${id}_${language}`,value);
                            localStorage.setItem(`draft_${id}_${language}_time`,Date.now().toString());
                        }}
                        theme="vs-dark"
                        options={{
                            fontSize: 14,
                            minimap: { enabled: false },
                            scrollBeyondLastLine: false,
                            padding: { top: 16 },
                        }}
                    />
                    {result && (
                    <div className={`${styles.resultBar} ${styles[result.toLowerCase()]}`}>
                        {result === 'ACCEPTED' && 'Accepted — Nice work!'}
                        {result === 'WRONG_ANSWER' && 'Wrong Answer — Try again'}
                        {result === 'COMPILATION_ERROR' && 'Compilation Error'}
                        {result === 'RUNTIME_ERROR' && 'Runtime Error'}
                        {result === 'TIME_LIMIT_EXCEEDED' && 'Time Limit Exceeded'}
                        {result === 'ERROR' && 'Submission Failed — Server Error'}
                    </div>
                    )}
                </div>
            </div>
        </div>
    );
}