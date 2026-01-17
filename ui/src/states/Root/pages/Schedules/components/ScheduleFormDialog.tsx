import * as React from "react";
import { t } from "i18next";
import { ClockIcon, CalendarIcon, RepeatIcon } from "@phosphor-icons/react";
import { CreateScheduleRequest, Schedule, ScheduleInterval } from "@/types/schedule";
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
import { TimePicker, MinutePicker } from "@/components/ui/time-picker";
import { cn } from "@/lib/utils";

const DAYS = [
    { value: 0, labelKey: "schedules.days.sunday_short" },
    { value: 1, labelKey: "schedules.days.monday_short" },
    { value: 2, labelKey: "schedules.days.tuesday_short" },
    { value: 3, labelKey: "schedules.days.wednesday_short" },
    { value: 4, labelKey: "schedules.days.thursday_short" },
    { value: 5, labelKey: "schedules.days.friday_short" },
    { value: 6, labelKey: "schedules.days.saturday_short" },
];

const INTERVAL_OPTIONS = [
    { value: "HOURLY" as ScheduleInterval, icon: RepeatIcon, labelKey: "schedules.interval.hourly" },
    { value: "DAILY" as ScheduleInterval, icon: ClockIcon, labelKey: "schedules.interval.daily" },
    { value: "WEEKLY" as ScheduleInterval, icon: CalendarIcon, labelKey: "schedules.interval.weekly" },
];

interface ScheduleFormDialogProps {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    onSubmit: (data: CreateScheduleRequest) => Promise<void>;
    schedule?: Schedule;
}

const ScheduleFormDialog = ({ open, onOpenChange, onSubmit, schedule }: ScheduleFormDialogProps) => {
    const isEdit = !!schedule;
    
    const [name, setName] = React.useState(schedule?.name ?? "");
    const [interval, setInterval] = React.useState<ScheduleInterval>(schedule?.interval ?? "DAILY");
    const [intervalValue, setIntervalValue] = React.useState(schedule?.intervalValue ?? 0);
    const [timeValue, setTimeValue] = React.useState(schedule?.timeValue ?? 0);
    const [isSubmitting, setIsSubmitting] = React.useState(false);

    React.useEffect(() => {
        if (schedule) {
            setName(schedule.name);
            setInterval(schedule.interval);
            setIntervalValue(schedule.intervalValue);
            setTimeValue(schedule.timeValue);
        } else {
            setName("");
            setInterval("DAILY");
            setIntervalValue(0);
            setTimeValue(0);
        }
    }, [schedule, open]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsSubmitting(true);
        
        try {
            await onSubmit({
                name,
                interval,
                intervalValue,
                timeValue: interval === "HOURLY" ? 0 : timeValue
            });
            if (!isEdit) {
                setName("");
                setInterval("DAILY");
                setIntervalValue(0);
                setTimeValue(0);
            }
        } finally {
            setIsSubmitting(false);
        }
    };

    const handleIntervalChange = (newInterval: ScheduleInterval) => {
        setInterval(newInterval);
        setIntervalValue(0);
        setTimeValue(0);
    };

    const renderIntervalInputs = () => {
        switch (interval) {
            case "HOURLY":
                return (
                    <div className="space-y-3">
                        <Label className="text-sm font-medium">{t("schedules.form.run_at_minute")}</Label>
                        <MinutePicker value={intervalValue} onChange={setIntervalValue} />
                        <p className="text-xs text-muted-foreground">{t("schedules.form.minute_hint")}</p>
                    </div>
                );
            case "DAILY":
                return (
                    <div className="space-y-3">
                        <Label className="text-sm font-medium">{t("schedules.form.run_at_time")}</Label>
                        <TimePicker
                            value={{ hour: intervalValue, minute: timeValue }}
                            onChange={(v) => {
                                setIntervalValue(v.hour);
                                setTimeValue(v.minute);
                            }}
                        />
                    </div>
                );
            case "WEEKLY":
                return (
                    <div className="space-y-4">
                        <div className="space-y-3">
                            <Label className="text-sm font-medium">{t("schedules.form.day")}</Label>
                            <div className="grid grid-cols-7 gap-1">
                                {DAYS.map((day) => (
                                    <Button
                                        key={day.value}
                                        type="button"
                                        variant={intervalValue === day.value ? "default" : "outline"}
                                        size="sm"
                                        className={cn(
                                            "h-9 w-full rounded-lg text-xs font-medium",
                                            intervalValue === day.value && "ring-2 ring-offset-1 ring-primary"
                                        )}
                                        onClick={() => setIntervalValue(day.value)}
                                    >
                                        {t(day.labelKey)}
                                    </Button>
                                ))}
                            </div>
                        </div>
                        <div className="space-y-3">
                            <Label className="text-sm font-medium">{t("schedules.form.run_at_time")}</Label>
                            <TimePicker
                                value={{ hour: Math.floor(timeValue / 60), minute: timeValue % 60 }}
                                onChange={(v) => setTimeValue(v.hour * 60 + v.minute)}
                            />
                        </div>
                    </div>
                );
        }
    };

    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent className="sm:max-w-[425px] rounded-xl">
                <form onSubmit={handleSubmit}>
                    <DialogHeader>
                        <DialogTitle className="text-lg">
                            {isEdit ? t("schedules.edit") : t("schedules.create")}
                        </DialogTitle>
                        <DialogDescription>
                            {isEdit ? t("schedules.edit_description") : t("schedules.create_description")}
                        </DialogDescription>
                    </DialogHeader>
                    <div className="grid gap-4 py-6">
                        <div className="grid gap-2">
                            <Label htmlFor="name">{t("schedules.form.name")}</Label>
                            <Input
                                id="name"
                                value={name}
                                onChange={(e) => setName(e.target.value)}
                                placeholder={t("schedules.form.name_placeholder")}
                                className="rounded-xl"
                                required
                            />
                        </div>
                        <div className="grid gap-2">
                            <Label>{t("schedules.form.interval")}</Label>
                            <div className="grid grid-cols-3 gap-2">
                                {INTERVAL_OPTIONS.map((option) => (
                                    <Button
                                        key={option.value}
                                        type="button"
                                        variant={interval === option.value ? "default" : "outline"}
                                        className={cn(
                                            "h-auto flex-col gap-1.5 py-3 rounded-xl",
                                            interval === option.value && "ring-2 ring-offset-2 ring-primary"
                                        )}
                                        onClick={() => handleIntervalChange(option.value)}
                                    >
                                        <option.icon className="h-5 w-5" weight={interval === option.value ? "fill" : "regular"} />
                                        <span className="text-xs font-medium">{t(option.labelKey)}</span>
                                    </Button>
                                ))}
                            </div>
                        </div>
                        {renderIntervalInputs()}
                    </div>
                    <DialogFooter>
                        <Button 
                            type="submit" 
                            disabled={!name.trim() || isSubmitting} 
                            size="lg" 
                            className="w-full h-12 rounded-xl text-base"
                        >
                            {isEdit ? t("action.save") : t("action.create")}
                        </Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    );
};

export default ScheduleFormDialog;
