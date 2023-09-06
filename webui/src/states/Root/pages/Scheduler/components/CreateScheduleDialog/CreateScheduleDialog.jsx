import {Button, Dialog, DialogActions, DialogContent, DialogTitle, MenuItem, Select, Stack, TextField} from "@mui/material";
import {t} from "i18next";
import {putRequest} from "@/common/utils/RequestUtil.js";
import {useContext, useEffect, useState} from "react";
import {SchedulesContext} from "@/states/Root/pages/Scheduler/contexts/Schedules";

export const CreateScheduleDialog = ({open, setOpen}) => {
    const days = () => [t("schedules.weekly.monday"), t("schedules.weekly.tuesday"),
        t("schedules.weekly.wednesday"), t("schedules.weekly.thursday"), t("schedules.weekly.friday"),
        t("schedules.weekly.saturday"), t("schedules.weekly.sunday")];

    const {updateSchedules} = useContext(SchedulesContext);

    const [name, setName] = useState("");
    const [frequency, setFrequency] = useState("monthly");
    const [time, setTime] = useState("1");

    const create = async () => {
        if (!name || !frequency || !time) return;
        let serverTime = time;
        if (frequency === "daily") serverTime = time.replace(":", "");
        await putRequest("schedules/", {name, frequency, time: serverTime});
        updateSchedules();
        setOpen(false);
    }

    const updateMinute = (minute) => {
        setTime(minute < 0 ? 59 : minute > 59 ? 0 : minute);
    }

    useEffect(() => {
        setTime((frequency === "monthly" || frequency === "weekly") ? "1" : frequency === "daily" ? "00:00" : "0");
    }, [frequency]);

    useEffect(() => {
        setName("");
        setFrequency("monthly");
        setTime("1");
    }, [open]);

    return (
        <Dialog open={open} onClose={() => setOpen(false)}>
            <DialogTitle>{t("schedules.create_title")}</DialogTitle>
            <DialogContent sx={{maxWidth: "20rem"}}>
                <TextField autoFocus label={t("schedules.name")} fullWidth sx={{mb: 2, mt: 1}}
                           value={name} onChange={(e) => setName(e.target.value)}/>
                <Stack direction="row" gap={2}>
                    <Select value={frequency} onChange={(e) => setFrequency(e.target.value)}
                            defaultValue="monthly" fullWidth>
                        <MenuItem value="monthly">{t("schedules.frequencies.monthly")}</MenuItem>
                        <MenuItem value="weekly">{t("schedules.frequencies.weekly")}</MenuItem>
                        <MenuItem value="daily">{t("schedules.frequencies.daily")}</MenuItem>
                        <MenuItem value="hourly">{t("schedules.frequencies.hourly")}</MenuItem>
                    </Select>
                    {frequency === "monthly" &&
                        <Select value={time} onChange={(e) => setTime(e.target.value)}>
                            {Array.from(Array(31).keys()).map((day) => <MenuItem value={day + 1}
                                                                                 key={day}>{day + 1}.</MenuItem>)}
                        </Select>}
                    {frequency === "weekly" && <Select value={time} onChange={(e) => setTime(e.target.value)} fullWidth>
                        {days().map((day, index) => <MenuItem value={index + 1} key={index}>{day}</MenuItem>)}
                    </Select>}
                    {frequency === "daily" && <TextField type="time" fullWidth value={time}
                                                         onChange={(e) => setTime(e.target.value)}/>}

                    {frequency === "hourly" && <TextField type="number" value={time} sx={{width: "6rem"}}
                                                          onChange={(e) => updateMinute(e.target.value)}/>}
                </Stack>
            </DialogContent>
            <DialogActions>
                <Button onClick={() => setOpen(false)}>{t("action.cancel")}</Button>
                <Button type="submit" onClick={create} disabled={!name || !frequency || !time}>{t("action.add")}</Button>
            </DialogActions>
        </Dialog>
    )
}