import {t} from "i18next";
import {getDays} from "@/states/Root/pages/Scheduler/components/CreateScheduleDialog/utils.js";

export const convertFrequency = (frequency, time) => {
    if (frequency === "MONTHLY")
        return t("schedules.schedule.monthly", {day: parseInt(time)});

    if (frequency === "WEEKLY")
        return t("schedules.schedule.weekly", {days: getDays()[parseInt(time)-1]});

    if (frequency === "DAILY")
        return t("schedules.schedule.daily", {time: time.replace(/(.{2})/, "$1:")});

    if (frequency === "HOURLY")
        return t("schedules.schedule.hourly", {minutes: parseInt(time)});
}