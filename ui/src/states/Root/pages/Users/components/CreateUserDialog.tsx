import * as React from "react";
import { t } from "i18next";
import { Button } from "@/components/ui/button";
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";

interface CreateUserDialogProps {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    onSubmit: (username: string, password: string) => Promise<boolean>;
}

const CreateUserDialog = ({ open, onOpenChange, onSubmit }: CreateUserDialogProps) => {
    const [username, setUsername] = React.useState("");
    const [password, setPassword] = React.useState("");
    const [confirmPassword, setConfirmPassword] = React.useState("");
    const [loading, setLoading] = React.useState(false);
    const [error, setError] = React.useState("");

    const resetForm = () => {
        setUsername("");
        setPassword("");
        setConfirmPassword("");
        setError("");
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError("");

        if (username.length < 3 || username.length > 32) {
            setError(t("users.error.username_length"));
            return;
        }

        if (password.length < 6) {
            setError(t("users.error.password_length"));
            return;
        }

        if (password !== confirmPassword) {
            setError(t("users.error.password_mismatch"));
            return;
        }

        setLoading(true);
        const success = await onSubmit(username, password);
        setLoading(false);

        if (success) {
            resetForm();
            onOpenChange(false);
        }
    };

    return (
        <Dialog open={open} onOpenChange={(value) => {
            if (!value) resetForm();
            onOpenChange(value);
        }}>
            <DialogContent className="sm:max-w-[425px] rounded-xl">
                <form onSubmit={handleSubmit}>
                    <DialogHeader>
                        <DialogTitle>{t("users.create")}</DialogTitle>
                        <DialogDescription>
                            {t("users.create_description")}
                        </DialogDescription>
                    </DialogHeader>
                    <div className="grid gap-4 py-6">
                        <div className="grid gap-2">
                            <Label htmlFor="username">{t("users.username")}</Label>
                            <Input
                                id="username"
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                placeholder={t("users.username_placeholder")}
                                autoComplete="off"
                            />
                        </div>
                        <div className="grid gap-2">
                            <Label htmlFor="password">{t("users.password")}</Label>
                            <Input
                                id="password"
                                type="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                placeholder={t("users.password_placeholder")}
                            />
                        </div>
                        <div className="grid gap-2">
                            <Label htmlFor="confirmPassword">{t("users.confirm_password")}</Label>
                            <Input
                                id="confirmPassword"
                                type="password"
                                value={confirmPassword}
                                onChange={(e) => setConfirmPassword(e.target.value)}
                                placeholder={t("users.confirm_password_placeholder")}
                            />
                        </div>
                        {error && (
                            <p className="text-sm text-destructive">{error}</p>
                        )}
                    </div>
                    <DialogFooter>
                        <Button
                            type="button"
                            variant="outline"
                            onClick={() => onOpenChange(false)}
                        >
                            {t("action.cancel")}
                        </Button>
                        <Button type="submit" disabled={loading}>
                            {t("action.create")}
                        </Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    );
};

export default CreateUserDialog;
