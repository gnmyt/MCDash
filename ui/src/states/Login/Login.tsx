import Logo from "@/assets/images/logo.png";

import LoginForm from "@/states/Login/components/LoginForm.tsx";
import {FormEvent, useContext, useState} from "react";
import {ServerInfoContext} from "@/contexts/ServerInfoContext.tsx";
import {postRequest} from "@/lib/RequestUtil.ts";
import {Navigate} from "react-router-dom";
import {toast} from "@/hooks/use-toast.ts";
import {t} from "i18next";

const Login = () => {

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    const {checkToken, tokenValid} = useContext(ServerInfoContext)!;

    const login = (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        postRequest("session/create", {username, password}).then(async (r) => {
            if (!r.session) throw new Error("Invalid credentials");
            localStorage.setItem("sessionToken", r.session);
            await checkToken();
        }).catch(() => {
            toast({description: t("login.failed"), variant: "destructive"});
        });
    }

    return (
        <div className="grid min-h-screen lg:grid-cols-2">
            {tokenValid && <Navigate to="/"/>}

            <div className="flex flex-col gap-4 p-6 md:p-10">
                <div className="flex flex-1 items-center justify-center">
                    <div className="w-full max-w-xs">
                        <LoginForm username={username} setUsername={setUsername} password={password}
                                   setPassword={setPassword}
                                   login={login}/>
                    </div>
                </div>
            </div>

            <div className="relative hidden lg:flex items-center justify-center p-8">
                <div className="relative w-4/5 h-4/5 max-w-lg max-h-lg">
                    <img
                        src={Logo}
                        alt="Image"
                        className="h-full w-full object-contain rounded-lg"
                    />
                </div>
            </div>
        </div>
    )
}

export default Login;