import {Cuboid} from "lucide-react";
import MinecraftBanner from "@/assets/images/minecraft-banner.png";

import LoginForm from "@/states/Login/components/LoginForm.tsx";
import {useState} from "react";

const Login = () => {

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    return (
        <div className="grid min-h-screen lg:grid-cols-2">
            <div className="flex flex-col gap-4 p-6 md:p-10">
                <div className="flex justify-center gap-2 md:justify-start">
                    <a href="#" className="flex items-center gap-2 font-medium">
                        <div
                            className="flex h-6 w-6 items-center justify-center rounded-md bg-primary text-primary-foreground">
                            <Cuboid className="size-4"/>
                        </div>
                        MCDash
                    </a>
                </div>
                <div className="flex flex-1 items-center justify-center">
                    <div className="w-full max-w-xs">
                        <LoginForm username={username} setUsername={setUsername} password={password} setPassword={setPassword}/>
                    </div>
                </div>
            </div>

            <div className="relative hidden lg:flex items-center justify-center p-8">
                <div className="relative w-4/5 h-4/5 max-w-lg max-h-lg">
                    <img
                        src={MinecraftBanner}
                        alt="Image"
                        className="h-full w-full object-contain rounded-lg shadow-lg"
                    />
                </div>
            </div>
        </div>
    )
}

export default Login;