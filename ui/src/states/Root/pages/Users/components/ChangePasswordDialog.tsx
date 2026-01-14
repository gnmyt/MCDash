import * as React from "react";
import { t } from "i18next";
import { User } from "@/types/user";
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

interface ChangePasswordDialogProps {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    user: User | null;
    onSubmit: (userId: number, password: string) => Promise<boolean>;
}

const ChangePasswordDialog = ({ open, onOpenChange, user, onSubmit }: ChangePasswordDialogProps) => {
    const [password, setPassword] = React.useState("");
    const [confirmPassword, setConfirmPassword] = React.useState("");
    const [loading, setLoading] = React.useState(false);
    const [error, setError] = React.useState("");

    const resetForm = () => {
        setPassword("");
        setConfirmPassword("");
        setError("");
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!user) return;

        setError("");

        if (password.length < 6) {
            setError(t("users.error.password_length"));
            return;
        }

        if (password !== confirmPassword) {
            setError(t("users.error.password_mismatch"));
            return;
        }

        setLoading(true);
        const success = await onSubmit(user.id, password);
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
                        <DialogTitle>{t("users.change_password")}</DialogTitle>
                        <DialogDescription>
                            {t("users.change_password_description", { username: user?.username })}
                        </DialogDescription>
                    </DialogHeader>
                    <div className="grid gap-4 py-6">
                        <div className="grid gap-2">
                            <Label htmlFor="newPassword">{t("users.new_password")}</Label>
                            <Input
                                id="newPassword"
                                type="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                placeholder={t("users.password_placeholder")}
                            />
                        </div>
                        <div className="grid gap-2">
                            <Label htmlFor="confirmNewPassword">{t("users.confirm_password")}</Label>
                            <Input
                                id="confirmNewPassword"
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
                            {t("action.save")}
                        </Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    );
};

export default ChangePasswordDialog;
