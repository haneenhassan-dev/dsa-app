import axios from 'axios';

const api = axios.create({
    baseURL : 'http://localhost:8080/',
    headers : {
        'Content-Type' : 'application/json'
    }
});

const interceptId = api.interceptors.request.use(
    (config)=>{
        const token = localStorage.getItem('token');
        if(token){
            config.headers.Authorization = `Bearer ${token}`;

        }
        return config;
    },
    (error)=>{
        return Promise.reject(error);
    }

)

api.interceptors.response.use(
    (response)=>{
        return response;
    },
    (error)=>{
        if(error.response && error.response.status==401){
            if (!error.config.url.includes('/auth/')) {
                localStorage.removeItem('token');
                localStorage.removeItem('user');
                window.location.href = '/login';
            }
        }

        return Promise.reject(error);
    }
)
export const authAPI = {
    register : (username, password, email) =>{
        return api.post('/auth/register', {username, password, email});
    },
    login: (username, password)=>{
        return api.post('/auth/login', {username, password});
    }
}

export const problemsAPI = {
    getAllProblems : ()=>{
        return api.get('/problems');
    },
    getById : (id)=>{
        return api.get(`/problems/${id}`);
    }
}

export const submitCodeAPI = ((problemId,code,language)=>{
    return api.post('/submissions',{problemId,code,language});
})

export const draftAPI = {
    getDraft : (problemId,language)=>{
        return api.get(`/draft?problemId=${problemId}&language=${language}`);
    },
    saveDraft : (problemId, code, language)=>{
        return api.post('/draft',{problemId,code,language});
    }
}

export const getSubmissionsAPI = (id)=>{
    return api.get(`/submissions?problemId=${id}`);
}

export default api;