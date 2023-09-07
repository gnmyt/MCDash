import {Button, Dialog, DialogActions, DialogContent, DialogTitle, MenuItem, Select, Stack, TextField} from "@mui/material";
import {t} from "i18next";
import {patchRequest, putRequest} from "@/common/utils/RequestUtil.js";
import {useContext, useEffect, useState} from "react";
import {SchedulesContext} from "@/states/Root/pages/Scheduler/contexts/Schedules";
import {getDays} from "./utils.js";

export const CreateScheduleDialog = ({open, setOpen, edit, currentName, execution}) => {
    const {updateSchedules} = useContext(SchedulesContext);

    const [name, setName] = useState("");
    const [frequency, setFrequency] = useState("monthly");
    const [time, setTime] = useState("");

    const create = async () => {
        if (!name || !frequency || !time) return;
        let serverTime = time;
        if (frequency === "daily") serverTime = time.replace(":", "");
        await putRequest("schedules/", {name, frequency, time: serverTime});
        updateSchedules();
        setOpen(false);
    }

    const update = async () => {
        if (!name || !frequency || !time) return;
        if (name !== currentName) await patchRequest("schedules/name", {name: currentName, new_name: name});
        let serverTime = time;
        if (frequency === "daily") serverTime = time.replace(":", "");
        await patchRequest("schedules/execution", {name, frequency, time: serverTime});

        updateSchedules();
        setOpen(false);
    }

    const updateMinute = (minute) => setTime(minute < 0 ? 59 : minute > 59 ? 0 : minute);

    const updateFrequency = (frequency) => {
        setFrequency(frequency);
        setTime((frequency === "monthly" || frequency === "weekly") ? "1" : frequency === "daily" ? "00:00" : "0");
    }

    useEffect(() => {
        setName(currentName);
        setFrequency(execution?.frequency ? execution.frequency.toLowerCase() : "monthly");

        setTime(execution?.frequency === "DAILY" ? (execution.time.substring(0, 2) + ":" + execution.time.substring(2, 4))
            : execution?.time ? parseInt(execution.time) : "1");
    }, [open]);

    return (
        <Dialog open={open} onClose={() => setOpen(false)}>
            <DialogTitle>{edit ? t("schedules.edit_title") : t("schedules.create_title")}</DialogTitle>
            <DialogContent sx={{maxWidth: "20rem"}}>
                <TextField autoFocus label={t("schedules.name")} fullWidth sx={{mb: 2, mt: 1}}
                           value={name} onChange={(e) => setName(e.target.value)}/>
                <Stack direction="row" gap={2}>
                    <Select value={frequency} onChange={(e) => updateFrequency(e.target.value)}
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
                        {getDays().map((day, index) => <MenuItem value={index + 1} key={index}>{day}</MenuItem>)}
                    </Select>}
                    {frequency === "daily" && <TextField type="time" fullWidth value={time}
                                                         onChange={(e) => setTime(e.target.value)}/>}

                    {frequency === "hourly" && <TextField type="number" value={time} sx={{width: "6rem"}}
                                                          onChange={(e) => updateMinute(e.target.value)}/>}
                </Stack>
            </DialogContent>
            <DialogActions>
                <Button onClick={() => setOpen(false)}>{t("action.cancel")}</Button>
                <Button type="submit" onClick={edit ? update : create} disabled={!name || !frequency || !time}>
                    {edit ? t("action.save") : t("action.add")}
                </Button>
            </DialogActions>
        </Dialog>
    )
}