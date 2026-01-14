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

interface EditUsernameDialogProps {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    user: User | null;
    onSubmit: (userId: number, username: string) => Promise<boolean>;
}

const EditUsernameDialog = ({ open, onOpenChange, user, onSubmit }: EditUsernameDialogProps) => {
    const [username, setUsername] = React.useState("");
    const [loading, setLoading] = React.useState(false);
    const [error, setError] = React.useState("");

    React.useEffect(() => {
        if (user) {
            setUsername(user.username);
        }
    }, [user]);

    const resetForm = () => {
        setUsername(user?.username || "");
        setError("");
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!user) return;

        setError("");

        if (username.length < 3 || username.length > 32) {
            setError(t("users.error.username_length"));
            return;
        }

        setLoading(true);
        const success = await onSubmit(user.id, username);
        setLoading(false);

        if (success) {
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
                        <DialogTitle>{t("users.edit_username")}</DialogTitle>
                        <DialogDescription>
                            {t("users.edit_username_description")}
                        </DialogDescription>
                    </DialogHeader>
                    <div className="grid gap-4 py-6">
                        <div className="grid gap-2">
                            <Label htmlFor="editUsername">{t("users.username")}</Label>
                            <Input
                                id="editUsername"
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                placeholder={t("users.username_placeholder")}
                                autoComplete="off"
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
                        <Button type="submit" disabled={loading || username === user?.username}>
                            {t("action.save")}
                        </Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    );
};

export default EditUsernameDialog;
