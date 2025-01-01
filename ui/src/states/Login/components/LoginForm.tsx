import { Label } from "@/components/ui/label.js";
import { Input } from "@/components/ui/input.js";
import { Button } from "@/components/ui/button.js";

import PasswordDialog from "@/states/Login/components/PasswordDialog.tsx";
import React from "react";
import {t} from "i18next";

interface LoginFormProps {
    username: string;
    setUsername: (value: string) => void;
    password: string;
    setPassword: (value: string) => void;
}

const LoginForm: React.FC<LoginFormProps> = ({ username, setUsername, password, setPassword }) => {
    return (
        <form className="flex flex-col gap-6" action="javascript:void(0)">
            <div className="flex flex-col items-center gap-2 text-center">
                <h1 className="text-2xl font-bold">{t("login.sign_in")}</h1>
            </div>
            <div className="grid gap-6">
                <div className="grid gap-2">
                    <Label htmlFor="username">{t("login.name")}</Label>
                    <Input
                        id="username"
                        type="text"
                        placeholder={t("login.name")}
                        required
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                    />
                </div>
                <div className="grid gap-2">
                    <div className="flex items-center justify-between">
                        <Label htmlFor="password">{t("login.password")}</Label>
                        <PasswordDialog />
                    </div>
                    <Input
                        id="password"
                        type="password"
                        placeholder={t("login.password")}
                        required
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                </div>
                <Button type="submit" className="w-full">
                    {t("login.sign_in")}
                </Button>
            </div>
        </form>
    );
};

export default LoginForm;