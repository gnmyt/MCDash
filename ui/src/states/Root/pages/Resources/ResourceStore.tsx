import { useParams } from "react-router-dom";
import { StorefrontIcon } from "@phosphor-icons/react";
import { t } from "i18next";
import { Badge } from "@/components/ui/badge";

export const ResourceStore = () => {
    const { type } = useParams<{ type: string }>();

    const getTypeLabel = () => {
        return t(`resources.types.${type}`, type?.charAt(0).toUpperCase() + type?.slice(1));
    };

    return (
        <div className="flex flex-col p-6 pt-0 gap-6" style={{ height: 'calc(100vh - 5.5rem)' }}>
            <div className="flex items-center justify-between p-4 rounded-xl border bg-card shrink-0">
                <div className="flex items-center gap-4">
                    <div className="h-12 w-12 rounded-xl bg-primary/10 flex items-center justify-center">
                        <StorefrontIcon className="h-6 w-6 text-primary" weight="fill" />
                    </div>
                    <div className="flex items-center gap-3">
                        <h1 className="text-lg font-semibold">
                            {getTypeLabel()} {t("resources.store")}
                        </h1>
                        <Badge variant="secondary" className="text-xs">
                            {t("resources.wip")}
                        </Badge>
                    </div>
                </div>
            </div>

            <div className="flex-1 flex flex-col items-center justify-center">
                <div className="h-24 w-24 rounded-2xl bg-muted flex items-center justify-center mb-6">
                    <StorefrontIcon className="h-12 w-12 text-muted-foreground" />
                </div>
                <h2 className="text-2xl font-semibold mb-2">{t("resources.store_coming_soon")}</h2>
                <p className="text-muted-foreground text-center max-w-md">
                    {t("resources.store_coming_soon_description")}
                </p>
            </div>
        </div>
    );
};