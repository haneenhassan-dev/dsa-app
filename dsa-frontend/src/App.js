import {BrowserRouter, Routes, Route, Navigate} from 'react-router-dom';
import { useAuth, AuthProvider } from './context/AuthContext';
import Login from './pages/Login/Login.js';
import { Problems } from './pages/Problems/ProblemList.js';
import { ProblemDetail } from './pages/Problems/ProblemDetails.js';

function ProtectedRoute({children}){
  const {isAuthenticated, loading} = useAuth();
  if(loading){
    return <div> loading.... </div>
  }
  if(!isAuthenticated){
    return <Navigate to="/login" replace />
  }

  return children;
}
function App() {
  return (<AuthProvider>
    <BrowserRouter>
    <Routes>
      <Route path = "/login" element = {<Login/>} />
      {/* <Route path= "/signup" element = {<Signup/>}/> */} 
       { <Route path= "/problems" 
         element = {
          <ProtectedRoute>
            <Problems/>
          </ProtectedRoute>
         }
      /> }
      
      <Route path= "/problems/:id" 
         element = {
          <ProtectedRoute>
            <ProblemDetail />
          </ProtectedRoute>
         }
      />   
    </Routes>  
    </BrowserRouter>
  </AuthProvider>
  );
}

export default App;
