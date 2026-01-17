import {useState} from "react";
import {t} from "i18next";
import {KeyIcon, CircleNotchIcon} from "@phosphor-icons/react";

import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog";
import {Button} from "@/components/ui/button";
import {Input} from "@/components/ui/input";
import {Label} from "@/components/ui/label";
import {toast} from "@/hooks/use-toast";
import {setProviderApiKey} from "@/lib/StoreApi";

interface ApiKeyDialogProps {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    providerId: string;
    providerName: string;
    isConfigured: boolean;
    onSuccess: () => void;
}

export const ApiKeyDialog = ({
    open,
    onOpenChange,
    providerId,
    providerName,
    isConfigured,
    onSuccess
}: ApiKeyDialogProps) => {
    const [apiKey, setApiKey] = useState("");
    const [saving, setSaving] = useState(false);

    const handleSave = async () => {
        if (!apiKey.trim()) {
            toast({
                description: t("store.api_key_required"),
                variant: "destructive"
            });
            return;
        }

        setSaving(true);
        try {
            const result = await setProviderApiKey(providerId, apiKey.trim());
            if (result.success) {
                toast({description: t("store.api_key_saved")});
                setApiKey("");
                onOpenChange(false);
                onSuccess();
            } else {
                toast({
                    description: result.error || t("store.api_key_save_failed"),
                    variant: "destructive"
                });
            }
        } catch (error) {
            console.error("Failed to save API key:", error);
            toast({
                description: t("store.api_key_save_failed"),
                variant: "destructive"
            });
        } finally {
            setSaving(false);
        }
    };

    const getHelpUrl = () => {
        switch (providerId) {
            case "curseforge":
                return "https://console.curseforge.com/";
            default:
                return null;
        }
    };

    const helpUrl = getHelpUrl();

    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent className="max-w-md">
                <DialogHeader>
                    <DialogTitle className="flex items-center gap-2">
                        <KeyIcon className="h-5 w-5"/>
                        {t("store.configure_api_key", {provider: providerName})}
                    </DialogTitle>
                    <DialogDescription>
                        {t("store.api_key_description", {provider: providerName})}
                        {helpUrl && (
                            <>
                                {" "}
                                <a
                                    href={helpUrl}
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    className="text-primary hover:underline"
                                >
                                    {t("store.get_api_key")}
                                </a>
                            </>
                        )}
                    </DialogDescription>
                </DialogHeader>

                <div className="space-y-4 py-4">
                    <div className="space-y-2">
                        <Label htmlFor="apiKey">
                            {isConfigured ? t("store.new_api_key") : t("store.api_key")}
                        </Label>
                        <Input
                            id="apiKey"
                            type="password"
                            placeholder={t("store.api_key_placeholder")}
                            value={apiKey}
                            onChange={(e) => setApiKey(e.target.value)}
                            onKeyDown={(e) => {
                                if (e.key === "Enter" && !saving) {
                                    handleSave();
                                }
                            }}
                        />
                        {isConfigured && (
                            <p className="text-xs text-muted-foreground">
                                {t("store.api_key_change_hint")}
                            </p>
                        )}
                    </div>
                </div>

                <DialogFooter>
                    <Button variant="outline" onClick={() => onOpenChange(false)}>
                        {t("action.cancel")}
                    </Button>
                    <Button onClick={handleSave} disabled={saving || !apiKey.trim()}>
                        {saving ? (
                            <>
                                <CircleNotchIcon className="h-4 w-4 animate-spin mr-2"/>
                                {t("action.saving")}
                            </>
                        ) : (
                            t("action.save")
                        )}
                    </Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    );
};
