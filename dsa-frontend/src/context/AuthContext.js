import { useState, useContext, createContext, useEffect } from "react";
import {authAPI} from "../services/api.js";

const AuthContext = createContext();

function decodeUserId(token) {
    try { return JSON.parse(atob(token.split(".")[1])).userId; }
    catch { return null; }
  }

export const AuthProvider = ({children})=>{

    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [user, setUser] = useState(null);
    const [loading, setLoading]= useState(true);
    const [userId, setUserId] = useState(null);

    useEffect(()=>{
        const token = localStorage.getItem('token');
        const savedUser = localStorage.getItem('user');

        if(token && savedUser){
            setIsAuthenticated(true);
            setUser(savedUser);
            setUserId(decodeUserId(token));
        }
        setLoading(false);
    },[]);


    async function login(username, password){
        try{
        const  response = await authAPI.login(username,password);
        localStorage.setItem('token',response.data.token);
        localStorage.setItem('user',username);
        setIsAuthenticated(true);
        setUser(username);
        setUserId(decodeUserId(response.data.token));

        return {success : true};
        }

        catch(error){
            return {
                success : false ,
                message : error.response?.data || "Login Failed"
            };
        }
    }

    async function logout (){
        const uid = userId;
        const prefix = `draft_${uid}_`;
        Object.keys(localStorage)
          .filter(k => k.startsWith(prefix))
          .forEach(k => localStorage.removeItem(k));
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        setIsAuthenticated(false);
        setUser(null);
    }

    return (
        <AuthContext.Provider value={{isAuthenticated, user, login, logout, loading, userId}} >
            {children}
        </AuthContext.Provider>
    )
}

export const useAuth = (()=>{
    const context = useContext(AuthContext);
    if(!context){
        throw new Error("use conext only inside the Auth provider");
    }
    return context;
})
