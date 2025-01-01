import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogHeader,
    DialogTitle,
    DialogTrigger
} from "@/components/ui/dialog.js";
import {t} from "i18next";

const PasswordDialog = () => {
    return (
        <Dialog>
            <DialogTrigger>
                <a className="ml-auto text-sm underline-offset-4 hover:underline">
                    {t("login.forgot_password")}
                </a>
            </DialogTrigger>
            <DialogContent>
                <DialogHeader>
                    <DialogTitle>{t("login.forgot_password")}</DialogTitle>
                    <DialogDescription>
                        {t("login.forgot_password_description")}
                    </DialogDescription>
                </DialogHeader>
            </DialogContent>
        </Dialog>
    )
}

export default PasswordDialog;