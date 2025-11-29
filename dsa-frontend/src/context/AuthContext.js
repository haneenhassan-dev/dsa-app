import { useState, useContext, createContext, useEffect } from "react";
import {authAPI} from "../services/api.js";

const AuthContext = createContext();

export const AuthProvider = ({children})=>{

    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [user, setUser] = useState(null);
    const [loading, setLoading]= useState(true);

    useEffect(()=>{
        const token = localStorage.getItem('token');
        const savedUser = localStorage.getItem('user');

        if(token && user){
            setIsAuthenticated(true);
            setUser(JSON.parse(savedUser));
        }
        setLoading(false);
    },[]);

    async function login(username, pasword){
        try{
            const  response = await authAPI.login(username,password);
        localStorage.setItem('token',response.data.token);
        localStorage.setItem('user',username);
        setIsAuthenticated(true);
        setUser(username);

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
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        setIsAuthenticated(false);
        setUser(null);
    }

    return (
        <AuthContext.Provider value={{isAuthenticated, user, login, logout, loading}} >
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
