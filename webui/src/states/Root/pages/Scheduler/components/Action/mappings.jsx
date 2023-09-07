import {Campaign, Group, PowerSettingsNew, Replay, Save, Terminal} from "@mui/icons-material";
import {t} from "i18next";

export default () => ({
    "1": {
        icon: <Terminal />,
        name: t("schedules.types.command"),
    },
    "2": {
        icon: <Campaign />,
        name: t("schedules.types.broadcast"),
    },
    "3": {
        icon: <Replay />,
        name: t("schedules.types.reload"),
    },
    "4": {
        icon: <PowerSettingsNew />,
        name: t("schedules.types.stop"),
    },
    "5": {
        icon: <Save />,
        name: t("schedules.types.backup"),
    },
    "6": {
        icon: <Group />,
        name: t("schedules.types.kick"),
    }
});